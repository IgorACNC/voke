import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { listarEventosAtivos, type Evento } from '../services/eventoService'
import {
  listarColecoes, criarColecao, adicionarEventoColecao,
  type ColecaoResumo,
} from '../services/colecaoService'
import './ExplorarEventos.css'

export default function ExplorarEventos() {
  const { usuario, sair } = useAuth()
  const navigate = useNavigate()
  const [eventos, setEventos] = useState<Evento[]>([])
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState('')
  const [busca, setBusca] = useState('')

  // Modal favoritar
  const [modalFavAberto, setModalFavAberto] = useState(false)
  const [eventoParaFav, setEventoParaFav] = useState<Evento | null>(null)
  const [colecoes, setColecoes] = useState<ColecaoResumo[]>([])
  const [colecaoSelecionada, setColecaoSelecionada] = useState('')
  const [criarNova, setCriarNova] = useState(false)
  const [novoNomeColecao, setNovoNomeColecao] = useState('')
  const [salvandoFav, setSalvandoFav] = useState(false)
  const [erroFav, setErroFav] = useState('')
  const [sucessoFav, setSucessoFav] = useState('')

  useEffect(() => {
    listarEventosAtivos()
      .then(setEventos)
      .catch(() => setErro('Erro ao carregar eventos.'))
      .finally(() => setCarregando(false))
  }, [])

  const eventosFiltrados = eventos.filter((ev) =>
    ev.nome.toLowerCase().includes(busca.toLowerCase()) ||
    ev.local.toLowerCase().includes(busca.toLowerCase())
  )

  async function abrirModalFavoritar(ev: Evento) {
    setEventoParaFav(ev)
    setColecaoSelecionada('')
    setCriarNova(false)
    setNovoNomeColecao('')
    setErroFav('')
    setSucessoFav('')
    setModalFavAberto(true)
    try {
      const data = await listarColecoes()
      setColecoes(data)
      if (data.length === 0) setCriarNova(true)
    } catch {
      setErroFav('Erro ao carregar coleções.')
    }
  }

  async function confirmarFavoritar() {
    if (!eventoParaFav) return
    setSalvandoFav(true)
    setErroFav('')
    try {
      let idColecao = colecaoSelecionada
      if (criarNova) {
        if (!novoNomeColecao.trim()) { setErroFav('Informe o nome da nova coleção.'); setSalvandoFav(false); return }
        const nova = await criarColecao(novoNomeColecao, 'PRIVADA')
        idColecao = nova.id
      }
      if (!idColecao) { setErroFav('Selecione ou crie uma coleção.'); setSalvandoFav(false); return }
      await adicionarEventoColecao(idColecao, eventoParaFav.id)
      setSucessoFav(`"${eventoParaFav.nome}" adicionado à coleção!`)
      setTimeout(() => setModalFavAberto(false), 1500)
    } catch (e: any) {
      setErroFav(e?.response?.data?.mensagem ?? 'Erro ao favoritar evento.')
    } finally {
      setSalvandoFav(false)
    }
  }

  function handleSair() { sair(); navigate('/login') }

  return (
    <div className="exp-bg">
      <header className="exp-header">
        <span className="exp-logo" onClick={() => navigate('/dashboard')}>Voke</span>
        <div className="exp-header-right">
          <span className="exp-papel">{usuario?.papel}</span>
          <span className="exp-nome">{usuario?.nome}</span>
          <button className="exp-sair" onClick={handleSair}>Sair</button>
        </div>
      </header>

      <main className="exp-main">
        <div className="exp-topo">
          <div>
            <h1 className="exp-titulo">Explorar Eventos</h1>
            <p className="exp-sub">Encontre eventos e garanta seu lugar</p>
          </div>
        </div>

        <input
          className="exp-busca"
          placeholder="Buscar por nome ou local..."
          value={busca}
          onChange={(e) => setBusca(e.target.value)}
        />

        {erro && <div className="exp-erro">{erro}</div>}
        {carregando && <p className="exp-info">Carregando eventos...</p>}
        {!carregando && eventosFiltrados.length === 0 && (
          <p className="exp-info">Nenhum evento encontrado.</p>
        )}

        <div className="exp-lista">
          {eventosFiltrados.map((ev) => (
            <div key={ev.id} className="exp-card">
              <div className="exp-card-topo">
                <div>
                  <h2 className="exp-card-nome">{ev.nome}</h2>
                  <p className="exp-card-local">{ev.local}</p>
                </div>
                <div className="exp-card-topo-right">
                  {ev.idadeMinima > 0 && (
                    <span className="exp-badge-idade">{ev.idadeMinima}+</span>
                  )}
                  <button
                    className="exp-btn-fav"
                    title="Salvar em coleção"
                    onClick={() => abrirModalFavoritar(ev)}
                  >
                    ⭐
                  </button>
                </div>
              </div>

              {ev.descricao && (
                <p className="exp-card-desc">{ev.descricao}</p>
              )}

              <div className="exp-card-info">
                <span>
                  {new Date(ev.dataHoraInicio).toLocaleDateString('pt-BR', {
                    day: '2-digit', month: 'short', year: 'numeric',
                  })}
                  {' — '}
                  {new Date(ev.dataHoraInicio).toLocaleTimeString('pt-BR', {
                    hour: '2-digit', minute: '2-digit',
                  })}
                </span>
                {ev.loteAtual && (
                  <span className="exp-preco">
                    R$ {ev.loteAtual.preco.toFixed(2)}
                  </span>
                )}
              </div>

              {ev.loteAtual && (
                <p className="exp-vagas">
                  {ev.loteAtual.quantidadeTotal - ev.loteAtual.quantidadeVendida} vagas disponíveis
                </p>
              )}

              <div className="exp-card-acoes">
                <button
                  className="exp-btn-grupo"
                  onClick={() => navigate(`/eventos/${ev.id}/grupo`)}
                >
                  Ver Grupo
                </button>
                <button
                  className="exp-btn-comprar"
                  onClick={() => navigate(`/eventos/${ev.id}`)}
                >
                  Ver / Comprar
                </button>
              </div>
            </div>
          ))}
        </div>
      </main>

      {/* Modal Favoritar */}
      {modalFavAberto && (
        <div className="exp-overlay" onClick={() => setModalFavAberto(false)}>
          <div className="exp-modal" onClick={(e) => e.stopPropagation()}>
            <h2 className="exp-modal-titulo">Salvar em coleção</h2>
            <p className="exp-modal-evento">"{eventoParaFav?.nome}"</p>

            {sucessoFav ? (
              <p className="exp-modal-sucesso">{sucessoFav}</p>
            ) : (
              <>
                {colecoes.length > 0 && !criarNova && (
                  <>
                    <label className="exp-label">Escolha uma coleção</label>
                    <select
                      className="exp-select"
                      value={colecaoSelecionada}
                      onChange={(e) => setColecaoSelecionada(e.target.value)}
                    >
                      <option value="">-- Selecione --</option>
                      {colecoes.map((c) => (
                        <option key={c.id} value={c.id}>
                          {c.nome} ({c.quantidadeItens} eventos)
                        </option>
                      ))}
                    </select>
                    <button className="exp-btn-nova-colecao" onClick={() => { setCriarNova(true); setColecaoSelecionada('') }}>
                      + Criar nova coleção
                    </button>
                  </>
                )}

                {(criarNova || colecoes.length === 0) && (
                  <>
                    <label className="exp-label">Nome da nova coleção</label>
                    <input
                      className="exp-input"
                      placeholder="Ex: Shows 2025..."
                      value={novoNomeColecao}
                      onChange={(e) => setNovoNomeColecao(e.target.value)}
                      autoFocus
                    />
                    {colecoes.length > 0 && (
                      <button className="exp-btn-nova-colecao" onClick={() => setCriarNova(false)}>
                        ← Usar coleção existente
                      </button>
                    )}
                  </>
                )}

                {erroFav && <p className="exp-erro-modal">{erroFav}</p>}

                <div className="exp-modal-acoes">
                  <button className="exp-btn-cancelar" onClick={() => setModalFavAberto(false)}>
                    Cancelar
                  </button>
                  <button className="exp-btn-salvar" onClick={confirmarFavoritar} disabled={salvandoFav}>
                    {salvandoFav ? 'Salvando...' : 'Salvar'}
                  </button>
                </div>
              </>
            )}
          </div>
        </div>
      )}
    </div>
  )
}
