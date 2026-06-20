import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { listarEventosAtivos, type Evento } from '../services/eventoService'
import './CatalogoPublico.css'

export default function CatalogoPublico() {
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

  return (
    <div className="cat-bg">
      <header className="cat-header">
        <span className="cat-logo">Voke</span>
        <div className="cat-header-right">
          <button className="cat-btn-outline" onClick={() => navigate('/login')}>Entrar</button>
          <button className="cat-btn-primary" onClick={() => navigate('/login')}>Criar conta</button>
        </div>
      </header>

      <div className="cat-banner">
        <p>
          Você está navegando como visitante. <strong>Faça login</strong> para se inscrever em eventos e participar de grupos.
          <button className="cat-banner-btn" onClick={() => navigate('/login')}>Entrar agora</button>
        </p>
      </div>

      <main className="cat-main">
        <div className="cat-topo">
          <div>
            <h1 className="cat-titulo">Eventos em destaque</h1>
            <p className="cat-sub">Descubra o que está acontecendo</p>
          </div>
          <span className="cat-total">{eventosFiltrados.length} evento{eventosFiltrados.length !== 1 ? 's' : ''}</span>
        </div>

        <input
          className="cat-busca"
          placeholder="Buscar por nome ou local..."
          value={busca}
          onChange={(e) => setBusca(e.target.value)}
        />

        {erro && <div className="cat-erro">{erro}</div>}
        {carregando && <p className="cat-info">Carregando eventos...</p>}
        {!carregando && eventosFiltrados.length === 0 && (
          <p className="cat-info">Nenhum evento encontrado.</p>
        )}

        <div className="cat-lista">
          {eventosFiltrados.map((ev) => (
            <div key={ev.id} className="cat-card">
              <div className="cat-card-topo">
                <div>
                  <h2 className="cat-card-nome">{ev.nome}</h2>
                  <p className="cat-card-local">📍 {ev.local}</p>
                </div>
                {ev.idadeMinima > 0 && (
                  <span className="cat-badge-idade">{ev.idadeMinima}+</span>
                )}
              </div>

              {ev.descricao && (
                <p className="cat-card-desc">{ev.descricao}</p>
              )}

              <div className="cat-card-info">
                <span className="cat-data">
                  🗓️ {new Date(ev.dataHoraInicio).toLocaleDateString('pt-BR', {
                    day: '2-digit', month: 'short', year: 'numeric',
                  })}
                  {' às '}
                  {new Date(ev.dataHoraInicio).toLocaleTimeString('pt-BR', {
                    hour: '2-digit', minute: '2-digit',
                  })}
                </span>
                {ev.loteAtual && (
                  <span className="cat-preco">R$ {ev.loteAtual.preco.toFixed(2)}</span>
                )}
              </div>

              {ev.loteAtual && (
                <p className="cat-vagas">
                  {ev.loteAtual.quantidadeTotal - ev.loteAtual.quantidadeVendida} vagas disponíveis
                </p>
              )}

              <button className="cat-btn-inscricao" onClick={() => navigate('/login')}>
                Entrar para se inscrever
              </button>
            </div>
          ))}
        </div>
      </main>
    </div>
  )
}
