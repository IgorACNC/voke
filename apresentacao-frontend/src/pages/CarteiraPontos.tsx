import { useEffect, useMemo, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import {
  consultarSaldoPontos,
  consultarExtratoPontos,
  type TransacaoPontos,
} from '../services/pontosService'
import './Social.css'
import './CarteiraPontos.css'

type Aba = 'CATALOGO' | 'HISTORICO' | 'RESGATES'

const TIPO_LABEL: Record<string, string> = {
  GANHO_PRESENCA: 'Ganho por presença',
  RESGATE_RECOMPENSA: 'Resgate de recompensa',
  EXPIRACAO: 'Pontos expirados',
}

export default function CarteiraPontos() {
  const navigate = useNavigate()
  const { usuario } = useAuth()

  const [saldo, setSaldo] = useState<number>(0)
  const [extrato, setExtrato] = useState<TransacaoPontos[]>([])
  const [aba, setAba] = useState<Aba>('HISTORICO')
  const [carregando, setCarregando] = useState(false)
  const [erro, setErro] = useState('')

  useEffect(() => {
    if (!usuario) return
    carregar()
  }, [usuario?.id])

  async function carregar() {
    setErro('')
    setCarregando(true)
    try {
      const [s, e] = await Promise.all([
        consultarSaldoPontos(usuario!.id).catch(() => 0),
        consultarExtratoPontos(usuario!.id).catch(() => []),
      ])
      setSaldo(s)
      setExtrato(e)
    } catch {
      setErro('Não foi possível carregar os pontos.')
    } finally {
      setCarregando(false)
    }
  }

  const totalGanho = useMemo(
    () => extrato.filter((t) => t.direcao === 'ENTRADA').reduce((acc, t) => acc + t.pontos, 0),
    [extrato],
  )
  const totalGasto = useMemo(
    () => extrato.filter((t) => t.direcao === 'SAIDA').reduce((acc, t) => acc + t.pontos, 0),
    [extrato],
  )

  const resgates = useMemo(() => extrato.filter((t) => t.tipo === 'RESGATE_RECOMPENSA'), [extrato])

  if (!usuario) return null

  return (
    <div className="social-bg">
      <header className="social-header">
        <button className="social-voltar" onClick={() => navigate('/dashboard')}>Voltar</button>
        <span className="social-logo">Voke</span>
        <div style={{ width: 90 }} />
      </header>

      <main className="social-main">
        <section className="social-title">
          <h1>Carteira de Pontos</h1>
          <p>Acompanhe seus pontos ganhos por presença e resgate recompensas.</p>
        </section>

        {erro && <p className="social-msg-erro">{erro}</p>}

        {/* Resumo de saldo */}
        <div className="pontos-resumo">
          <div className="pontos-resumo-item">
            <span className="pontos-resumo-label">SALDO ATUAL</span>
            <strong className="pontos-resumo-valor">{saldo} pts</strong>
          </div>
          <div className="pontos-resumo-item">
            <span className="pontos-resumo-label">TOTAL GANHO</span>
            <strong className="pontos-resumo-valor secundario">{totalGanho} pts</strong>
          </div>
          <div className="pontos-resumo-item">
            <span className="pontos-resumo-label">TOTAL GASTO</span>
            <strong className="pontos-resumo-valor secundario">{totalGasto} pts</strong>
          </div>
        </div>

        <p className="pontos-info">
          <strong>Como ganhar pontos:</strong> 30% do valor pago em ingressos vira pontos após o check-in
          no evento encerrado.
        </p>

        {/* Tabs */}
        <div className="pontos-tabs">
          <button
            className={`pontos-tab ${aba === 'CATALOGO' ? 'ativo' : ''}`}
            onClick={() => navigate('/catalogo-recompensas')}
          >
            CATÁLOGO
          </button>
          <button
            className={`pontos-tab ${aba === 'HISTORICO' ? 'ativo' : ''}`}
            onClick={() => setAba('HISTORICO')}
          >
            HISTÓRICO
          </button>
          <button
            className={`pontos-tab ${aba === 'RESGATES' ? 'ativo' : ''}`}
            onClick={() => setAba('RESGATES')}
          >
            RESGATES
          </button>
        </div>

        {/* Conteúdo da aba */}
        {carregando ? (
          <p style={{ color: '#9ca3af', textAlign: 'center', padding: '2rem' }}>Carregando...</p>
        ) : aba === 'HISTORICO' ? (
          <div className="pontos-lista">
            {extrato.length === 0 ? (
              <p className="social-vazio">Nenhuma movimentação ainda.</p>
            ) : (
              extrato.map((t) => (
                <div key={t.id} className={`pontos-transacao ${t.direcao === 'ENTRADA' ? 'entrada' : 'saida'}`}>
                  <div className="pontos-transacao-info">
                    <span className="pontos-transacao-tipo">{TIPO_LABEL[t.tipo] ?? t.tipo}</span>
                    <span className="pontos-transacao-desc">{t.descricao}</span>
                    <span className="pontos-transacao-data">
                      {new Date(t.dataHora).toLocaleString('pt-BR')}
                    </span>
                  </div>
                  <span className="pontos-transacao-valor">
                    {t.direcao === 'ENTRADA' ? '+' : '-'}{t.pontos} pts
                  </span>
                </div>
              ))
            )}
          </div>
        ) : (
          <div className="pontos-lista">
            {resgates.length === 0 ? (
              <p className="social-vazio">Nenhum resgate realizado ainda.</p>
            ) : (
              resgates.map((t) => (
                <div key={t.id} className="pontos-transacao saida">
                  <div className="pontos-transacao-info">
                    <span className="pontos-transacao-tipo">Resgate</span>
                    <span className="pontos-transacao-desc">{t.descricao}</span>
                    <span className="pontos-transacao-data">
                      {new Date(t.dataHora).toLocaleString('pt-BR')}
                    </span>
                  </div>
                  <span className="pontos-transacao-valor">-{t.pontos} pts</span>
                </div>
              ))
            )}
          </div>
        )}
      </main>
    </div>
  )
}
