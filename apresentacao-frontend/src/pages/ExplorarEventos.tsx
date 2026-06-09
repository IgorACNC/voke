import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { listarEventosAtivos, type Evento } from '../services/eventoService'
import './ExplorarEventos.css'

export default function ExplorarEventos() {
  const { usuario, sair } = useAuth()
  const navigate = useNavigate()
  const [eventos, setEventos] = useState<Evento[]>([])
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState('')
  const [busca, setBusca] = useState('')

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
                {ev.idadeMinima > 0 && (
                  <span className="exp-badge-idade">{ev.idadeMinima}+</span>
                )}
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
                <div>
                  <button
                    className="exp-btn-grupo"
                    onClick={() => navigate(`/eventos/${ev.id}/grupo`)}
                  >
                    Ver Grupo
                  </button>
                  <button className="exp-btn-comprar" onClick={() => navigate(`/eventos/${ev.id}`)}>
                    Ver / Comprar
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      </main>
    </div>
  )
}
