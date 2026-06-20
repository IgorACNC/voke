import { useEffect, useMemo, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import {
  enviarMensagem,
  listarAmizades,
  listarConversa,
  type Amizade,
  type MensagemPrivada,
} from '../services/socialService'
import './Social.css'

export default function ChatPrivado() {
  const navigate = useNavigate()
  const { usuario } = useAuth()
  const [amizades, setAmizades] = useState<Amizade[]>([])
  const [amigoSelecionadoId, setAmigoSelecionadoId] = useState('')
  const [mensagens, setMensagens] = useState<MensagemPrivada[]>([])
  const [novaMensagem, setNovaMensagem] = useState('')
  const [erro, setErro] = useState('')
  const [carregando, setCarregando] = useState(false)

  const amigosAtivos = useMemo(
    () => amizades.filter((a) => a.status === 'ATIVA' && a.amigo),
    [amizades],
  )

  const amigoSelecionado = useMemo(
    () => amigosAtivos.find((a) => a.amigo?.id === amigoSelecionadoId)?.amigo,
    [amigosAtivos, amigoSelecionadoId],
  )

  useEffect(() => {
    if (!usuario) return
    listarAmizades(usuario.id)
      .then((lista) => {
        setAmizades(lista)
        setAmigoSelecionadoId((atual) => atual || lista.find((a) => a.status === 'ATIVA' && a.amigo)?.amigo?.id || '')
      })
      .catch(() => setErro('Nao foi possivel carregar seus amigos.'))
  }, [usuario?.id])

  useEffect(() => {
    if (!usuario || !amigoSelecionadoId) {
      setMensagens([])
      return
    }

    carregarConversa().catch(() => setErro('Nao foi possivel carregar a conversa.'))
    const id = window.setInterval(() => {
      carregarConversa().catch(() => undefined)
    }, 3000)
    return () => window.clearInterval(id)
  }, [usuario?.id, amigoSelecionadoId])

  if (!usuario) return null

  async function carregarConversa() {
    if (!usuario || !amigoSelecionadoId) return
    setMensagens(await listarConversa(usuario.id, amigoSelecionadoId))
  }

  async function handleEnviarMensagem(e: React.FormEvent) {
    e.preventDefault()
    if (!amigoSelecionadoId || !novaMensagem.trim()) return

    setErro('')
    setCarregando(true)
    try {
      await enviarMensagem(usuario!.id, amigoSelecionadoId, novaMensagem)
      setNovaMensagem('')
      await carregarConversa()
    } catch (err: unknown) {
      setErro((err as { response?: { data?: { mensagem?: string } } })?.response?.data?.mensagem ?? 'Mensagem nao enviada.')
    } finally {
      setCarregando(false)
    }
  }

  return (
    <div className="social-bg">
      <header className="social-header">
        <button className="social-voltar" onClick={() => navigate('/dashboard')}>Voltar</button>
        <span className="social-logo">Voke</span>
        <div style={{ width: 90 }} />
      </header>

      <main className="social-main">
        <section className="social-title">
          <h1>Chat privado</h1>
          <p>Converse apenas com participantes que possuem amizade ativa com voce.</p>
        </section>

        {erro && <p className="social-msg-erro">{erro}</p>}

        <section className="social-chat-layout">
          <aside className="social-card social-chat-amigos">
            <h2>Amigos</h2>
            <div className="social-lista">
              {amigosAtivos.map((amizade) => (
                <button
                  className={amizade.amigo?.id === amigoSelecionadoId ? 'social-amigo-btn ativo' : 'social-amigo-btn'}
                  key={amizade.id}
                  onClick={() => setAmigoSelecionadoId(amizade.amigo!.id)}
                >
                  <strong>{amizade.amigo!.nome}</strong>
                  <span>{amizade.amigo!.email}</span>
                </button>
              ))}
              {amigosAtivos.length === 0 && <p className="social-vazio">Voce ainda nao possui amizades ativas para conversar.</p>}
            </div>
          </aside>

          <section className="social-card social-chat-painel">
            <div className="social-chat-topo">
              <div>
                <h2>{amigoSelecionado?.nome ?? 'Selecione um amigo'}</h2>
                <span>{amigoSelecionado ? 'Amizade ativa' : 'Nenhuma conversa selecionada'}</span>
              </div>
            </div>

            <div className="social-mensagens social-mensagens-altas">
              {mensagens.map((m) => (
                <div key={m.id} className={m.remetenteId === usuario.id ? 'social-bolha minha' : 'social-bolha'}>
                  <p>{m.conteudo}</p>
                  <span>{new Date(m.enviadaEm).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</span>
                </div>
              ))}
              {amigoSelecionadoId && mensagens.length === 0 && <p className="social-vazio">Nenhuma mensagem nesta conversa.</p>}
              {!amigoSelecionadoId && <p className="social-vazio">Escolha um amigo ativo para iniciar a conversa.</p>}
            </div>

            <form className="social-form social-chat-form" onSubmit={handleEnviarMensagem}>
              <input
                value={novaMensagem}
                onChange={(e) => setNovaMensagem(e.target.value)}
                placeholder="Digite sua mensagem"
                disabled={!amigoSelecionadoId}
              />
              <button disabled={!amigoSelecionadoId || carregando}>Enviar</button>
            </form>
          </section>
        </section>
      </main>
    </div>
  )
}
