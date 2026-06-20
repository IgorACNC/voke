import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { buscarEvento, type Evento } from '../services/eventoService'
import {
  agendarNotificacao,
  cancelarNotificacao,
  editarNotificacao,
  enviarNotificacao,
  listarNotificacoesPorEvento,
  removerNotificacao,
  type Notificacao,
} from '../services/notificacaoService'
import './NotificacoesEvento.css'

type ModoEnvio = 'imediato' | 'agendado'

export default function NotificacoesEvento() {
  const { eventoId } = useParams<{ eventoId: string }>()
  const navigate = useNavigate()
  const { sair } = useAuth()

  const [evento, setEvento] = useState<Evento | null>(null)
  const [notificacoes, setNotificacoes] = useState<Notificacao[]>([])
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState('')
  const [mensagem, setMensagem] = useState('')

  // Formulário
  const [modo, setModo] = useState<ModoEnvio>('imediato')
  const [conteudo, setConteudo] = useState('')
  const [dataAgendamento, setDataAgendamento] = useState('')
  const [usarSegmentacao, setUsarSegmentacao] = useState(false)
  const [numeroLote, setNumeroLote] = useState('')
  const [salvando, setSalvando] = useState(false)

  // Edição
  const [editandoId, setEditandoId] = useState<string | null>(null)
  const [conteudoEdit, setConteudoEdit] = useState('')

  useEffect(() => { carregar() }, [eventoId])

  async function carregar() {
    if (!eventoId) return
    setCarregando(true)
    try {
      const [ev, notifs] = await Promise.all([
        buscarEvento(eventoId),
        listarNotificacoesPorEvento(eventoId),
      ])
      setEvento(ev)
      setNotificacoes(notifs.sort(
        (a, b) => new Date(b.dataEnvio).getTime() - new Date(a.dataEnvio).getTime()
      ))
    } catch {
      setErro('Erro ao carregar notificações.')
    } finally {
      setCarregando(false)
    }
  }

  function limpar() { setErro(''); setMensagem('') }

  async function handleEnviar(e: React.FormEvent) {
    e.preventDefault()
    if (!eventoId || !conteudo.trim()) return
    limpar(); setSalvando(true)
    try {
      if (modo === 'agendado') {
        if (!dataAgendamento) { setErro('Defina a data de agendamento.'); setSalvando(false); return }
        await agendarNotificacao({
          eventoId,
          conteudo: conteudo.trim(),
          dataAgendamento,
        })
        setMensagem('Notificação agendada com sucesso.')
      } else {
        await enviarNotificacao({
          eventoId,
          conteudo: conteudo.trim(),
          numeroLote: usarSegmentacao && numeroLote ? Number(numeroLote) : null,
        })
        setMensagem('Notificação enviada com sucesso.')
      }
      setConteudo(''); setDataAgendamento(''); setNumeroLote('')
      setUsarSegmentacao(false)
      await carregar()
    } catch (err: unknown) {
      const msg = (err as { response?: { data?: { mensagem?: string } } })?.response?.data?.mensagem
        ?? 'Erro ao enviar notificação.'
      setErro(msg)
    } finally { setSalvando(false) }
  }

  function iniciarEdicao(n: Notificacao) {
    limpar(); setEditandoId(n.id); setConteudoEdit(n.conteudo)
  }
  function cancelarEdicao() { setEditandoId(null); setConteudoEdit('') }

  async function salvarEdicao() {
    if (!editandoId || !conteudoEdit.trim()) return
    limpar(); setSalvando(true)
    try {
      await editarNotificacao(editandoId, conteudoEdit.trim())
      setMensagem('Notificação editada.')
      cancelarEdicao()
      await carregar()
    } catch (err: unknown) {
      const msg = (err as { response?: { data?: { mensagem?: string } } })?.response?.data?.mensagem
        ?? 'Erro ao editar.'
      setErro(msg)
    } finally { setSalvando(false) }
  }

  async function handleCancelar(id: string) {
    limpar(); setSalvando(true)
    try {
      await cancelarNotificacao(id)
      setMensagem('Notificação cancelada.')
      await carregar()
    } catch (err: unknown) {
      const msg = (err as { response?: { data?: { mensagem?: string } } })?.response?.data?.mensagem
        ?? 'Erro ao cancelar.'
      setErro(msg)
    } finally { setSalvando(false) }
  }

  async function handleRemover(id: string) {
    if (!confirm('Remover esta notificação permanentemente?')) return
    limpar(); setSalvando(true)
    try {
      await removerNotificacao(id)
      setMensagem('Notificação removida.')
      await carregar()
    } catch (err: unknown) {
      const msg = (err as { response?: { data?: { mensagem?: string } } })?.response?.data?.mensagem
        ?? 'Erro ao remover.'
      setErro(msg)
    } finally { setSalvando(false) }
  }

  return (
    <div className="notif-bg">
      <header className="notif-header">
        <button className="notif-voltar" onClick={() => navigate('/meus-eventos')}>← Voltar</button>
        <span className="notif-logo">Voke</span>
        <button className="notif-sair" onClick={() => { sair(); navigate('/login') }}>Sair</button>
      </header>

      <main className="notif-main">
        <div className="notif-titulo-evento">
          <h1>Notificações</h1>
          {evento && <p>{evento.nome}</p>}
        </div>

        {mensagem && <p className="notif-msg-sucesso">{mensagem}</p>}
        {erro && <p className="notif-msg-erro">{erro}</p>}

        <section className="notif-secao">
          <h2>Nova notificação</h2>
          <div className="notif-tabs">
            <button
              type="button"
              className={modo === 'imediato' ? 'ativo' : ''}
              onClick={() => setModo('imediato')}
            >Envio imediato</button>
            <button
              type="button"
              className={modo === 'agendado' ? 'ativo' : ''}
              onClick={() => setModo('agendado')}
            >Agendar</button>
          </div>

          <form onSubmit={handleEnviar} className="notif-form">
            <label>Mensagem</label>
            <textarea
              value={conteudo}
              onChange={(e) => setConteudo(e.target.value)}
              placeholder="Ex: Portões abrem às 18h. Tragam documento com foto."
              rows={3}
              required
            />

            {modo === 'agendado' && (
              <>
                <label>Data e hora de envio</label>
                <input
                  type="datetime-local"
                  value={dataAgendamento}
                  onChange={(e) => setDataAgendamento(e.target.value)}
                  required
                />
              </>
            )}

            {modo === 'imediato' && (
              <>
                <label className="notif-check-label">
                  <input
                    type="checkbox"
                    checked={usarSegmentacao}
                    onChange={(e) => setUsarSegmentacao(e.target.checked)}
                  />
                  Segmentar por lote de inscrição
                </label>
                {usarSegmentacao && (
                  <>
                    <label>Número do lote</label>
                    <input
                      type="number"
                      min={1}
                      value={numeroLote}
                      onChange={(e) => setNumeroLote(e.target.value)}
                      placeholder="Ex: 1"
                    />
                  </>
                )}
              </>
            )}

            <button type="submit" className="notif-btn" disabled={salvando || !conteudo.trim()}>
              {salvando ? 'Enviando...' : modo === 'agendado' ? 'Agendar' : 'Enviar agora'}
            </button>
          </form>
        </section>

        <section className="notif-secao">
          <h2>Histórico ({notificacoes.length})</h2>
          {carregando ? (
            <p className="notif-info">Carregando...</p>
          ) : notificacoes.length === 0 ? (
            <p className="notif-info">Nenhuma notificação enviada ainda.</p>
          ) : (
            <ul className="notif-lista">
              {notificacoes.map((n) => (
                <li key={n.id} className={`notif-item status-${n.status.toLowerCase()}`}>
                  <div className="notif-item-topo">
                    <span className={`notif-badge status-${n.status.toLowerCase()}`}>{n.status}</span>
                    {n.criterioSegmentacao && (
                      <span className="notif-criterio">{n.criterioSegmentacao}</span>
                    )}
                    {n.contadorEdicoes > 0 && (
                      <span className="notif-edicoes">{n.contadorEdicoes}/3 edições</span>
                    )}
                  </div>

                  {editandoId === n.id ? (
                    <>
                      <textarea
                        className="notif-edit-textarea"
                        value={conteudoEdit}
                        onChange={(e) => setConteudoEdit(e.target.value)}
                        rows={2}
                      />
                      <div className="notif-acoes">
                        <button className="notif-btn-sec" onClick={cancelarEdicao} disabled={salvando}>Cancelar</button>
                        <button className="notif-btn" onClick={salvarEdicao} disabled={salvando}>Salvar</button>
                      </div>
                    </>
                  ) : (
                    <>
                      <p className="notif-conteudo">{n.conteudo}</p>
                      <p className="notif-meta">
                        {n.status === 'AGENDADA' && n.dataAgendamento
                          ? `Agendada para ${new Date(n.dataAgendamento).toLocaleString('pt-BR')}`
                          : `Enviada em ${new Date(n.dataEnvio).toLocaleString('pt-BR')}`}
                        {n.totalDestinatarios > 0 && ` • ${n.totalDestinatarios} destinatário(s)`}
                      </p>
                      <div className="notif-acoes">
                        {n.status !== 'CANCELADA' && n.contadorEdicoes < 3 && (
                          <button className="notif-btn-sec" onClick={() => iniciarEdicao(n)} disabled={salvando}>
                            Editar
                          </button>
                        )}
                        {n.status === 'AGENDADA' && (
                          <button className="notif-btn-sec" onClick={() => handleCancelar(n.id)} disabled={salvando}>
                            Cancelar agendamento
                          </button>
                        )}
                        <button className="notif-btn-perigo" onClick={() => handleRemover(n.id)} disabled={salvando}>
                          Remover
                        </button>
                      </div>
                    </>
                  )}
                </li>
              ))}
            </ul>
          )}
        </section>
      </main>
    </div>
  )
}
