import { useEffect, useMemo, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import Header from '../components/Header'
import { consultarOverview, type OverviewResp } from '../services/dashboardService'
import './DashboardOrganizador.css'

function fmtBRL(n: number) {
  return n.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })
}

function fmtInt(n: number) {
  return n.toLocaleString('pt-BR')
}

interface Status {
  label: string
  modifier: 'live' | 'closed' | 'cancelled'
}

function classifyStatus(status: string): Status {
  switch (status) {
    case 'CANCELADO':
    case 'REMOVIDO':
      return { label: 'cancelado', modifier: 'cancelled' }
    case 'ENCERRADO':
      return { label: 'encerrado', modifier: 'closed' }
    default:
      return { label: 'ao vivo', modifier: 'live' }
  }
}

export default function DashboardOrganizador() {
  const navigate = useNavigate()
  const [overview, setOverview] = useState<OverviewResp | null>(null)
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState('')

  useEffect(() => {
    consultarOverview()
      .then(setOverview)
      .catch(() => setErro('Não foi possível carregar o painel.'))
      .finally(() => setCarregando(false))
  }, [])

  const ativos = useMemo(
    () => overview?.porEvento.filter((e) => e.statusEvento === 'ATIVO').length ?? 0,
    [overview],
  )

  const eyebrow = overview ? (
    <span className="t-time">
      {ativos.toString().padStart(2, '0')} AO VIVO ·{' '}
      {overview.totalEventos.toString().padStart(2, '0')} TOTAIS
    </span>
  ) : (
    <span className="t-time tone-on-ink-soft">PAINEL</span>
  )

  return (
    <div className="dor">
      <Header eyebrow={eyebrow} />

      <main className="dor-main container--wide">
        <header className="dor-head">
          <p className="t-eyebrow tone-hush">Dashboard & Estatísticas</p>
          <h1 className="t-display">Visão geral dos eventos</h1>
          <p className="t-body tone-hush dor-head__sub">
            Acompanhe vendas, presença e receita em tempo real.
          </p>
        </header>

        {erro && <p className="t-body tone-ember dor-erro">{erro}</p>}
        {carregando && <p className="t-body tone-hush dor-info">Carregando painel…</p>}

        {overview && (
          <>
            {/* Régua de KPIs — tipografia carrega o peso */}
            <section className="dor-rail" aria-label="Indicadores gerais">
              <KpiBig
                kicker="Eventos publicados"
                value={fmtInt(overview.totalEventos)}
                hint={`${ativos} ativos`}
              />
              <KpiBig
                kicker="Ingressos vendidos"
                value={fmtInt(overview.totalIngressosVendidos)}
                hint="Inscrições pagas"
              />
              <KpiBig
                kicker="Receita acumulada"
                value={fmtBRL(overview.receitaTotal)}
                hint="Líquida"
                tone="spot"
              />
              <KpiBig
                kicker="Check-ins"
                value={fmtInt(overview.totalCheckIns)}
                hint="Compareceram"
              />
              <KpiBig
                kicker="Visualizações"
                value={fmtInt(overview.totalVisualizacoes)}
                hint="Página pública"
              />
            </section>

            <section className="dor-eventos">
              <div className="dor-eventos__head">
                <div>
                  <p className="t-eyebrow tone-hush">Eventos</p>
                  <h2 className="t-h2">Por evento</h2>
                </div>
                <span className="t-time tone-hush">
                  {overview.porEvento.length.toString().padStart(2, '0')} registrados
                </span>
              </div>

              {overview.porEvento.length === 0 ? (
                <div className="dor-empty">
                  <p className="t-h3">Sem eventos publicados ainda</p>
                  <p className="t-body tone-hush">Crie seu primeiro evento e os indicadores aparecem aqui.</p>
                  <button
                    type="button"
                    className="btn btn--primary"
                    onClick={() => navigate('/meus-eventos/novo')}
                  >
                    Criar evento
                  </button>
                </div>
              ) : (
                <ul className="dor-lista">
                  {overview.porEvento.map((e) => {
                    const st = classifyStatus(e.statusEvento)
                    return (
                      <li key={e.eventoId} className={`dor-linha dor-linha--${st.modifier}`}>
                        <div className="dor-linha__nome">
                          <p className="t-h3">{e.nomeEvento}</p>
                          <p className="t-time tone-hush">{st.label.toUpperCase()}</p>
                        </div>

                        <Stat label="Vendidos" value={fmtInt(e.ingressosVendidos)} />
                        <Stat label="Receita" value={fmtBRL(e.receitaConsolidada)} highlight />
                        <Stat label="Check-ins" value={fmtInt(e.checkInsRealizados)} />
                        <Stat label="Views" value={fmtInt(e.visualizacoes)} />

                        <div className="dor-linha__cta">
                          <button
                            type="button"
                            className="btn btn--ghost btn--sm"
                            onClick={() => navigate(`/dashboard-organizador/eventos/${e.eventoId}`)}
                          >
                            Detalhar
                          </button>
                        </div>
                      </li>
                    )
                  })}
                </ul>
              )}
            </section>
          </>
        )}
      </main>
    </div>
  )
}

function KpiBig({
  kicker, value, hint, tone,
}: { kicker: string; value: string; hint?: string; tone?: 'spot' }) {
  return (
    <div className="dor-kpi">
      <p className="t-eyebrow tone-hush dor-kpi__kicker">{kicker}</p>
      <p className={`t-mega dor-kpi__value ${tone === 'spot' ? 'tone-spot' : ''}`}>{value}</p>
      {hint && <p className="t-meta tone-hush">{hint}</p>}
    </div>
  )
}

function Stat({ label, value, highlight }: { label: string; value: string; highlight?: boolean }) {
  return (
    <div className="dor-stat">
      <p className="t-eyebrow tone-hush">{label}</p>
      <p className={`t-time-lg ${highlight ? 'tone-spot' : 'tone-ink'} dor-stat__value`}>{value}</p>
    </div>
  )
}
