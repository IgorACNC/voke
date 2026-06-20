import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { listarMinhasInscricoes, cancelarInscricao, estimarEstorno, type Inscricao } from '../services/inscricaoService'
import { realizarCheckIn } from '../services/pontosService'
import './Social.css'
import './MinhasInscricoes.css'

type StatusFiltro = 'TODOS' | 'CONFIRMADA' | 'CHECK_IN_REALIZADO' | 'CANCELADA' | 'PENDENTE'

const FILTROS: { valor: StatusFiltro; label: string }[] = [
  { valor: 'TODOS', label: 'Todos' },
  { valor: 'CONFIRMADA', label: 'Confirmada' },
  { valor: 'CHECK_IN_REALIZADO', label: 'Check-in' },
  { valor: 'CANCELADA', label: 'Cancelada' },
  { valor: 'PENDENTE', label: 'Pendente' },
]

const STATUS_CONFIG: Record<string, { label: string; cls: string }> = {
  CONFIRMADA:          { label: 'Confirmada',   cls: 'badge-confirmada' },
  CHECK_IN_REALIZADO:  { label: 'Check-in feito', cls: 'badge-checkin' },
  CANCELADA:           { label: 'Cancelada',    cls: 'badge-cancelada' },
  PENDENTE:            { label: 'Pendente',     cls: 'badge-pendente' },
}

function calcularPercentualEstorno(dataInicioEvento: string): number {
  const diffHours = (new Date(dataInicioEvento).getTime() - Date.now()) / 3_600_000
  if (diffHours >= 168) return 1      // >= 7 dias
  if (diffHours >= 48) return 0.5     // >= 48 horas
  if (diffHours > 0) return 0.25      // evento ainda não começou
  return 0                            // evento já passou
}

function eventoFinalizado(dataFim: string) {
  return new Date(dataFim).getTime() < Date.now()
}

export default function MinhasInscricoes() {
  const navigate = useNavigate()
  const { usuario } = useAuth()

  const [inscricoes, setInscricoes] = useState<Inscricao[]>([])
  const [carregando, setCarregando] = useState(false)
  const [erro, setErro] = useState('')
  const [mensagem, setMensagem] = useState('')
  const [filtro, setFiltro] = useState<StatusFiltro>('TODOS')
  const [cancelando, setCancelando] = useState<string | null>(null)
  const [fazendoCheckIn, setFazendoCheckIn] = useState<string | null>(null)

  useEffect(() => {
    if (!usuario) return
    carregar()
  }, [usuario?.id])

  async function carregar() {
    setErro('')
    setCarregando(true)
    try {
      setInscricoes(await listarMinhasInscricoes(usuario!.id))
    } catch {
      setErro('Não foi possível carregar suas inscrições.')
    } finally {
      setCarregando(false)
    }
  }

  async function handleCheckIn(id: string) {
    setErro(''); setMensagem('')
    setFazendoCheckIn(id)
    try {
      const r = await realizarCheckIn(id)
      setMensagem(r.mensagem)
      await carregar()
    } catch (e: unknown) {
      setErro((e as any)?.response?.data?.mensagem ?? 'Não foi possível fazer check-in.')
    } finally {
      setFazendoCheckIn(null)
    }
  }

  async function handleCancelar(id: string) {
    const inscr = inscricoes.find((x) => x.id === id)
    if (!inscr) return
    setErro('')

    let valorEstorno = 0
    try {
      const resp = await estimarEstorno(id)
      valorEstorno = Math.round(resp.valor * 100) / 100
    } catch {
      const pct = calcularPercentualEstorno(inscr.evento.dataHoraInicio)
      valorEstorno = Math.round(inscr.valorPago * pct * 100) / 100
    }

    if (!confirm(`Cancelar inscrição em "${inscr.evento.nome}"?\nEstorno estimado: R$ ${valorEstorno.toFixed(2)}`)) return

    setCancelando(id)
    try {
      await cancelarInscricao(id)
      setMensagem('Inscrição cancelada com sucesso.')
      await carregar()
    } catch (e: unknown) {
      setErro((e as any)?.response?.data?.mensagem ?? 'Não foi possível cancelar a inscrição.')
    } finally {
      setCancelando(null)
    }
  }

  if (!usuario) return null

  const visíveis = filtro === 'TODOS' ? inscricoes : inscricoes.filter((i) => i.status === filtro)

  const contagem = (s: StatusFiltro) =>
    s === 'TODOS' ? inscricoes.length : inscricoes.filter((i) => i.status === s).length

  return (
    <div className="social-bg">
      <header className="social-header">
        <button className="social-voltar" onClick={() => navigate('/dashboard')}>Voltar</button>
        <span className="social-logo">Voke</span>
        <div style={{ width: 90 }} />
      </header>

      <main className="social-main">
        <section className="social-title">
          <h1>Minhas Inscrições</h1>
          <p>Acompanhe seus ingressos, faça check-in e cancele quando necessário.</p>
        </section>

        {mensagem && <p className="social-msg-sucesso">{mensagem}</p>}
        {erro && <p className="social-msg-erro">{erro}</p>}

        {/* Filtros */}
        <div className="inscricao-filtros">
          {FILTROS.map((f) => {
            const n = contagem(f.valor)
            if (f.valor !== 'TODOS' && n === 0) return null
            return (
              <button
                key={f.valor}
                className={`inscricao-filtro-btn inscricao-filtro-${f.valor.toLowerCase()} ${filtro === f.valor ? 'ativo' : ''}`}
                onClick={() => setFiltro(f.valor)}
              >
                {f.label}
                <span className="inscricao-filtro-count">{n}</span>
              </button>
            )
          })}
        </div>

        {carregando ? (
          <p style={{ color: '#9ca3af', textAlign: 'center', padding: '2rem' }}>Carregando...</p>
        ) : visíveis.length === 0 ? (
          <div className="inscricao-vazio">
            <span>🎫</span>
            <p>{filtro === 'TODOS' ? 'Você ainda não tem inscrições.' : 'Nenhuma inscrição com esse status.'}</p>
            {filtro === 'TODOS' && (
              <button className="inscricao-btn-explorar" onClick={() => navigate('/explorar-eventos')}>
                Explorar eventos
              </button>
            )}
          </div>
        ) : (
          <div className="inscricao-lista">
            {visíveis.map((i) => {
              const cfg = STATUS_CONFIG[i.status] ?? { label: i.status, cls: 'badge-pendente' }
              const finalizado = eventoFinalizado(i.evento.dataHoraFim)
              const podeAvaliar = (i.status === 'CONFIRMADA' || i.status === 'CHECK_IN_REALIZADO') && finalizado
              const podeCancelar = i.status === 'CONFIRMADA'

              return (
                <div key={i.id} className="inscricao-card">
                  <div className="inscricao-card-topo">
                    <div className="inscricao-card-info">
                      <div className="inscricao-card-nome-linha">
                        <h3 className="inscricao-card-nome">{i.evento.nome}</h3>
                        <span className={`inscricao-badge ${cfg.cls}`}>{cfg.label}</span>
                      </div>
                      <p className="inscricao-card-local">📍 {i.evento.local}</p>
                      <p className="inscricao-card-data">
                        🗓 {new Date(i.evento.dataHoraInicio).toLocaleString('pt-BR', {
                          day: '2-digit', month: 'short', year: 'numeric',
                          hour: '2-digit', minute: '2-digit',
                        })}
                      </p>
                    </div>
                    <div className="inscricao-card-valor">
                      <span className="inscricao-card-preco">
                        {Number(i.valorPago).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}
                      </span>
                      {i.loteNumero && <span className="inscricao-card-lote">Lote {i.loteNumero}</span>}
                    </div>
                  </div>

                  <div className="inscricao-card-acoes">
                    <button className="inscricao-btn-ver" onClick={() => navigate(`/eventos/${i.evento.id}`)}>
                      Ver evento
                    </button>
                    {(() => {
                      const eventoComecou = new Date(i.evento.dataHoraInicio).getTime() <= Date.now()
                      const eventoEncerrou = new Date(i.evento.dataHoraFim).getTime() <= Date.now()
                      const podeCheckIn = i.status === 'CONFIRMADA' && eventoComecou
                      const podeResgatar = i.status === 'CHECK_IN_REALIZADO' && eventoEncerrou && !i.pontosCreditados
                      if (!podeCheckIn && !podeResgatar) return null
                      const label = podeResgatar ? '🏆 Receber pontos' : '✓ Check-in'
                      return (
                        <button
                          className="inscricao-btn-ver"
                          onClick={() => handleCheckIn(i.id)}
                          disabled={fazendoCheckIn === i.id}
                        >
                          {fazendoCheckIn === i.id ? 'Aguarde...' : label}
                        </button>
                      )
                    })()}
                    {podeAvaliar && (
                      <button
                        className="inscricao-btn-avaliar"
                        onClick={() => navigate(`/avaliacoes/${i.evento.id}`, { state: { evento: i.evento } })}
                      >
                        ⭐ Avaliar
                      </button>
                    )}
                    {podeCancelar && (
                      <button
                        className="inscricao-btn-cancelar"
                        onClick={() => handleCancelar(i.id)}
                        disabled={cancelando === i.id}
                      >
                        {cancelando === i.id ? 'Cancelando...' : 'Cancelar inscrição'}
                      </button>
                    )}
                  </div>
                </div>
              )
            })}
          </div>
        )}
      </main>
    </div>
  )
}
