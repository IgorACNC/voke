import { useEffect, useRef, useState } from 'react'
import {
  listarMensagensGrupo, enviarMensagemGrupo,
  listarMensagensSubgrupo, enviarMensagemSubgrupo,
  type TipoCanalChat, type MensagemCanal,
} from '../services/chatCanalService'
import './ChatCanal.css'

interface Props {
  canalTipo: TipoCanalChat
  canalId: string
  usuarioId: string
  podeEnviar: boolean
  titulo?: string
}

export default function ChatCanal({ canalTipo, canalId, usuarioId, podeEnviar }: Props) {
  const [mensagens, setMensagens] = useState<MensagemCanal[]>([])
  const [novaMensagem, setNovaMensagem] = useState('')
  const [erro, setErro] = useState('')
  const [enviando, setEnviando] = useState(false)
  const containerRef = useRef<HTMLDivElement>(null)

  const listar = canalTipo === 'GRUPO_EVENTO' ? listarMensagensGrupo : listarMensagensSubgrupo
  const enviar = canalTipo === 'GRUPO_EVENTO' ? enviarMensagemGrupo : enviarMensagemSubgrupo

  async function carregarMensagens() {
    try {
      const lista = await listar(canalId)
      setMensagens(lista)
    } catch {
      /* silencioso no polling */
    }
  }

  useEffect(() => {
    carregarMensagens()
    const id = window.setInterval(carregarMensagens, 3000)
    return () => window.clearInterval(id)
  }, [canalId, canalTipo])

  useEffect(() => {
    if (containerRef.current) {
      containerRef.current.scrollTop = containerRef.current.scrollHeight
    }
  }, [mensagens])

  async function handleEnviar(e: React.FormEvent) {
    e.preventDefault()
    if (!novaMensagem.trim() || enviando) return

    setErro('')
    setEnviando(true)
    try {
      await enviar(canalId, novaMensagem)
      setNovaMensagem('')
      await carregarMensagens()
    } catch (err: unknown) {
      const msg = (err as { response?: { data?: { mensagem?: string } } })?.response?.data?.mensagem
      setErro(msg || 'Erro ao enviar mensagem.')
    } finally {
      setEnviando(false)
    }
  }

  return (
    <div className="chat-canal">
      <div className="chat-canal-mensagens" ref={containerRef}>
        {mensagens.length === 0 && (
          <p className="chat-canal-vazio">Nenhuma mensagem ainda. Seja o primeiro a falar!</p>
        )}
        {mensagens.map((m) => (
          <div
            key={m.id}
            className={m.remetenteId === usuarioId ? 'chat-canal-bolha minha' : 'chat-canal-bolha'}
          >
            {m.remetenteId !== usuarioId && (
              <span className="chat-canal-bolha-nome">{m.remetenteNome}</span>
            )}
            <p>{m.conteudo}</p>
            <span className="chat-canal-hora">
              {new Date(m.enviadaEm).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
            </span>
          </div>
        ))}
      </div>

      {erro && <div className="chat-canal-erro">{erro}</div>}

      <form className="chat-canal-form" onSubmit={handleEnviar}>
        <input
          value={novaMensagem}
          onChange={(e) => setNovaMensagem(e.target.value)}
          placeholder="Digite sua mensagem..."
          disabled={!podeEnviar}
          maxLength={1000}
        />
        <button disabled={!podeEnviar || enviando || !novaMensagem.trim()}>
          {enviando ? '...' : 'Enviar'}
        </button>
      </form>
    </div>
  )
}
