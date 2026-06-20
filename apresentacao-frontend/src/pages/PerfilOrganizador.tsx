import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { buscarPerfilPublico, type PerfilOrganizadorPublico } from '../services/organizadorService'
import { listarEventosPorOrganizador, type Evento } from '../services/eventoService'
import './PerfilOrganizador.css'

export default function PerfilOrganizador() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()

  const [organizador, setOrganizador] = useState<PerfilOrganizadorPublico | null>(null)
  const [eventos, setEventos] = useState<Evento[]>([])
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState('')

  useEffect(() => {
    if (!id) return
    Promise.all([
      buscarPerfilPublico(id),
      listarEventosPorOrganizador(id),
    ])
      .then(([org, evs]) => {
        setOrganizador(org)
        setEventos(evs)
      })
      .catch(() => setErro('Não foi possível carregar o perfil do organizador.'))
      .finally(() => setCarregando(false))
  }, [id])

  const ativos = eventos.filter((e) => e.status === 'ATIVO')
  const passados = eventos.filter((e) => e.status !== 'ATIVO')

  if (carregando) return <div className="org-bg"><p className="org-loading">Carregando...</p></div>

  return (
    <div className="org-bg">
      <header className="org-header">
        <button className="org-voltar" onClick={() => navigate(-1)}>← Voltar</button>
        <span className="org-logo">Voke</span>
        <div style={{ width: 90 }} />
      </header>

      <main className="org-main">
        {erro ? (
          <div className="org-card"><p className="org-erro">{erro}</p></div>
        ) : organizador && (
          <>
            <div className="org-card org-perfil">
              <div className="org-avatar">{organizador.nome.charAt(0).toUpperCase()}</div>
              <h1>{organizador.nome}</h1>
              <p className="org-email">{organizador.email}</p>
              <span className="org-papel">Organizador</span>
            </div>

            <section className="org-secao">
              <h2>Eventos ativos ({ativos.length})</h2>
              {ativos.length === 0 ? (
                <p className="org-vazio">Nenhum evento ativo no momento.</p>
              ) : (
                <div className="org-eventos-grid">
                  {ativos.map((ev) => (
                    <div key={ev.id} className="org-evento-card" onClick={() => navigate(`/eventos/${ev.id}`)}>
                      <h3>{ev.nome}</h3>
                      <p className="org-evento-local">📍 {ev.local}</p>
                      <p className="org-evento-data">📅 {new Date(ev.dataHoraInicio).toLocaleDateString('pt-BR')}</p>
                    </div>
                  ))}
                </div>
              )}
            </section>

            {passados.length > 0 && (
              <section className="org-secao">
                <h2>Eventos anteriores ({passados.length})</h2>
                <div className="org-eventos-grid org-eventos-passados">
                  {passados.map((ev) => (
                    <div key={ev.id} className="org-evento-card">
                      <h3>{ev.nome}</h3>
                      <p className="org-evento-status">{ev.status}</p>
                    </div>
                  ))}
                </div>
              </section>
            )}
          </>
        )}
      </main>
    </div>
  )
}
