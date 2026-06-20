import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import {
  buscarParceiroPorParticipante,
  ATIVIDADES_LABELS,
  type Parceiro,
} from '../services/parceiroService'
import './Social.css'
import './Parceiros.css'

export default function PainelParceiro() {
  const navigate = useNavigate()
  const { usuario } = useAuth()

  const [parceiros, setParceiros] = useState<Parceiro[]>([])
  const [saldos, setSaldos] = useState<Record<string, number>>({})
  const [comissoes, setComissoes] = useState<Record<string, any[]>>({})
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState('')

  useEffect(() => {
    if (!usuario) return
    buscarParceiroPorParticipante(usuario.id)
      .then(async (lista) => {
        setParceiros(lista)
        const novosSaldos: Record<string, number> = {}
        const novasComissoes: Record<string, any[]> = {}
        for (const p of lista) {
          try {
            const { consultarSaldoComissoes, consultarComissoes } = await import('../services/parceiroService')
            novosSaldos[p.id] = await consultarSaldoComissoes(p.id)
            novasComissoes[p.id] = await consultarComissoes(p.id)
          } catch (e) {
            console.error('Erro ao carregar comissoes para parceiro', p.id, e)
            novosSaldos[p.id] = 0
            novasComissoes[p.id] = []
          }
        }
        setSaldos(novosSaldos)
        setComissoes(novasComissoes)
      })
      .catch(() => setErro('Não foi possível carregar seus dados de parceiro.'))
      .finally(() => setCarregando(false))
  }, [usuario?.id])

  if (!usuario) return null

  return (
    <div className="social-bg">
      <header className="social-header">
        <button className="social-voltar" onClick={() => navigate('/dashboard')}>Voltar</button>
        <span className="social-logo">Voke</span>
        <div style={{ width: 90 }} />
      </header>

      <main className="social-main social-main-curto">
        <section className="social-title">
          <h1>Painel do Parceiro</h1>
          <p>Acompanhe suas atividades e comissões como parceiro.</p>
        </section>

        {erro && <p className="social-msg-erro">{erro}</p>}

        {carregando ? (
          <p style={{ color: '#9ca3af', textAlign: 'center', padding: '2rem' }}>Carregando...</p>
        ) : parceiros.length === 0 ? (
          <div className="social-card">
            <p className="social-vazio">Você ainda não é parceiro de nenhum organizador.</p>
            <p style={{ color: '#6b7280', fontSize: '0.85rem', marginTop: '0.5rem' }}>
              Para se tornar parceiro, um organizador precisa cadastrá-lo. Você deve ter participado de pelo menos 5 eventos dele.
            </p>
          </div>
        ) : (
          parceiros.map((p) => {
            const saldo = saldos[p.id] || 0
            const historico = comissoes[p.id] || []
            const vendas = historico.filter(c => c.status === 'CREDITADA').length

            return (
              <div key={p.id} className="social-card" style={{ marginBottom: '1.25rem' }}>
                <h2>Parceria com organizador</h2>
                <span style={{ color: '#6b7280', fontSize: '0.8rem' }}>
                  Org. ID: {p.organizadorId.slice(0, 8)}...
                </span>

                <div className="parceiro-atividades" style={{ marginTop: '0.75rem' }}>
                  {p.atividades.map((a) => (
                    <span key={a} className="parceiro-atividade">{ATIVIDADES_LABELS[a]}</span>
                  ))}
                </div>

                <div className="parceiro-stats">
                  <div className="parceiro-stat">
                    <span className="parceiro-stat-valor" style={{ color: '#10b981' }}>
                      {new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(saldo)}
                    </span>
                    <span className="parceiro-stat-label">Comissões Recebidas</span>
                  </div>
                  <div className="parceiro-stat">
                    <span className="parceiro-stat-valor">{vendas}</span>
                    <span className="parceiro-stat-label">Ingressos Vendidos</span>
                  </div>
                </div>

                {historico.length > 0 && (
                  <div style={{ marginTop: '1.5rem' }}>
                    <h3 style={{ fontSize: '1rem', color: '#374151', marginBottom: '0.5rem' }}>Últimas transações</h3>
                    <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
                      {historico.slice(0, 5).map(c => (
                        <div key={c.id} style={{ display: 'flex', justifyContent: 'space-between', padding: '0.5rem', backgroundColor: '#f9fafb', borderRadius: '4px', fontSize: '0.85rem' }}>
                          <span style={{ color: c.status === 'ESTORNADA' ? '#ef4444' : '#111827' }}>
                            {c.status === 'ESTORNADA' ? 'Estorno' : 'Comissão'} (Cupom {c.cupomId.slice(0, 6)}...)
                          </span>
                          <span style={{ fontWeight: 600, color: c.status === 'ESTORNADA' ? '#ef4444' : '#10b981' }}>
                            {c.status === 'ESTORNADA' ? '-' : '+'}{new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(c.valor)}
                          </span>
                        </div>
                      ))}
                    </div>
                  </div>
                )}
              </div>
            )
          })
        )}
      </main>
    </div>
  )
}