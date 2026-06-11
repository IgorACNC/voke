import { useEffect, useMemo, useState } from 'react'
import { useLocation, useNavigate, useParams } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { buscarEvento, type Evento } from '../services/eventoService'
import {
  avaliarEvento,
  buscarAvaliacao,
  editarAvaliacao,
  listarEventosAvaliaveis,
  removerAvaliacao,
  type Avaliacao,
  type EventoAvaliavel,
} from '../services/avaliacaoService'
import './Social.css'

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
                  <div>
                    <strong>{evento.nome}</strong>
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
          </section>
        )}
      </main>
    </div>
  )
}
