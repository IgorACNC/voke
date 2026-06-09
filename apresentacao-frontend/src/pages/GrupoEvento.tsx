import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import {
  buscarGrupoPorEvento, entrarNoGrupo, removerMembro,
  editarRegras, excluirGrupo, type GrupoEvento as GrupoEventoType,
} from '../services/grupoEventoService'
import { buscarEvento, type Evento } from '../services/eventoService'
import './GrupoEvento.css'

export default function GrupoEvento() {
  const { eventoId } = useParams<{ eventoId: string }>()
  const { usuario, sair } = useAuth()
  const navigate = useNavigate()

  const [grupo, setGrupo] = useState<GrupoEventoType | null>(null)
  const [evento, setEvento] = useState<Evento | null>(null)
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState('')
  const [sucesso, setSucesso] = useState('')

  const [editandoRegras, setEditandoRegras] = useState(false)
  const [novasRegras, setNovasRegras] = useState('')
  const [salvandoRegras, setSalvandoRegras] = useState(false)

  const [confirmarExclusao, setConfirmarExclusao] = useState(false)

  const eOrganizador = usuario?.papel === 'ORGANIZADOR'
  const eOrganizadorDoGrupo = eOrganizador && grupo?.organizadorId === usuario?.id
  const jaMembro = grupo?.membrosIds.includes(usuario?.id ?? '') ?? false

  useEffect(() => {
    if (!eventoId) return
    Promise.all([buscarGrupoPorEvento(eventoId), buscarEvento(eventoId)])
      .then(([g, ev]) => { setGrupo(g); setEvento(ev) })
      .catch(() => setErro('Erro ao carregar dados do grupo.'))
      .finally(() => setCarregando(false))
  }, [eventoId])

  async function handleEntrar() {
    setErro(''); setSucesso('')
    try {
      await entrarNoGrupo(grupo!.id)
      const atualizado = await buscarGrupoPorEvento(eventoId!)
      setGrupo(atualizado)
      setSucesso('Você entrou no grupo!')
    } catch (err: unknown) {
      const msg = (err as { response?: { data?: { mensagem?: string } } })?.response?.data?.mensagem
      setErro(msg || 'Erro ao entrar no grupo.')
    }
  }

  async function handleRemoverMembro(participanteId: string) {
    setErro(''); setSucesso('')
    try {
      await removerMembro(grupo!.id, participanteId)
      const atualizado = await buscarGrupoPorEvento(eventoId!)
      setGrupo(atualizado)
      setSucesso('Membro removido.')
    } catch (err: unknown) {
      const msg = (err as { response?: { data?: { mensagem?: string } } })?.response?.data?.mensagem
      setErro(msg || 'Erro ao remover membro.')
    }
  }

  async function handleSalvarRegras(e: React.FormEvent) {
    e.preventDefault()
    setErro(''); setSucesso('')
    setSalvandoRegras(true)
    try {
      await editarRegras(grupo!.id, novasRegras)
      const atualizado = await buscarGrupoPorEvento(eventoId!)
      setGrupo(atualizado)
      setEditandoRegras(false)
      setSucesso('Regras atualizadas.')
    } catch (err: unknown) {
      const msg = (err as { response?: { data?: { mensagem?: string } } })?.response?.data?.mensagem
      setErro(msg || 'Erro ao salvar regras.')
    } finally {
      setSalvandoRegras(false)
    }
  }

  async function handleExcluirGrupo() {
    setErro('')
    try {
      await excluirGrupo(grupo!.id)
      navigate(`/meus-eventos/${eventoId}/editar`)
    } catch (err: unknown) {
      const msg = (err as { response?: { data?: { mensagem?: string } } })?.response?.data?.mensagem
      setErro(msg || 'Erro ao excluir grupo.')
      setConfirmarExclusao(false)
    }
  }

  function handleSair() { sair(); navigate('/login') }

  if (carregando) return <div className="gev-loading">Carregando...</div>

  return (
    <div className="gev-bg">
      <header className="gev-header">
        <span className="gev-logo" onClick={() => navigate(eOrganizador ? '/meus-eventos' : '/dashboard')}>
          Voke
        </span>
        <div className="gev-header-right">
          <span className="gev-papel">{usuario?.papel}</span>
          <span className="gev-nome">{usuario?.nome}</span>
          <button className="gev-sair" onClick={handleSair}>Sair</button>
        </div>
      </header>

      <main className="gev-main">
        <button className="gev-voltar"
          onClick={() => navigate(eOrganizador ? `/meus-eventos/${eventoId}/editar` : '/dashboard')}>
          ← Voltar
        </button>

        {erro && <div className="gev-erro">{erro}</div>}
        {sucesso && <div className="gev-sucesso">{sucesso}</div>}

        {/* Sem grupo criado */}
        {!grupo && (
          <div className="gev-card gev-card--vazio">
            <h2 className="gev-titulo">{evento?.nome} — Grupo</h2>
            <p className="gev-vazio-desc">Este evento ainda não tem um grupo de comunicação.</p>
            {eOrganizadorDoGrupo === false && eOrganizador && (
              <button className="gev-btn-primario"
                onClick={() => navigate(`/eventos/${eventoId}/grupo/criar`)}>
                Criar Grupo
              </button>
            )}
            {!eOrganizador && (
              <p className="gev-vazio-info">O organizador ainda não criou um grupo para este evento.</p>
            )}
          </div>
        )}

        {/* Grupo existe */}
        {grupo && (
          <>
            <div className="gev-card">
              <div className="gev-card-topo">
                <div>
                  <h2 className="gev-titulo">{grupo.nome}</h2>
                  <p className="gev-membros-info">{grupo.membrosIds.length} membros</p>
                </div>
                <span className="gev-status-badge">ATIVO</span>
              </div>

              {grupo.regras && !editandoRegras && (
                <div className="gev-regras-box">
                  <span className="gev-regras-titulo">Regras</span>
                  <p className="gev-regras-texto">{grupo.regras}</p>
                </div>
              )}

              {editandoRegras && (
                <form className="gev-form-regras" onSubmit={handleSalvarRegras}>
                  <label className="gev-label">Regras de Convivência</label>
                  <textarea className="gev-textarea" rows={4} value={novasRegras}
                    onChange={(e) => setNovasRegras(e.target.value)} />
                  <div className="gev-regras-acoes">
                    <button type="button" className="gev-btn-sec"
                      onClick={() => setEditandoRegras(false)}>Cancelar</button>
                    <button type="submit" className="gev-btn-primario" disabled={salvandoRegras}>
                      {salvandoRegras ? 'Salvando...' : 'Salvar Regras'}
                    </button>
                  </div>
                </form>
              )}

              {/* Ações do participante */}
              {!eOrganizador && !jaMembro && (
                <button className="gev-btn-primario" onClick={handleEntrar}>
                  Entrar no Grupo
                </button>
              )}
              {!eOrganizador && jaMembro && (
                <p className="gev-ja-membro">Você já é membro deste grupo.</p>
              )}
            </div>

            {/* Lista de membros */}
            <div className="gev-card">
              <h3 className="gev-secao-titulo">Membros ({grupo.membrosIds.length})</h3>
              {grupo.membrosIds.length === 0 && (
                <p className="gev-vazio-desc">Nenhum membro ainda.</p>
              )}
              <ul className="gev-membros-lista">
                {grupo.membrosIds.map((mid) => (
                  <li key={mid} className="gev-membro-item">
                    <span className="gev-membro-id">{mid.slice(0, 8)}...</span>
                    {eOrganizadorDoGrupo && (
                      <button className="gev-btn-remover" onClick={() => handleRemoverMembro(mid)}>
                        Remover
                      </button>
                    )}
                  </li>
                ))}
              </ul>
            </div>

            {/* Ações do organizador */}
            {eOrganizadorDoGrupo && (
              <div className="gev-card gev-card--acoes-org">
                <button className="gev-btn-regras"
                  onClick={() => { setNovasRegras(grupo.regras || ''); setEditandoRegras(true) }}>
                  Editar Regras
                </button>
                <button className="gev-btn-excluir" onClick={() => setConfirmarExclusao(true)}>
                  Deletar Grupo
                </button>
              </div>
            )}
          </>
        )}
      </main>

      {confirmarExclusao && (
        <div className="gev-modal-bg" onClick={() => setConfirmarExclusao(false)}>
          <div className="gev-modal" onClick={(e) => e.stopPropagation()}>
            <h2>Excluir grupo?</h2>
            <p>Esta ação é irreversível. Todos os membros perderão acesso ao grupo.</p>
            <div className="gev-modal-acoes">
              <button className="gev-btn-sec" onClick={() => setConfirmarExclusao(false)}>Cancelar</button>
              <button className="gev-btn-excluir" onClick={handleExcluirGrupo}>Confirmar exclusão</button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
