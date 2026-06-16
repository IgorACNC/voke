import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import {
  buscarSubgrupo, editarRegrasSubgrupo, excluirSubgrupo, removerMembroSubgrupo,
  promoverModerador, removerModerador, type Subgrupo,
} from '../services/subgrupoService'
import {
  listarSolicitacoesDoSubgrupo, aprovarSolicitacao, rejeitarSolicitacao,
  type SolicitacaoSubgrupo,
} from '../services/solicitacaoSubgrupoService'
import ChatCanal from '../components/ChatCanal'
import './Subgrupo.css'

type Aba = 'membros' | 'regras' | 'solicitacoes' | 'chat'

export default function SubgrupoDetalhe() {
  const { id } = useParams<{ id: string }>()
  const { usuario, sair } = useAuth()
  const navigate = useNavigate()

  const [sub, setSub] = useState<Subgrupo | null>(null)
  const [solicitacoes, setSolicitacoes] = useState<SolicitacaoSubgrupo[]>([])
  const [aba, setAba] = useState<Aba>('membros')
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState('')
  const [mensagem, setMensagem] = useState('')
  const [editandoRegras, setEditandoRegras] = useState(false)
  const [novaRegras, setNovaRegras] = useState('')
  const [novaDescricao, setNovaDescricao] = useState('')
  const [confirmarExcluir, setConfirmarExcluir] = useState(false)

  const ehOrganizador = usuario?.papel === 'ORGANIZADOR'
  const ehModerador = !!(usuario && sub && sub.moderadorId === usuario.id)
  const ehGestor = ehOrganizador || ehModerador
  const ehMembro = !!(usuario && sub && sub.membrosIds.includes(usuario.id))

  useEffect(() => { carregar() }, [id])

  async function carregar() {
    if (!id) return
    setCarregando(true)
    setErro('')
    try {
      const s = await buscarSubgrupo(id)
      setSub(s)
      setNovaRegras(s.regras || '')
      setNovaDescricao(s.descricao || '')
      try {
        const sols = await listarSolicitacoesDoSubgrupo(id)
        setSolicitacoes(sols)
      } catch { /* sem permissão para listar */ }
    } catch {
      setErro('Erro ao carregar subgrupo.')
    } finally {
      setCarregando(false)
    }
  }

  async function handleSalvarRegras() {
    if (!sub) return
    try {
      await editarRegrasSubgrupo(sub.id, novaRegras, novaDescricao)
      setEditandoRegras(false)
      setMensagem('Regras atualizadas.')
      await carregar()
    } catch (err: unknown) {
      setErro(extrairMsg(err) || 'Erro ao salvar regras.')
    }
  }

  async function handleRemoverMembro(participanteId: string) {
    if (!sub) return
    try {
      await removerMembroSubgrupo(sub.id, participanteId)
      await carregar()
    } catch (err: unknown) {
      setErro(extrairMsg(err) || 'Erro ao remover membro.')
    }
  }

  async function handlePromover(participanteId: string) {
    if (!sub) return
    try {
      await promoverModerador(sub.id, participanteId)
      setMensagem('Moderador promovido.')
      await carregar()
    } catch (err: unknown) {
      setErro(extrairMsg(err) || 'Erro ao promover.')
    }
  }

  async function handleRemoverModerador() {
    if (!sub) return
    try {
      await removerModerador(sub.id)
      setMensagem('Moderador removido.')
      await carregar()
    } catch (err: unknown) {
      setErro(extrairMsg(err) || 'Erro ao remover moderador.')
    }
  }

  async function handleAprovar(sId: string) {
    try {
      await aprovarSolicitacao(sId)
      await carregar()
    } catch (err: unknown) {
      setErro(extrairMsg(err) || 'Erro ao aprovar.')
    }
  }

  async function handleRejeitar(sId: string) {
    try {
      await rejeitarSolicitacao(sId)
      await carregar()
    } catch (err: unknown) {
      setErro(extrairMsg(err) || 'Erro ao rejeitar.')
    }
  }

  async function handleExcluir() {
    if (!sub) return
    try {
      await excluirSubgrupo(sub.id)
      navigate(-1)
    } catch (err: unknown) {
      setErro(extrairMsg(err) || 'Erro ao excluir.')
    }
  }

  function extrairMsg(err: unknown): string | undefined {
    return (err as { response?: { data?: { mensagem?: string } } })?.response?.data?.mensagem
  }

  function handleSair() { sair(); navigate('/login') }

  if (carregando) return <div className="sub-bg"><p className="sub-info">Carregando...</p></div>
  if (!sub) return <div className="sub-bg"><p className="sub-info">Subgrupo não encontrado.</p></div>

  const pendentes = solicitacoes.filter(s => s.status === 'PENDENTE')
  const historico = solicitacoes.filter(s => s.status !== 'PENDENTE')

  return (
    <div className="sub-bg">
      <header className="sub-header">
        <span className="sub-logo" onClick={() => navigate('/dashboard')}>Voke</span>
        <div className="sub-header-right">
          <span className="sub-papel">{usuario?.papel}</span>
          <span className="sub-nome">{usuario?.nome}</span>
          <button className="sub-sair" onClick={handleSair}>Sair</button>
        </div>
      </header>

      <main className="sub-main">
        <button className="sub-voltar" onClick={() => navigate(-1)}>← Voltar</button>

        <div className="sub-topo">
          <div>
            <h1 className="sub-titulo">{sub.nome}</h1>
            <p className="sub-sub">{sub.descricao}</p>
            <div className="sub-card-meta" style={{ marginTop: '0.5rem' }}>
              <span className="sub-tag sub-tag--cat">{sub.categoria}</span>
              <span className={`sub-tag sub-tag--tipo-${sub.tipo.toLowerCase()}`}>{sub.tipo}</span>
              {sub.moderadorId && <span className="sub-tag sub-tag--moderado">⭐ Moderador definido</span>}
            </div>
          </div>
        </div>

        {erro && <div className="sub-erro">{erro}</div>}
        {mensagem && <div className="sub-erro" style={{ background: '#dcfce7', color: '#166534', borderColor: '#bbf7d0' }}>{mensagem}</div>}

        <div className="sub-abas">
          <button className={`sub-aba ${aba === 'membros' ? 'sub-aba--ativa' : ''}`}
            onClick={() => setAba('membros')}>
            Membros ({sub.membrosIds.length})
          </button>
          <button className={`sub-aba ${aba === 'regras' ? 'sub-aba--ativa' : ''}`}
            onClick={() => setAba('regras')}>
            Regras
          </button>
          {ehGestor && sub.tipo === 'FECHADO' && (
            <button className={`sub-aba ${aba === 'solicitacoes' ? 'sub-aba--ativa' : ''}`}
              onClick={() => setAba('solicitacoes')}>
              Solicitações {pendentes.length > 0 && `(${pendentes.length})`}
            </button>
          )}
          {(ehMembro || ehGestor) && (
            <button className={`sub-aba ${aba === 'chat' ? 'sub-aba--ativa' : ''}`}
              onClick={() => setAba('chat')}>
              Chat
            </button>
          )}
        </div>

        {aba === 'membros' && (
          <div>
            {sub.membrosIds.length === 0 && <p className="sub-info">Sem membros ainda.</p>}
            {sub.membrosIds.map((mid) => {
              const ehEsteModerador = sub.moderadorId === mid
              return (
                <div key={mid} className="sub-membro">
                  <span className="sub-membro-id">
                    {ehEsteModerador && '⭐ '}
                    {mid.substring(0, 8)}…
                  </span>
                  <div className="sub-membro-acoes">
                    {ehOrganizador && !ehEsteModerador && !sub.moderadorId && (
                      <button className="sub-btn-secundario" onClick={() => handlePromover(mid)}>
                        Promover a moderador
                      </button>
                    )}
                    {ehOrganizador && ehEsteModerador && (
                      <button className="sub-btn-secundario" onClick={handleRemoverModerador}>
                        Remover moderação
                      </button>
                    )}
                    {ehGestor && !ehEsteModerador && (
                      <button className="sub-btn-perigo" onClick={() => handleRemoverMembro(mid)}>
                        Remover
                      </button>
                    )}
                  </div>
                </div>
              )
            })}
          </div>
        )}

        {aba === 'regras' && (
          <div className="sub-form">
            {!editandoRegras ? (
              <>
                <h2 style={{ margin: 0, color: '#302b63', fontSize: '1.1rem' }}>Descrição</h2>
                <p style={{ color: '#444', margin: 0 }}>{sub.descricao || '(sem descrição)'}</p>
                <h2 style={{ margin: '0.8rem 0 0', color: '#302b63', fontSize: '1.1rem' }}>Regras</h2>
                <p style={{ color: '#444', whiteSpace: 'pre-wrap', margin: 0 }}>
                  {sub.regras || '(sem regras definidas)'}
                </p>
                {ehGestor && (
                  <button className="sub-btn-secundario" style={{ alignSelf: 'flex-start' }}
                    onClick={() => setEditandoRegras(true)}>
                    Editar
                  </button>
                )}
              </>
            ) : (
              <>
                <label className="sub-label">Descrição</label>
                <input className="sub-input" value={novaDescricao}
                  onChange={(e) => setNovaDescricao(e.target.value)} />
                <label className="sub-label">Regras</label>
                <textarea className="sub-textarea" rows={5} value={novaRegras}
                  onChange={(e) => setNovaRegras(e.target.value)} />
                <div style={{ display: 'flex', gap: '0.5rem' }}>
                  <button className="sub-btn-primario" onClick={handleSalvarRegras}>Salvar</button>
                  <button className="sub-btn-secundario" onClick={() => setEditandoRegras(false)}>Cancelar</button>
                </div>
              </>
            )}
          </div>
        )}

        {aba === 'solicitacoes' && (
          <div>
            <h3 style={{ color: '#302b63' }}>Pendentes</h3>
            {pendentes.length === 0 && <p className="sub-info">Sem solicitações pendentes.</p>}
            {pendentes.map((sol) => (
              <div key={sol.id} className="sub-solicitacao">
                <div className="sub-solicitacao-topo">
                  <span className="sub-solicitacao-id">👤 {sol.participanteId.substring(0, 8)}…</span>
                  <span className="sub-solicitacao-data">
                    {new Date(sol.dataSolicitacao).toLocaleString('pt-BR')}
                  </span>
                </div>
                {sol.mensagem && <p className="sub-solicitacao-msg">"{sol.mensagem}"</p>}
                <div className="sub-solicitacao-acoes">
                  <button className="sub-btn-primario" onClick={() => handleAprovar(sol.id)}>
                    Aprovar
                  </button>
                  <button className="sub-btn-perigo" onClick={() => handleRejeitar(sol.id)}>
                    Rejeitar
                  </button>
                </div>
              </div>
            ))}

            {historico.length > 0 && (
              <details style={{ marginTop: '1.5rem' }}>
                <summary style={{ cursor: 'pointer', color: '#6b21a8', fontWeight: 600 }}>
                  Histórico ({historico.length})
                </summary>
                <div style={{ marginTop: '0.6rem' }}>
                  {historico.map((sol) => (
                    <div key={sol.id} className="sub-solicitacao" style={{ opacity: 0.75 }}>
                      <div className="sub-solicitacao-topo">
                        <span className="sub-solicitacao-id">👤 {sol.participanteId.substring(0, 8)}…</span>
                        <span className={`sub-tag sub-tag--${sol.status === 'APROVADA' ? 'tipo-aberto' : 'tipo-fechado'}`}>
                          {sol.status}
                        </span>
                      </div>
                      {sol.mensagem && <p className="sub-solicitacao-msg">"{sol.mensagem}"</p>}
                    </div>
                  ))}
                </div>
              </details>
            )}
          </div>
        )}

        {aba === 'chat' && usuario && (ehMembro || ehGestor) && (
          <ChatCanal
            canalTipo="SUBGRUPO"
            canalId={sub.id}
            usuarioId={usuario.id}
            podeEnviar={true}
          />
        )}

        {ehOrganizador && (
          <div style={{ marginTop: '2rem', paddingTop: '1.5rem', borderTop: '1px solid #eee' }}>
            <button className="sub-btn-perigo" onClick={() => setConfirmarExcluir(true)}>
              Excluir subgrupo
            </button>
          </div>
        )}
      </main>

      {confirmarExcluir && (
        <div className="sub-modal-bg" onClick={() => setConfirmarExcluir(false)}>
          <div className="sub-modal" onClick={(e) => e.stopPropagation()}>
            <h2>Excluir subgrupo?</h2>
            <p>Esta ação não pode ser desfeita. Os membros serão desvinculados e as solicitações pendentes serão perdidas.</p>
            <div className="sub-modal-acoes">
              <button className="sub-btn-secundario" onClick={() => setConfirmarExcluir(false)}>Cancelar</button>
              <button className="sub-btn-perigo" onClick={handleExcluir}>Confirmar exclusão</button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
