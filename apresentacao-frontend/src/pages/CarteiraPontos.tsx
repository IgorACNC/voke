import { useEffect, useMemo, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import {
  consultarSaldoPontos,
  consultarExtratoPontos,
  type TransacaoPontos,
} from '../services/pontosService'
import { listarMeusCupons, type MeuCupom } from '../services/recompensaService'
import './Social.css'
import './CarteiraPontos.css'

type Aba = 'CATALOGO' | 'HISTORICO' | 'RESGATES' | 'CUPONS'

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
  const [meusCupons, setMeusCupons] = useState<MeuCupom[]>([])
  const [aba, setAba] = useState<Aba>('HISTORICO')
  const [carregando, setCarregando] = useState(false)
  const [erro, setErro] = useState('')
  const [copiado, setCopiado] = useState<string | null>(null)

  useEffect(() => {
    if (!usuario) return
    carregar()
  }, [usuario?.id])

  async function carregar() {
    setErro('')
    setCarregando(true)
    try {
      const [s, e, c] = await Promise.all([
        consultarSaldoPontos(usuario!.id).catch(() => 0),
        consultarExtratoPontos(usuario!.id).catch(() => []),
        listarMeusCupons(usuario!.id).catch(() => [] as MeuCupom[]),
      ])
      setSaldo(s)
      setExtrato(e)
      setMeusCupons(c)
    } catch {
      setErro('Não foi possível carregar os pontos.')
    } finally {
      setCarregando(false)
    }
  }

  async function copiarCodigo(codigo: string) {
    try {
      await navigator.clipboard.writeText(codigo)
      setCopiado(codigo)
      setTimeout(() => setCopiado((c) => (c === codigo ? null : c)), 1500)
    } catch {
      /* ignore */
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
          <button
            className={`pontos-tab ${aba === 'CUPONS' ? 'ativo' : ''}`}
            onClick={() => setAba('CUPONS')}
          >
            MINHAS RECOMPENSAS
          </button>
        </div>

        {/* Conteúdo da aba */}
        {carregando ? (
          <p style={{ color: '#9ca3af', textAlign: 'center', padding: '2rem' }}>Carregando...</p>
        ) : aba === 'CUPONS' ? (
          <div className="pontos-lista">
            {meusCupons.length === 0 ? (
              <p className="social-vazio">
                Você ainda não resgatou nenhum cupom. Vá ao{' '}
                <a href="#" onClick={(e) => { e.preventDefault(); navigate('/catalogo-recompensas') }}>
                  catálogo
                </a>{' '}
                para resgatar.
              </p>
            ) : (
              meusCupons.map((c) => {
                const indisponivel = c.utilizado || !c.ativo
                return (
                  <div
                    key={c.id}
                    className="pontos-transacao"
                    style={{ opacity: indisponivel ? 0.6 : 1, alignItems: 'flex-start' }}
                  >
                    <div className="pontos-transacao-info" style={{ flex: 1 }}>
                      <span className="pontos-transacao-tipo">
                        {c.recompensaNome}
                        {c.global ? ' · Global' : ' · Do organizador'}
                      </span>
                      <span
                        className="pontos-transacao-desc"
                        style={{
                          fontFamily: 'monospace',
                          fontSize: '1rem',
                          letterSpacing: '0.05em',
                          color: '#111827',
                          fontWeight: 700,
                        }}
                      >
                        {c.codigoCupom}
                      </span>
                      <span className="pontos-transacao-data">
                        Resgatado em {new Date(c.dataResgate).toLocaleString('pt-BR')}
                        {c.valor != null && ` · R$ ${c.valor.toFixed(2)} de desconto`}
                      </span>
                    </div>
                    <div style={{ display: 'flex', flexDirection: 'column', gap: 4, alignItems: 'flex-end' }}>
                      <span
                        style={{
                          fontSize: '0.7rem',
                          padding: '2px 10px',
                          borderRadius: 12,
                          background: c.utilizado ? '#fee2e2' : !c.ativo ? '#f3f4f6' : '#dcfce7',
                          color: c.utilizado ? '#b91c1c' : !c.ativo ? '#6b7280' : '#15803d',
                          fontWeight: 700,
                        }}
                      >
                        {c.utilizado ? 'UTILIZADO' : !c.ativo ? 'INATIVO' : 'DISPONÍVEL'}
                      </span>
                      {!indisponivel && (
                        <button
                          type="button"
                          onClick={() => copiarCodigo(c.codigoCupom)}
                          style={{
                            fontSize: '0.75rem',
                            padding: '4px 10px',
                            borderRadius: 8,
                            border: '1px solid #d1d5db',
                            background: copiado === c.codigoCupom ? '#dcfce7' : '#fff',
                            cursor: 'pointer',
                          }}
                        >
                          {copiado === c.codigoCupom ? 'Copiado!' : 'Copiar código'}
                        </button>
                      )}
                    </div>
                  </div>
                )
              })
            )}
          </div>
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
