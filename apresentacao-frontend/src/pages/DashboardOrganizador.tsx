import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { consultarOverview, type OverviewResp } from '../services/dashboardService'
import KpiCard from '../components/KpiCard'
import './DashboardOrganizador.css'

export default function DashboardOrganizador() {
  const { usuario, sair } = useAuth()
  const navigate = useNavigate()
  const [overview, setOverview] = useState<OverviewResp | null>(null)
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState('')

  useEffect(() => {
    consultarOverview()
      .then(setOverview)
      .catch(() => setErro('Erro ao carregar overview.'))
      .finally(() => setCarregando(false))
  }, [])

  function handleSair() { sair(); navigate('/login') }

  const fmtBRL = (n: number) =>
    new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(n)

  return (
    <div className="dor-bg">
      <header className="dor-header">
        <span className="dor-logo" onClick={() => navigate('/dashboard')}>Voke</span>
        <div className="dor-header-right">
          <span className="dor-papel">{usuario?.papel}</span>
          <span className="dor-nome">{usuario?.nome}</span>
          <button className="dor-sair" onClick={handleSair}>Sair</button>
        </div>
      </header>

      <main className="dor-main">
        <div className="dor-topo">
          <h1 className="dor-titulo">Dashboard & Estatísticas</h1>
          <p className="dor-sub">Visão analítica dos seus eventos</p>
        </div>

        {erro && <div className="dor-erro">{erro}</div>}
        {carregando && <p className="dor-info">Carregando...</p>}

        {overview && (
          <>
            <div className="dor-kpis">
              <KpiCard icon="📅" label="Eventos" value={overview.totalEventos} />
              <KpiCard icon="🎫" label="Ingressos vendidos"
                       value={overview.totalIngressosVendidos}
                       hint="Inscrições pagas" />
              <KpiCard icon="💰" label="Receita total" value={fmtBRL(overview.receitaTotal)} />
              <KpiCard icon="✅" label="Check-ins" value={overview.totalCheckIns}
                       hint="Compareceram ao evento" />
              <KpiCard icon="👁️" label="Visualizações"
                       value={overview.totalVisualizacoes}
                       hint="Páginas vistas" />
            </div>

            <div className="dor-secao">
              <h2 className="dor-secao-titulo">Por evento</h2>
              {overview.porEvento.length === 0 ? (
                <p className="dor-info">Nenhuma estatística ainda. Publique um evento e venda ingressos!</p>
              ) : (
                <table className="dor-tabela">
                  <thead>
                    <tr>
                      <th>Evento</th>
                      <th>Vendidos</th>
                      <th>Receita</th>
                      <th>Check-ins</th>
                      <th>Visualizações</th>
                      <th>Status</th>
                      <th></th>
                    </tr>
                  </thead>
                  <tbody>
                    {overview.porEvento.map((e) => (
                      <tr key={e.eventoId}>
                        <td>{e.nomeEvento}</td>
                        <td>{e.ingressosVendidos}</td>
                        <td>{fmtBRL(e.receitaConsolidada)}</td>
                        <td>{e.checkInsRealizados}</td>
                        <td>{e.visualizacoes}</td>
                        <td>
                          {e.statusEvento === 'ATIVO' && <span className="dor-tag dor-tag--ativo">ATIVO</span>}
                          {e.statusEvento === 'ENCERRADO' && <span className="dor-tag dor-tag--encerrado">ENCERRADO</span>}
                          {e.statusEvento === 'CANCELADO' && <span className="dor-tag dor-tag--cancelado">CANCELADO</span>}
                          {(e.statusEvento === 'RASCUNHO' || e.statusEvento === 'PUBLICADO') &&
                            <span className="dor-tag dor-tag--ativo">{e.statusEvento}</span>}
                          {e.statusEvento === 'REMOVIDO' &&
                            <span className="dor-tag dor-tag--cancelado">REMOVIDO</span>}
                        </td>
                        <td>
                          <button className="dor-btn-ver"
                                  onClick={() => navigate(`/dashboard-organizador/eventos/${e.eventoId}`)}>
                            Detalhar
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              )}
            </div>
          </>
        )}
      </main>
    </div>
  )
}
