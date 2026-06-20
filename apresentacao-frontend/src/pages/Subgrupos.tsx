import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { buscarGrupoPorEvento, type GrupoEvento } from '../services/grupoEventoService'
import {
  listarSubgruposDoGrupo, entrarNoSubgrupo, removerMembroSubgrupo, type Subgrupo,
} from '../services/subgrupoService'
import { solicitarEntrada, listarMinhasSolicitacoes } from '../services/solicitacaoSubgrupoService'
import './Subgrupo.css'

const CAT_LABEL: Record<string, string> = {
  CARONA: '🚗 Carona', INTERESSE: '🎯 Interesse', SOCIAL: '👥 Social',
  OPERACIONAL: '🛠️ Operacional', OUTRO: '📌 Outro',
}

export default function Subgrupos() {
  const { eventoId } = useParams<{ eventoId: string }>()
  const { usuario, sair } = useAuth()
  const navigate = useNavigate()

  const [grupo, setGrupo] = useState<GrupoEvento | null>(null)
  const [subgrupos, setSubgrupos] = useState<Subgrupo[]>([])
  const [pendentes, setPendentes] = useState<Set<string>>(new Set())
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState('')
  const [solicitarFor, setSolicitarFor] = useState<Subgrupo | null>(null)
  const [mensagem, setMensagem] = useState('')

  const ehOrganizador = usuario?.papel === 'ORGANIZADOR'

  useEffect(() => { carregar() }, [eventoId])

  async function carregar() {
    if (!eventoId) return
    setCarregando(true)
    setErro('')
    try {
      const g = await buscarGrupoPorEvento(eventoId)
      setGrupo(g)
      if (g) {
        const subs = await listarSubgruposDoGrupo(g.id)
        setSubgrupos(subs)
        if (usuario?.papel === 'PARTICIPANTE') {
          const minhas = await listarMinhasSolicitacoes()
          const pendentesSubgrupos = new Set(
            minhas.filter(s => s.status === 'PENDENTE').map(s => s.subgrupoId)
          )
          setPendentes(pendentesSubgrupos)
        }
      }
    } catch {
      setErro('Erro ao carregar subgrupos.')
    } finally {
      setCarregando(false)
    }
  }

  async function handleEntrar(sub: Subgrupo) {
    setErro('')
    try {
      await entrarNoSubgrupo(sub.id)
      await carregar()
    } catch (err: unknown) {
      const msg = (err as { response?: { data?: { mensagem?: string } } })?.response?.data?.mensagem
      setErro(msg || 'Erro ao entrar no subgrupo.')
    }
  }

  async function handleSair(sub: Subgrupo) {
    if (!usuario) return
    try {
      await removerMembroSubgrupo(sub.id, usuario.id)
      await carregar()
    } catch (err: unknown) {
      const msg = (err as { response?: { data?: { mensagem?: string } } })?.response?.data?.mensagem
      setErro(msg || 'Erro ao sair.')
    }
  }

  async function handleSolicitar() {
    if (!solicitarFor) return
    try {
      await solicitarEntrada(solicitarFor.id, mensagem)
      setSolicitarFor(null)
      setMensagem('')
      await carregar()
    } catch (err: unknown) {
      const msg = (err as { response?: { data?: { mensagem?: string } } })?.response?.data?.mensagem
      setErro(msg || 'Erro ao enviar solicitação.')
    }
  }

  function handleSairApp() { sair(); navigate('/login') }

  return (
    <div className="sub-bg">
      <header className="sub-header">
        <span className="sub-logo" onClick={() => navigate('/dashboard')}>Voke</span>
        <div className="sub-header-right">
          <span className="sub-papel">{usuario?.papel}</span>
          <span className="sub-nome">{usuario?.nome}</span>
          <button className="sub-sair" onClick={handleSairApp}>Sair</button>
        </div>
      </header>

      <main className="sub-main">
        <button className="sub-voltar" onClick={() => navigate(`/eventos/${eventoId}/grupo`)}>
          ← Voltar para o Grupo
        </button>

        <div className="sub-topo">
          <div>
            <h1 className="sub-titulo">Subgrupos</h1>
            <p className="sub-sub">{grupo?.nome ?? 'Grupo do evento'}</p>
          </div>
          {ehOrganizador && grupo && (
            <button className="sub-btn-primario"
              onClick={() => navigate(`/eventos/${eventoId}/grupo/subgrupos/novo?grupoId=${grupo.id}`)}>
              + Criar Subgrupo
            </button>
          )}
        </div>

        {erro && <div className="sub-erro">{erro}</div>}
        {carregando && <p className="sub-info">Carregando subgrupos...</p>}
        {!carregando && !grupo && (
          <p className="sub-info">Este evento ainda não tem um Grupo principal.</p>
        )}
        {!carregando && grupo && subgrupos.length === 0 && (
          <p className="sub-info">Nenhum subgrupo criado ainda.</p>
        )}

        <div className="sub-lista">
          {subgrupos.map((s) => {
            const ehMembro = usuario ? s.membrosIds.includes(usuario.id) : false
            const ehModerador = usuario ? s.moderadorId === usuario.id : false
            const ehGestor = ehOrganizador || ehModerador
            const cheio = s.limiteMembros > 0 && s.membrosIds.length >= s.limiteMembros
            const temPendente = pendentes.has(s.id)

            return (
              <div key={s.id} className="sub-card">
                <div className="sub-card-topo">
                  <div>
                    <h2 className="sub-card-nome">{s.nome}</h2>
                    {s.descricao && <p className="sub-card-desc">{s.descricao}</p>}
                  </div>
                </div>

                <div className="sub-card-meta">
                  <span className="sub-tag sub-tag--cat">{CAT_LABEL[s.categoria] || s.categoria}</span>
                  <span className={`sub-tag sub-tag--tipo-${s.tipo.toLowerCase()}`}>
                    {s.tipo === 'ABERTO' ? '🔓 Aberto' : '🔒 Fechado'}
                  </span>
                  {s.moderadorId && <span className="sub-tag sub-tag--moderado">⭐ Moderado</span>}
                  {temPendente && <span className="sub-tag sub-tag--pendente">⏳ Solicitação enviada</span>}
                </div>

                <p className="sub-card-info">
                  <strong>{s.membrosIds.length}</strong>
                  {s.limiteMembros > 0 ? ` / ${s.limiteMembros}` : ''} membros
                </p>

                <div className="sub-card-acoes">
                  {ehGestor || ehMembro ? (
                    <button className="sub-btn-secundario"
                      onClick={() => navigate(`/subgrupos/${s.id}`)}>
                      {ehGestor ? 'Gerenciar' : 'Ver detalhes'}
                    </button>
                  ) : null}

                  {!ehMembro && !ehGestor && s.tipo === 'ABERTO' && (
                    <button className="sub-btn-primario"
                      disabled={cheio}
                      onClick={() => handleEntrar(s)}>
                      {cheio ? 'Cheio' : 'Entrar'}
                    </button>
                  )}

                  {!ehMembro && !ehGestor && s.tipo === 'FECHADO' && (
                    <button className="sub-btn-primario"
                      disabled={cheio || temPendente}
                      onClick={() => { setSolicitarFor(s); setMensagem('') }}>
                      {cheio ? 'Cheio' : temPendente ? 'Solicitação enviada' : 'Solicitar entrada'}
                    </button>
                  )}

                  {ehMembro && !ehOrganizador && (
                    <button className="sub-btn-perigo" onClick={() => handleSair(s)}>
                      Sair
                    </button>
                  )}
                </div>
              </div>
            )
          })}
        </div>
      </main>

      {solicitarFor && (
        <div className="sub-modal-bg" onClick={() => setSolicitarFor(null)}>
          <div className="sub-modal" onClick={(e) => e.stopPropagation()}>
            <h2>Solicitar entrada</h2>
            <p>Envie uma mensagem ao organizador explicando por que quer entrar em <strong>{solicitarFor.nome}</strong>.</p>
            <textarea className="sub-textarea" rows={4}
              placeholder="ex: tenho carro, vou de Boa Viagem; ou: quero participar da mesa de jogos..."
              value={mensagem} onChange={(e) => setMensagem(e.target.value)} />
            <div className="sub-modal-acoes" style={{ marginTop: '0.8rem' }}>
              <button className="sub-btn-secundario" onClick={() => setSolicitarFor(null)}>Cancelar</button>
              <button className="sub-btn-primario" onClick={handleSolicitar}>Enviar solicitação</button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
