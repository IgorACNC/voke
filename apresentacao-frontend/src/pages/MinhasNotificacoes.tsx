import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { listarMinhasNotificacoes, type Notificacao } from '../services/notificacaoService'
import { buscarEvento, type Evento } from '../services/eventoService'
import './MinhasNotificacoes.css'

export default function MinhasNotificacoes() {
  const navigate = useNavigate()
  const { sair } = useAuth()

  const [notificacoes, setNotificacoes] = useState<Notificacao[]>([])
  const [eventos, setEventos] = useState<Map<string, Evento>>(new Map())
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState('')

  useEffect(() => {
    carregar()
  }, [])

  async function carregar() {
    setCarregando(true)
    try {
      const notifs = await listarMinhasNotificacoes()
      // RN3: filtramos apenas notificações que foram realmente enviadas
      const enviadas = notifs.filter((n) => n.status === 'ENVIADA')
      setNotificacoes(enviadas.sort(
        (a, b) => new Date(b.dataEnvio).getTime() - new Date(a.dataEnvio).getTime()
      ))

      const idsEvento = Array.from(new Set(enviadas.map((n) => n.eventoId)))
      const map = new Map<string, Evento>()
      await Promise.all(idsEvento.map(async (id) => {
        try {
          const ev = await buscarEvento(id)
          map.set(id, ev)
        } catch { /* ignora */ }
      }))
      setEventos(map)
    } catch {
      setErro('Erro ao carregar notificações.')
    } finally {
      setCarregando(false)
    }
  }

  return (
    <div className="mn-bg">
      <header className="mn-header">
        <button className="mn-voltar" onClick={() => navigate('/dashboard')}>← Voltar</button>
        <span className="mn-logo">Voke</span>
        <button className="mn-sair" onClick={() => { sair(); navigate('/login') }}>Sair</button>
      </header>

      <main className="mn-main">
        <h1 className="mn-titulo">Notificações</h1>
        <p className="mn-sub">Avisos dos organizadores dos seus eventos</p>

        {erro && <p className="mn-erro">{erro}</p>}

        {carregando ? (
          <p className="mn-info">Carregando...</p>
        ) : notificacoes.length === 0 ? (
          <div className="mn-vazio">
            <span>🔔</span>
            <p>Nenhuma notificação ainda.</p>
            <small>Você receberá avisos quando se inscrever em eventos.</small>
          </div>
        ) : (
          <ul className="mn-lista">
            {notificacoes.map((n) => {
              const ev = eventos.get(n.eventoId)
              return (
                <li key={n.id} className="mn-item">
                  <div className="mn-item-topo">
                    {ev && (
                      <span
                        className="mn-evento-nome"
                        onClick={() => navigate(`/eventos/${n.eventoId}`)}
                      >
                        {ev.nome}
                      </span>
                    )}
                    {ev?.status === 'CANCELADO' && (
                      <span className="mn-badge-cancelado">EVENTO CANCELADO</span>
                    )}
                  </div>
                  <p className="mn-conteudo">{n.conteudo}</p>
                  <p className="mn-meta">{new Date(n.dataEnvio).toLocaleString('pt-BR')}</p>
                </li>
              )
            })}
          </ul>
        )}
      </main>
    </div>
  )
}
