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
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState('')

  useEffect(() => {
    if (!usuario) return
    buscarParceiroPorParticipante(usuario.id)
      .then(setParceiros)
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
          <p>Acompanhe suas atividades como parceiro.</p>
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
          parceiros.map((p) => (
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
                  <span className="parceiro-stat-valor">R$ 0,00</span>
                  <span className="parceiro-stat-label">Comissões</span>
                </div>
                <div className="parceiro-stat">
                  <span className="parceiro-stat-valor">0</span>
                  <span className="parceiro-stat-label">Vendas</span>
                </div>
              </div>

              <div className="parceiro-breve">
                💡 Funcionalidade de comissões e vendas em breve.
              </div>

              <div className="social-acoes" style={{ marginTop: '1rem' }}>
                <button onClick={() => navigate('/cupons')}>🎟️ Ver Cupons</button>
              </div>
            </div>
          ))
        )}
      </main>
    </div>
  )
}
