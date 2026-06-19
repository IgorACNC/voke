import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { listarMeusEventos, cancelarEvento, type Evento } from '../services/eventoService'
import {
  listarAvaliacoesEvento,
  resumoAvaliacoesEvento,
  type AvaliacaoPublica,
  type ResumoAvaliacao,
} from '../services/avaliacaoService'
import './MeusEventos.css'

function estrelas(media: number) {
  const cheias = Math.round(media)
  return '★'.repeat(cheias) + '☆'.repeat(5 - cheias)
}

function statusLabel(s: string) {
  if (s === 'ATIVO') return 'ATIVO'
  if (s === 'CANCELADO') return 'CANCELADO'
  if (s === 'ENCERRADO') return 'ENCERRADO'
  return s
}

export default function MeusEventos() {
  const { usuario, sair } = useAuth()
  const navigate = useNavigate()
  const [eventos, setEventos] = useState<Evento[]>([])
  const [carregando, setCarregando] = useState(false)
  const [erro, setErro] = useState('')
  const [confirmarCancelId, setConfirmarCancelId] = useState<string | null>(null)
  const [resumos, setResumos] = useState<Record<string, ResumoAvaliacao>>({})
  const [verFeedbacksEv, setVerFeedbacksEv] = useState<Evento | null>(null)
  const [feedbacks, setFeedbacks] = useState<AvaliacaoPublica[]>([])
  const [carregandoFeedbacks, setCarregandoFeedbacks] = useState(false)

  useEffect(() => { carregar() }, [])

  async function carregar() {
    setCarregando(true)
    setErro('')
    try {
      const lista = await listarMeusEventos()
      setEventos(lista)
      const encerrados = lista.filter((e) => e.status === 'ENCERRADO')
      const entradas = await Promise.all(
        encerrados.map(async (e) => [e.id, await resumoAvaliacoesEvento(e.id).catch(() => ({ media: 0, quantidade: 0 }))] as const),
      )
      setResumos(Object.fromEntries(entradas))
    } catch {
      setErro('Erro ao carregar eventos.')
    } finally {
      setCarregando(false)
    }
  }

  async function abrirFeedbacks(ev: Evento) {
    setVerFeedbacksEv(ev)
    setCarregandoFeedbacks(true)
    try {
      setFeedbacks(await listarAvaliacoesEvento(ev.id))
    } catch {
      setFeedbacks([])
    } finally {
      setCarregandoFeedbacks(false)
    }
  }

  async function handleCancelar(id: string) {
    try {
      await cancelarEvento(id)
      setConfirmarCancelId(null)
      await carregar()
    } catch (err: unknown) {
      const msg = (err as { response?: { data?: { mensagem?: string } } })?.response?.data?.mensagem
      setErro(msg || 'Erro ao cancelar evento.')
    }
  }

  function handleSair() { sair(); navigate('/login') }

  return (
    <div className="mev-bg">
      <header className="mev-header">
        <span className="mev-logo" onClick={() => navigate('/dashboard')}>Voke</span>
        <div className="mev-header-right">
          <span className="mev-papel">{usuario?.papel}</span>
          <span className="mev-nome">{usuario?.nome}</span>
          <button className="mev-sair" onClick={handleSair}>Sair</button>
        </div>
      </header>

      <main className="mev-main">
        <div className="mev-topo">
          <div>
            <h1 className="mev-titulo">Meus Eventos</h1>
            <p className="mev-sub">Gerencie os eventos que você criou</p>
          </div>
          <button className="mev-btn-novo" onClick={() => navigate('/meus-eventos/novo')}>
            + Novo Evento
          </button>
        </div>

        {erro && <div className="mev-erro">{erro}</div>}
        {carregando && <p className="mev-vazio">Carregando...</p>}

        {!carregando && eventos.length === 0 && (
          <div className="mev-vazio-box">
            <p>Você ainda não criou nenhum evento.</p>
            <button className="mev-btn-novo" onClick={() => navigate('/meus-eventos/novo')}>
              Criar meu primeiro evento
            </button>
          </div>
        )}

        <div className="mev-lista">
          {eventos.map((ev) => (
            <div key={ev.id} className="mev-card">
              <div className="mev-card-topo">
                <div>
                  <h2 className="mev-card-nome">{ev.nome}</h2>
                  <p className="mev-card-data">
                    {new Date(ev.dataHoraInicio).toLocaleDateString('pt-BR')} &mdash;&nbsp;
                    {ev.local}
                  </p>
                </div>
                <span className={`mev-badge mev-badge--${ev.status.toLowerCase()}`}>
                  {statusLabel(ev.status)}
                </span>
              </div>

              {ev.status === 'ENCERRADO' && resumos[ev.id] && (
                <div style={{
                  marginTop: 6,
                  padding: '0.55rem 0.85rem',
                  background: '#fffbeb',
                  border: '1px solid #fde68a',
                  borderRadius: 10,
                  fontSize: '0.9rem',
                  display: 'flex',
                  justifyContent: 'space-between',
                  alignItems: 'center',
                  gap: 8,
                }}>
                  <span>
                    <span style={{ color: '#f59e0b', fontWeight: 700 }}>{estrelas(resumos[ev.id].media)}</span>
                    <strong style={{ marginLeft: 6 }}>{resumos[ev.id].media.toFixed(1)}</strong>
                    <span style={{ marginLeft: 6, color: '#92400e' }}>
                      ({resumos[ev.id].quantidade} {resumos[ev.id].quantidade === 1 ? 'avaliação' : 'avaliações'})
                    </span>
                  </span>
                  {resumos[ev.id].quantidade > 0 && (
                    <button className="mev-btn-editar" onClick={() => abrirFeedbacks(ev)}>
                      Ver feedbacks
                    </button>
                  )}
                </div>
              )}

              {ev.loteAtual && (
                <p className="mev-card-lote">
                  Lote {ev.loteAtual.numero} &bull; R$&nbsp;{ev.loteAtual.preco.toFixed(2)} &bull;&nbsp;
                  {ev.loteAtual.quantidadeVendida}/{ev.loteAtual.quantidadeTotal} vendidos &bull;&nbsp;
                  {ev.loteAtual.ativo ? 'EM VENDA' : 'ENCERRADO'}
                </p>
              )}

              <div className="mev-card-acoes">
                {ev.status === 'ATIVO' && (
                  <>
                    <button className="mev-btn-editar" onClick={() => navigate(`/meus-eventos/${ev.id}/editar`)}>
                      Editar
                    </button>
                    <button className="mev-btn-grupo" onClick={() => navigate(`/eventos/${ev.id}/grupo`)}>
                      Grupo
                    </button>
                    <button className="mev-btn-grupo" onClick={() => navigate(`/eventos/${ev.id}/notificacoes`)}>
                      🔔 Notificações
                    </button>
                    <button className="mev-btn-cancelar" onClick={() => setConfirmarCancelId(ev.id)}>
                      Cancelar Evento
                    </button>
                  </>
                )}
                {ev.status !== 'ATIVO' && (
                  <>
                    <button className="mev-btn-editar" onClick={() => navigate(`/meus-eventos/${ev.id}/editar`)}>
                      Ver detalhes
                    </button>
                    <button className="mev-btn-grupo" onClick={() => navigate(`/eventos/${ev.id}/notificacoes`)}>
                      🔔 Notificações
                    </button>
                  </>
                )}
              </div>
            </div>
          ))}
        </div>
      </main>

      {verFeedbacksEv && (
        <div className="mev-modal-bg" onClick={() => setVerFeedbacksEv(null)}>
          <div className="mev-modal" onClick={(e) => e.stopPropagation()} style={{ maxWidth: 560, maxHeight: '80vh', overflowY: 'auto' }}>
            <h2 style={{ marginBottom: 4 }}>Feedbacks — {verFeedbacksEv.nome}</h2>
            {resumos[verFeedbacksEv.id] && (
              <p style={{ color: '#92400e', marginTop: 0 }}>
                <span style={{ color: '#f59e0b' }}>{estrelas(resumos[verFeedbacksEv.id].media)}</span>{' '}
                <strong>{resumos[verFeedbacksEv.id].media.toFixed(1)}</strong>
                {' · '}
                {resumos[verFeedbacksEv.id].quantidade} {resumos[verFeedbacksEv.id].quantidade === 1 ? 'avaliação' : 'avaliações'}
              </p>
            )}
            {carregandoFeedbacks ? (
              <p>Carregando...</p>
            ) : feedbacks.length === 0 ? (
              <p>Nenhum feedback ainda.</p>
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem', marginTop: '0.75rem' }}>
                {feedbacks.map((a) => (
                  <div key={a.id} style={{
                    padding: '0.85rem 1rem',
                    border: '1px solid #e5e7eb',
                    borderRadius: 10,
                    background: '#fafafa',
                  }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                      <strong>{a.nomeParticipante}</strong>
                      <span style={{ color: '#f59e0b', fontWeight: 700 }}>{estrelas(a.nota)} {a.nota}</span>
                    </div>
                    {a.comentario && <p style={{ margin: '0.4rem 0 0', color: '#4b5563' }}>{a.comentario}</p>}
                  </div>
                ))}
              </div>
            )}
            <div className="mev-modal-acoes" style={{ marginTop: '1rem' }}>
              <button className="mev-btn-editar" onClick={() => setVerFeedbacksEv(null)}>Fechar</button>
            </div>
          </div>
        </div>
      )}

      {confirmarCancelId && (
        <div className="mev-modal-bg" onClick={() => setConfirmarCancelId(null)}>
          <div className="mev-modal" onClick={(e) => e.stopPropagation()}>
            <h2>Cancelar evento?</h2>
            <p>Esta ação não pode ser desfeita. O evento será marcado como cancelado e as inscrições serão invalidadas.</p>
            <div className="mev-modal-acoes">
              <button className="mev-btn-editar" onClick={() => setConfirmarCancelId(null)}>Voltar</button>
              <button className="mev-btn-cancelar" onClick={() => handleCancelar(confirmarCancelId)}>
                Confirmar cancelamento
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
