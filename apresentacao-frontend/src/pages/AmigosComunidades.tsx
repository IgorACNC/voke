import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import {
  aceitarAmizade,
  adicionarMembroComunidade,
  buscarParticipantes,
  compartilharEventoComunidade,
  criarComunidade,
  desfazerAmizade,
  listarAmizades,
  listarComunidades,
  recusarAmizade,
  solicitarAmizade,
  type Amizade,
  type Comunidade,
  type ParticipanteResumo,
} from '../services/socialService'
import { listarEventosAtivos, type Evento } from '../services/eventoService'
import './Social.css'

export default function AmigosComunidades() {
  const navigate = useNavigate()
  const { usuario } = useAuth()
  const [amizades, setAmizades] = useState<Amizade[]>([])
  const [comunidades, setComunidades] = useState<Comunidade[]>([])
  const [eventos, setEventos] = useState<Evento[]>([])
  const [termoBusca, setTermoBusca] = useState('')
  const [participantesEncontrados, setParticipantesEncontrados] = useState<ParticipanteResumo[]>([])
  const [nomeComunidade, setNomeComunidade] = useState('')
  const [membroPorComunidade, setMembroPorComunidade] = useState<Record<string, string>>({})
  const [eventoPorComunidade, setEventoPorComunidade] = useState<Record<string, string>>({})
  const [erro, setErro] = useState('')
  const [mensagem, setMensagem] = useState('')
  const [carregando, setCarregando] = useState(false)

  async function carregarBase() {
    if (!usuario) return
    const [amizadesResp, comunidadesResp, eventosResp] = await Promise.all([
      listarAmizades(usuario.id),
      listarComunidades(usuario.id),
      listarEventosAtivos(),
    ])
    setAmizades(amizadesResp)
    setComunidades(comunidadesResp)
    setEventos(eventosResp)
  }

  useEffect(() => {
    carregarBase().catch(() => setErro('Nao foi possivel carregar seus dados sociais.'))
  }, [usuario?.id])

  if (!usuario) return null

  async function executar(acao: () => Promise<void>, sucesso: string) {
    setErro('')
    setMensagem('')
    setCarregando(true)
    try {
      await acao()
      await carregarBase()
      setMensagem(sucesso)
    } catch (err: unknown) {
      setErro((err as { response?: { data?: { mensagem?: string } } })?.response?.data?.mensagem ?? 'Acao nao concluida.')
    } finally {
      setCarregando(false)
    }
  }

  async function handleCriarComunidade(e: React.FormEvent) {
    e.preventDefault()
    if (!nomeComunidade.trim()) return
    await executar(async () => {
      await criarComunidade(usuario!.id, nomeComunidade)
      setNomeComunidade('')
    }, 'Comunidade criada.')
  }

  async function handleBuscarParticipantes(e: React.FormEvent) {
    e.preventDefault()
    if (termoBusca.trim().length < 2) {
      setErro('Digite pelo menos 2 caracteres para buscar.')
      return
    }
    setErro('')
    setMensagem('')
    setCarregando(true)
    try {
      setParticipantesEncontrados(await buscarParticipantes(termoBusca, usuario!.id))
    } catch {
      setErro('Nao foi possivel buscar participantes.')
    } finally {
      setCarregando(false)
    }
  }

  async function handleSolicitarAmizade(receptorId: string) {
    await executar(async () => {
      await solicitarAmizade(usuario!.id, receptorId)
      setParticipantesEncontrados((atuais) => atuais.filter((p) => p.id !== receptorId))
    }, 'Solicitacao enviada.')
  }

  function amigosDisponiveis(comunidade: Comunidade): ParticipanteResumo[] {
    const membrosIds = new Set(comunidade.membros.map((m) => m.id))
    return amizades
      .filter((a) => a.status === 'ATIVA' && a.amigo && !membrosIds.has(a.amigo.id))
      .map((a) => a.amigo!)
  }

  async function handleAdicionarMembro(comunidadeId: string) {
    const participanteId = membroPorComunidade[comunidadeId]
    if (!participanteId) return
    await executar(async () => {
      await adicionarMembroComunidade(comunidadeId, usuario!.id, participanteId)
      setMembroPorComunidade((atuais) => ({ ...atuais, [comunidadeId]: '' }))
    }, 'Amigo adicionado na comunidade.')
  }

  async function handleCompartilharEvento(comunidadeId: string) {
    const eventoId = eventoPorComunidade[comunidadeId]
    if (!eventoId) return
    await executar(async () => {
      await compartilharEventoComunidade(comunidadeId, usuario!.id, eventoId)
      setEventoPorComunidade((atuais) => ({ ...atuais, [comunidadeId]: '' }))
    }, 'Evento compartilhado na comunidade.')
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
          <h1>Amigos e comunidades</h1>
          <p>Gerencie suas conexoes e organize comunidades com amigos confirmados.</p>
        </section>

        {mensagem && <p className="social-msg-sucesso">{mensagem}</p>}
        {erro && <p className="social-msg-erro">{erro}</p>}

        <div className="social-grid">
          <section className="social-card">
            <h2>Solicitar amizade</h2>
            <form className="social-form-col" onSubmit={handleBuscarParticipantes}>
              <label>
                Buscar por nome ou e-mail
                <div className="social-form-linha">
                  <input
                    value={termoBusca}
                    onChange={(e) => setTermoBusca(e.target.value)}
                    placeholder="Ex: Maria ou maria@email.com"
                  />
                  <button disabled={carregando}>Buscar</button>
                </div>
              </label>
            </form>
            <div className="social-lista">
              {participantesEncontrados.map((p) => (
                <div className="social-item" key={p.id}>
                  <div>
                    <strong>{p.nome}</strong>
                    <span>{p.email}</span>
                  </div>
                  <button onClick={() => handleSolicitarAmizade(p.id)} disabled={carregando}>Adicionar</button>
                </div>
              ))}
              {termoBusca.length >= 2 && participantesEncontrados.length === 0 && (
                <p className="social-vazio">Nenhum participante encontrado para essa busca.</p>
              )}
            </div>
          </section>

          <section className="social-card">
            <h2>Minhas amizades</h2>
            <div className="social-lista">
              {amizades.map((a) => (
                <div className="social-item" key={a.id}>
                  <div>
                    <strong>{a.amigo?.nome ?? 'Participante'}</strong>
                    <span>{a.status}</span>
                  </div>
                  {a.status === 'PENDENTE' && a.receptorId === usuario.id ? (
                    <div className="social-acoes-linha">
                      <button onClick={() => executar(() => aceitarAmizade(a.id), 'Amizade aceita.')}>Aceitar</button>
                      <button className="social-btn-sec" onClick={() => executar(() => recusarAmizade(a.id), 'Amizade recusada.')}>Recusar</button>
                    </div>
                  ) : a.status === 'ATIVA' ? (
                    <div className="social-acoes-linha">
                      <button onClick={() => navigate('/chat-privado')}>Chat</button>
                      <button className="social-btn-sec" onClick={() => executar(() => desfazerAmizade(a.id), 'Amizade desfeita.')}>Remover</button>
                    </div>
                  ) : null}
                </div>
              ))}
              {amizades.length === 0 && <p className="social-vazio">Nenhuma amizade ainda.</p>}
            </div>
          </section>

          <section className="social-card">
            <h2>Comunidades</h2>
            <form className="social-form" onSubmit={handleCriarComunidade}>
              <input value={nomeComunidade} onChange={(e) => setNomeComunidade(e.target.value)}
                     placeholder="Nome da comunidade" />
              <button disabled={carregando}>Criar</button>
            </form>
            <div className="social-lista">
              {comunidades.map((c) => (
                <div className="social-comunidade" key={c.id}>
                  <div className="social-comunidade-topo">
                    <strong>{c.nome}</strong>
                    <span>{c.membros.length} membros - {c.eventosCompartilhados.length} eventos</span>
                  </div>
                  <div className="social-mini-lista">
                    <span>Membros</span>
                    <p>{c.membros.map((m) => m.nome).join(', ') || 'Nenhum membro ainda.'}</p>
                  </div>
                  <div className="social-mini-lista">
                    <span>Eventos indicados</span>
                    <p>{c.eventosCompartilhados.map((e) => e.nome).join(', ') || 'Nenhum evento indicado ainda.'}</p>
                  </div>
                  {c.criadorId === usuario.id && (
                    <>
                      <div className="social-form-linha social-form-sem-margem">
                        <select
                          value={membroPorComunidade[c.id] ?? ''}
                          onChange={(e) => setMembroPorComunidade((atuais) => ({ ...atuais, [c.id]: e.target.value }))}
                        >
                          <option value="">Adicionar amigo</option>
                          {amigosDisponiveis(c).map((amigo) => (
                            <option key={amigo.id} value={amigo.id}>{amigo.nome}</option>
                          ))}
                        </select>
                        <button onClick={() => handleAdicionarMembro(c.id)} disabled={carregando || !membroPorComunidade[c.id]}>
                          Adicionar
                        </button>
                      </div>
                      <div className="social-form-linha social-form-sem-margem">
                        <select
                          value={eventoPorComunidade[c.id] ?? ''}
                          onChange={(e) => setEventoPorComunidade((atuais) => ({ ...atuais, [c.id]: e.target.value }))}
                        >
                          <option value="">Indicar evento</option>
                          {eventos
                            .filter((evento) => !c.eventosCompartilhados.some((compartilhado) => compartilhado.id === evento.id))
                            .map((evento) => (
                              <option key={evento.id} value={evento.id}>{evento.nome}</option>
                            ))}
                        </select>
                        <button onClick={() => handleCompartilharEvento(c.id)} disabled={carregando || !eventoPorComunidade[c.id]}>
                          Compartilhar
                        </button>
                      </div>
                    </>
                  )}
                </div>
              ))}
              {comunidades.length === 0 && <p className="social-vazio">Crie uma comunidade depois de ter uma amizade ativa.</p>}
            </div>
          </section>

        </div>
      </main>
    </div>
  )
}
