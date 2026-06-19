import { useEffect, useMemo, useState } from 'react'
import { useLocation, useNavigate, useParams } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { buscarEvento, type Evento } from '../services/eventoService'
import {
  avaliarEvento,
  buscarAvaliacao,
  editarAvaliacao,
  listarEventosAvaliaveis,
  listarAvaliacoesEvento,
  removerAvaliacao,
  type Avaliacao,
  type AvaliacaoPublica,
  type EventoAvaliavel,
} from '../services/avaliacaoService'
import './Social.css'

function estrelas(media: number) {
  const cheias = Math.round(media)
  return '★'.repeat(cheias) + '☆'.repeat(5 - cheias)
}

export default function Avaliacoes() {
  const navigate = useNavigate()
  const location = useLocation()
  const { eventoId } = useParams()
  const { usuario } = useAuth()
  const [eventos, setEventos] = useState<EventoAvaliavel[]>([])
  const [eventoDetalhe, setEventoDetalhe] = useState<Evento | null>(null)
  const [nota, setNota] = useState(5)
  const [comentario, setComentario] = useState('')
  const [avaliacao, setAvaliacao] = useState<Avaliacao | null>(null)
  const [outrasAvaliacoes, setOutrasAvaliacoes] = useState<AvaliacaoPublica[]>([])
  const [mensagem, setMensagem] = useState('')
  const [erro, setErro] = useState('')
  const [carregando, setCarregando] = useState(false)

  const eventoRecebido = (location.state as { evento?: Evento } | null)?.evento
  const eventoSelecionado = useMemo(() => eventos.find((e) => e.id === eventoId), [eventos, eventoId])

  useEffect(() => {
    if (!usuario) return
    carregarEventos().catch(() => setErro('Nao foi possivel carregar seus eventos avaliaveis.'))
  }, [usuario?.id])

  useEffect(() => {
    if (!usuario || !eventoId) {
      setAvaliacao(null)
      setEventoDetalhe(null)
      setNota(5)
      setComentario('')
      return
    }

    if (eventoRecebido?.id === eventoId) {
      setEventoDetalhe(eventoRecebido)
    } else {
      buscarEvento(eventoId)
        .then(setEventoDetalhe)
        .catch(() => undefined)
    }

    buscarAvaliacao(usuario.id, eventoId)
      .then((encontrada) => {
        setAvaliacao(encontrada)
        setNota(encontrada?.nota ?? 5)
        setComentario(encontrada?.comentario ?? '')
      })
      .catch(() => setErro('Nao foi possivel carregar a avaliacao deste evento.'))

    listarAvaliacoesEvento(eventoId)
      .then(setOutrasAvaliacoes)
      .catch(() => setOutrasAvaliacoes([]))
  }, [usuario?.id, eventoId])

  if (!usuario) return null

  async function carregarEventos() {
    const lista = await listarEventosAvaliaveis(usuario!.id)
    setEventos(lista)
  }

  async function executar(acao: () => Promise<void>, sucesso: string) {
    setErro('')
    setMensagem('')
    setCarregando(true)
    try {
      await acao()
      await carregarEventos()
      setMensagem(sucesso)
    } catch (err: unknown) {
      setErro((err as { response?: { data?: { mensagem?: string } } })?.response?.data?.mensagem ?? 'Acao nao concluida.')
    } finally {
      setCarregando(false)
    }
  }

  async function handleSalvar(e: React.FormEvent) {
    e.preventDefault()
    if (!eventoId) return
    await executar(async () => {
      if (avaliacao) {
        await editarAvaliacao(avaliacao.id, nota, comentario)
      } else {
        const nova = await avaliarEvento({
          participanteId: usuario!.id,
          eventoId,
          nota,
          comentario,
        })
        setAvaliacao(nova)
      }
    }, avaliacao ? 'Avaliacao atualizada.' : 'Avaliacao registrada.')
  }

  async function handleRemover() {
    if (!avaliacao) return
    await executar(async () => {
      await removerAvaliacao(avaliacao.id)
      setAvaliacao(null)
      setComentario('')
      setNota(5)
    }, 'Avaliacao removida.')
  }

  return (
    <div className="social-bg">
      <header className="social-header">
        <button className="social-voltar" onClick={() => eventoId ? navigate('/avaliacoes') : navigate('/dashboard')}>Voltar</button>
        <span className="social-logo">Voke</span>
        <div style={{ width: 90 }} />
      </header>

      <main className={eventoId ? 'social-main social-main-curto' : 'social-main'}>
        <section className="social-title">
          <h1>{eventoId ? 'Avaliar evento' : 'Eventos para avaliar'}</h1>
          <p>{eventoId ? 'Registre ou edite sua avaliacao deste evento.' : 'Apenas eventos finalizados com inscricao confirmada aparecem aqui.'}</p>
        </section>

        {mensagem && <p className="social-msg-sucesso">{mensagem}</p>}
        {erro && <p className="social-msg-erro">{erro}</p>}

        {!eventoId ? (
          <section className="social-card">
            <h2>Eventos liberados</h2>
            <div className="social-lista social-lista-grande">
              {eventos.map((evento) => (
                <button
                  className="social-evento-btn"
                  key={evento.id}
                  onClick={() => navigate(`/avaliacoes/${evento.id}`)}
                >
                  <div style={{ flex: 1 }}>
                    <strong>
                      {evento.nome}
                      {evento.quantidade > 0 && (
                        <span style={{ marginLeft: 10, color: '#f59e0b', fontWeight: 600, fontSize: '0.9rem' }}>
                          {estrelas(evento.media)} {evento.media.toFixed(1)}
                        </span>
                      )}
                      {evento.quantidade > 0 && (
                        <span style={{ marginLeft: 6, color: '#6b7280', fontWeight: 500, fontSize: '0.85rem' }}>
                          ({evento.quantidade} {evento.quantidade === 1 ? 'comentário' : 'comentários'})
                        </span>
                      )}
                    </strong>
                    <span>{evento.local} - terminou em {new Date(evento.dataHoraFim).toLocaleString()}</span>
                  </div>
                  <span className={evento.avaliado ? 'social-tag feita' : 'social-tag'}>{evento.avaliado ? 'Editavel' : 'Avaliar'}</span>
                </button>
              ))}
              {eventos.length === 0 && <p className="social-vazio">Nenhum evento finalizado com inscricao confirmada para avaliar.</p>}
            </div>
          </section>
        ) : (
          <section className="social-card">
            <h2>{eventoSelecionado?.nome ?? eventoDetalhe?.nome ?? 'Evento selecionado'}</h2>
            {(eventoSelecionado || eventoDetalhe) && (
              <p className="social-card-sub">
                {(eventoSelecionado?.local ?? eventoDetalhe?.local)} - encerrado em {new Date(eventoSelecionado?.dataHoraFim ?? eventoDetalhe!.dataHoraFim).toLocaleString()}
              </p>
            )}

            <form className="social-form-col" onSubmit={handleSalvar}>
              <label>
                Nota
                <select value={nota} onChange={(e) => setNota(Number(e.target.value))}>
                  {[1, 2, 3, 4, 5].map((n) => <option key={n} value={n}>{n}</option>)}
                </select>
              </label>

              <label>
                Comentario
                <textarea value={comentario} onChange={(e) => setComentario(e.target.value)}
                          placeholder="Conte como foi sua experiencia" rows={5} />
              </label>

              <div className="social-acoes">
                <button disabled={carregando}>{avaliacao ? 'Atualizar avaliacao' : 'Registrar avaliacao'}</button>
                {avaliacao && <button type="button" className="social-btn-perigo" onClick={handleRemover}>Remover</button>}
              </div>
            </form>

            <hr style={{ margin: '1.5rem 0', border: 0, borderTop: '1px solid #e5e7eb' }} />

            <h3 style={{ margin: '0 0 0.5rem' }}>
              O que outros participantes disseram
              {outrasAvaliacoes.length > 0 && (
                <span style={{ marginLeft: 8, color: '#6b7280', fontWeight: 500, fontSize: '0.9rem' }}>
                  ({outrasAvaliacoes.length})
                </span>
              )}
            </h3>

            {outrasAvaliacoes.length === 0 ? (
              <p className="social-vazio">Ainda não há avaliações registradas para este evento.</p>
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
                {outrasAvaliacoes.map((a) => {
                  const ehVoce = a.participanteId === usuario!.id
                  return (
                    <div key={a.id} style={{
                      padding: '0.85rem 1rem',
                      border: ehVoce ? '1.5px solid #c4b5fd' : '1px solid #e5e7eb',
                      borderRadius: 10,
                      background: ehVoce ? '#f5f3ff' : '#fafafa',
                    }}>
                      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <strong>
                          {a.nomeParticipante}
                          {ehVoce && (
                            <span style={{
                              marginLeft: 8,
                              padding: '2px 8px',
                              borderRadius: 999,
                              background: '#7c6af7',
                              color: '#fff',
                              fontSize: '0.7rem',
                              fontWeight: 700,
                              letterSpacing: '0.04em',
                            }}>VOCÊ</span>
                          )}
                        </strong>
                        <span style={{ color: '#f59e0b', fontWeight: 700 }}>
                          {estrelas(a.nota)} {a.nota}
                        </span>
                      </div>
                      {a.comentario && <p style={{ margin: '0.4rem 0 0', color: '#4b5563' }}>{a.comentario}</p>}
                    </div>
                  )
                })}
              </div>
            )}
          </section>
        )}
      </main>
    </div>
  )
}
