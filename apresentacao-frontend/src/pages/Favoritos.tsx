import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import {
  listarColecoes, criarColecao, editarColecao, excluirColecao,
  buscarColecao, removerEventoColecao, moverEventoColecao, duplicarColecao,
  type ColecaoResumo, type ColecaoDetalhe,
} from '../services/colecaoService'
import './Favoritos.css'

type Vista = 'lista' | 'detalhe'

export default function Favoritos() {
  const { usuario, sair } = useAuth()
  const navigate = useNavigate()

  const [colecoes, setColecoes] = useState<ColecaoResumo[]>([])
  const [colecaoAtiva, setColecaoAtiva] = useState<ColecaoDetalhe | null>(null)
  const [vista, setVista] = useState<Vista>('lista')
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState('')

  // Modal criar/editar
  const [modalAberto, setModalAberto] = useState(false)
  const [modoEdicao, setModoEdicao] = useState(false)
  const [colecaoEditandoId, setColecaoEditandoId] = useState<string | null>(null)
  const [formNome, setFormNome] = useState('')
  const [salvando, setSalvando] = useState(false)
  const [erroModal, setErroModal] = useState('')

  // Modal mover evento
  const [modalMoverAberto, setModalMoverAberto] = useState(false)
  const [eventoParaMover, setEventoParaMover] = useState('')
  const [destinoMover, setDestinoMover] = useState('')

  useEffect(() => {
    carregarColecoes()
  }, [])

  async function carregarColecoes() {
    setCarregando(true)
    try {
      const data = await listarColecoes()
      setColecoes(data)
    } catch {
      setErro('Erro ao carregar coleções.')
    } finally {
      setCarregando(false)
    }
  }

  async function abrirColecao(id: string) {
    try {
      const detalhe = await buscarColecao(id)
      setColecaoAtiva(detalhe)
      setVista('detalhe')
    } catch {
      setErro('Erro ao abrir coleção.')
    }
  }

  function abrirModalCriar() {
    setModoEdicao(false)
    setColecaoEditandoId(null)
    setFormNome('')
    setErroModal('')
    setModalAberto(true)
  }

  function abrirModalEditar(id: string, nomeAtual: string) {
    setModoEdicao(true)
    setColecaoEditandoId(id)
    setFormNome(nomeAtual)
    setErroModal('')
    setModalAberto(true)
  }

  async function salvarColecao() {
    if (!formNome.trim()) { setErroModal('Nome é obrigatório.'); return }
    setSalvando(true)
    setErroModal('')
    try {
      if (modoEdicao && colecaoEditandoId) {
        await editarColecao(colecaoEditandoId, formNome, 'PRIVADA')
        if (colecaoAtiva?.id === colecaoEditandoId) {
          const atualizado = await buscarColecao(colecaoEditandoId)
          setColecaoAtiva(atualizado)
        }
      } else {
        await criarColecao(formNome, 'PRIVADA')
      }
      setModalAberto(false)
      await carregarColecoes()
    } catch (e: any) {
      setErroModal(e?.response?.data?.mensagem ?? 'Erro ao salvar coleção.')
    } finally {
      setSalvando(false)
    }
  }

  async function handleExcluir(id: string) {
    if (!confirm('Excluir esta coleção? Todos os favoritos dela serão removidos.')) return
    try {
      await excluirColecao(id)
      if (colecaoAtiva?.id === id) { setColecaoAtiva(null); setVista('lista') }
      await carregarColecoes()
    } catch {
      setErro('Erro ao excluir coleção.')
    }
  }

  async function handleRemoverEvento(eventoId: string) {
    if (!colecaoAtiva) return
    try {
      const atualizado = await removerEventoColecao(colecaoAtiva.id, eventoId)
      setColecaoAtiva(atualizado)
      await carregarColecoes()
    } catch (e: any) {
      setErro(e?.response?.data?.mensagem ?? 'Erro ao remover evento.')
    }
  }

  function abrirModalMover(eventoId: string) {
    setEventoParaMover(eventoId)
    setDestinoMover('')
    setModalMoverAberto(true)
  }

  async function confirmarMover() {
    if (!colecaoAtiva || !destinoMover) return
    try {
      await moverEventoColecao(colecaoAtiva.id, destinoMover, eventoParaMover)
      const atualizado = await buscarColecao(colecaoAtiva.id)
      setColecaoAtiva(atualizado)
      setModalMoverAberto(false)
      await carregarColecoes()
    } catch (e: any) {
      setErro(e?.response?.data?.mensagem ?? 'Erro ao mover evento.')
    }
  }

  async function handleDuplicar(id: string) {
    try {
      await duplicarColecao(id)
      await carregarColecoes()
    } catch (e: any) {
      setErro(e?.response?.data?.mensagem ?? 'Erro ao duplicar coleção.')
    }
  }

  function handleSair() { sair(); navigate('/login') }

  function formatarData(iso: string) {
    return new Date(iso).toLocaleDateString('pt-BR', { day: '2-digit', month: 'short', year: 'numeric' })
  }

  return (
    <div className="fav-bg">
      <header className="fav-header">
        <span className="fav-logo" onClick={() => navigate('/dashboard')}>Voke</span>
        <div className="fav-header-right">
          <span className="fav-papel">{usuario?.papel}</span>
          <span className="fav-nome">{usuario?.nome}</span>
          <button className="fav-sair" onClick={handleSair}>Sair</button>
        </div>
      </header>

      <main className="fav-main">
        {vista === 'lista' ? (
          <>
            <div className="fav-topo">
              <div>
                <h1 className="fav-titulo">Minhas Coleções</h1>
                <p className="fav-sub">Organize seus eventos favoritos em coleções personalizadas</p>
              </div>
              <button className="fav-btn-criar" onClick={abrirModalCriar}>+ Nova Coleção</button>
            </div>

            {erro && <div className="fav-erro">{erro}</div>}
            {carregando && <p className="fav-info">Carregando...</p>}

            {!carregando && colecoes.length === 0 && (
              <div className="fav-empty">
                <div className="fav-empty-icon">⭐</div>
                <h2>Nenhuma coleção ainda</h2>
                <p>Crie sua primeira coleção e comece a organizar seus eventos favoritos</p>
                <button className="fav-btn-criar" onClick={abrirModalCriar}>Criar primeira coleção</button>
              </div>
            )}

            <div className="fav-grid">
              {colecoes.map((c) => (
                <div key={c.id} className="fav-card" onClick={() => abrirColecao(c.id)}>
                  <div className="fav-card-topo">
                    <h3 className="fav-card-nome">{c.nome}</h3>
                  </div>
                  <p className="fav-card-qtd">
                    {c.quantidadeItens} {c.quantidadeItens === 1 ? 'evento' : 'eventos'}
                  </p>
                  <p className="fav-card-data">Criada em {formatarData(c.dataCriacao)}</p>
                  <div className="fav-card-acoes" onClick={(e) => e.stopPropagation()}>
                    <button
                      className="fav-btn-editar"
                      onClick={() => abrirModalEditar(c.id, c.nome)}
                    >
                      Editar
                    </button>
                    <button className="fav-btn-duplicar" onClick={() => handleDuplicar(c.id)}>
                      Duplicar
                    </button>
                    <button className="fav-btn-excluir" onClick={() => handleExcluir(c.id)}>
                      Excluir
                    </button>
                  </div>
                </div>
              ))}
            </div>
          </>
        ) : (
          <>
            <div className="fav-topo">
              <div className="fav-topo-detalhe">
                <button className="fav-btn-voltar" onClick={() => { setVista('lista'); setColecaoAtiva(null) }}>
                  ← Voltar
                </button>
                <div>
                  <h1 className="fav-titulo">{colecaoAtiva?.nome}</h1>
                  <p className="fav-sub">
                    {colecaoAtiva?.itens.length ?? 0} {colecaoAtiva?.itens.length === 1 ? 'evento' : 'eventos'}
                  </p>
                </div>
              </div>
              <div style={{ display: 'flex', gap: '0.5rem' }}>
                <button
                  className="fav-btn-editar"
                  onClick={() => colecaoAtiva && abrirModalEditar(colecaoAtiva.id, colecaoAtiva.nome)}
                >
                  Editar nome
                </button>
                <button className="fav-btn-excluir" onClick={() => colecaoAtiva && handleExcluir(colecaoAtiva.id)}>
                  Excluir coleção
                </button>
              </div>
            </div>

            {erro && <div className="fav-erro">{erro}</div>}

            {colecaoAtiva?.itens.length === 0 && (
              <div className="fav-empty">
                <div className="fav-empty-icon">🎟️</div>
                <h2>Coleção vazia</h2>
                <p>Adicione eventos a esta coleção explorando os eventos disponíveis</p>
                <button className="fav-btn-criar" onClick={() => navigate('/explorar-eventos')}>
                  Explorar Eventos
                </button>
              </div>
            )}

            <div className="fav-itens-lista">
              {colecaoAtiva?.itens.map((item) => (
                <div key={item.eventoId} className="fav-item-card">
                  <div className="fav-item-info">
                    <div>
                      <h3 className="fav-item-nome">{item.nomeEvento}</h3>
                      <p className="fav-item-local">{item.local}</p>
                      {item.dataHoraInicio && (
                        <p className="fav-item-data">
                          {new Date(item.dataHoraInicio).toLocaleDateString('pt-BR', {
                            day: '2-digit', month: 'short', year: 'numeric',
                          })}
                        </p>
                      )}
                    </div>
                    <span className="fav-item-ordem">#{item.ordem}</span>
                  </div>
                  <div className="fav-item-acoes">
                    <button className="fav-btn-ver"
                      onClick={() => navigate(`/eventos/${item.eventoId}`)}>
                      Ver Evento
                    </button>
                    {colecoes.length > 1 && (
                      <button className="fav-btn-mover" onClick={() => abrirModalMover(item.eventoId)}>
                        Mover
                      </button>
                    )}
                    <button className="fav-btn-remover-item"
                      onClick={() => handleRemoverEvento(item.eventoId)}>
                      Remover
                    </button>
                  </div>
                </div>
              ))}
            </div>
          </>
        )}
      </main>

      {/* Modal Criar / Editar Coleção */}
      {modalAberto && (
        <div className="fav-overlay" onClick={() => setModalAberto(false)}>
          <div className="fav-modal" onClick={(e) => e.stopPropagation()}>
            <h2 className="fav-modal-titulo">
              {modoEdicao ? 'Editar Coleção' : 'Nova Coleção'}
            </h2>
            <label className="fav-label">Nome da coleção</label>
            <input
              className="fav-input"
              placeholder="Ex: Shows 2025, Festivais..."
              value={formNome}
              onChange={(e) => setFormNome(e.target.value)}
              onKeyDown={(e) => e.key === 'Enter' && salvarColecao()}
              autoFocus
            />
            {erroModal && <p className="fav-erro-modal">{erroModal}</p>}
            <div className="fav-modal-acoes">
              <button className="fav-btn-cancelar" onClick={() => setModalAberto(false)}>
                Cancelar
              </button>
              <button className="fav-btn-salvar" onClick={salvarColecao} disabled={salvando}>
                {salvando ? 'Salvando...' : 'Salvar'}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Modal Mover Evento */}
      {modalMoverAberto && (
        <div className="fav-overlay" onClick={() => setModalMoverAberto(false)}>
          <div className="fav-modal" onClick={(e) => e.stopPropagation()}>
            <h2 className="fav-modal-titulo">Mover para outra coleção</h2>
            <label className="fav-label">Selecione a coleção de destino</label>
            <select className="fav-select" value={destinoMover}
              onChange={(e) => setDestinoMover(e.target.value)}>
              <option value="">-- Escolha uma coleção --</option>
              {colecoes
                .filter((c) => c.id !== colecaoAtiva?.id)
                .map((c) => (
                  <option key={c.id} value={c.id}>{c.nome}</option>
                ))}
            </select>
            <div className="fav-modal-acoes">
              <button className="fav-btn-cancelar" onClick={() => setModalMoverAberto(false)}>
                Cancelar
              </button>
              <button className="fav-btn-salvar" onClick={confirmarMover} disabled={!destinoMover}>
                Mover
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
