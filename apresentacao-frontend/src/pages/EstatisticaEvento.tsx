import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import {
  consultarEstatisticaEvento, consultarCurvaVendas,
  exportarListaPresenca, exportarFinanceiro,
  type EstatisticaEventoResp, type PontoCurva,
} from '../services/dashboardService'
import KpiCard from '../components/KpiCard'
import CurvaVendasChart from '../components/CurvaVendasChart'
import './DashboardOrganizador.css'

export default function EstatisticaEvento() {
  const { id } = useParams<{ id: string }>()
  const { usuario, sair } = useAuth()
  const navigate = useNavigate()
  const [estatistica, setEstatistica] = useState<EstatisticaEventoResp | null>(null)
  const [curva, setCurva] = useState<PontoCurva[]>([])
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState('')

  useEffect(() => {
    if (!id) return
    Promise.all([consultarEstatisticaEvento(id), consultarCurvaVendas(id)])
      .then(([e, c]) => { setEstatistica(e); setCurva(c) })
      .catch(() => setErro('Erro ao carregar estatísticas.'))
      .finally(() => setCarregando(false))
  }, [id])

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
          <h1 className="dor-titulo">Estatística do Evento</h1>
          <p className="dor-sub">
            {estatistica?.nomeEvento ?? 'Carregando…'}
            {estatistica?.statusEvento === 'ENCERRADO' && (
              <span className="dor-tag dor-tag--encerrado" style={{ marginLeft: '0.5rem' }}>
                ENCERRADO (read-only)
              </span>
            )}
            {estatistica?.statusEvento === 'CANCELADO' && (
              <span className="dor-tag dor-tag--cancelado" style={{ marginLeft: '0.5rem' }}>
                CANCELADO (read-only)
              </span>
            )}
          </p>
        </div>

        {erro && <div className="dor-erro">{erro}</div>}
        {carregando && <p className="dor-info">Carregando...</p>}

        {estatistica && (
          <>
            <div className="dor-kpis">
              <KpiCard icon="🎫" label="Ingressos vendidos"
                       value={estatistica.ingressosVendidos}
                       hint="Inscrições pagas (CONFIRMADA)" />
              <KpiCard icon="💰" label="Receita consolidada" value={fmtBRL(estatistica.receitaConsolidada)} />
              <KpiCard icon="✅" label="Check-ins"
                       value={estatistica.checkInsRealizados}
                       hint={estatistica.ingressosVendidos > 0
                         ? `${Math.round(estatistica.checkInsRealizados / estatistica.ingressosVendidos * 100)}% de presença`
                         : 'Compareceram ao evento'} />
              <KpiCard icon="🏷️" label="Cupons usados" value={estatistica.cuponsUtilizados}
                       hint={`Desconto: ${fmtBRL(estatistica.descontoAcumulado)}`} />
              <KpiCard icon="👁️" label="Visualizações" value={estatistica.visualizacoes}
                       hint="Páginas vistas do evento" />
            </div>

            <div className="dor-secao">
              <h2 className="dor-secao-titulo">Curva de vendas</h2>
              <CurvaVendasChart pontos={curva} />
            </div>

            <div className="dor-secao" style={{ marginTop: '1.5rem' }}>
              <h2 className="dor-secao-titulo">Relatórios</h2>
              <p style={{ color: '#666', fontSize: '0.9rem', marginTop: 0 }}>
                Os relatórios respeitam a LGPD: o CPF aparece mascarado e dados sensíveis são omitidos.
              </p>
              <div className="dor-acoes">
                <button className="dor-btn-export" onClick={() => id && exportarListaPresenca(id)}>
                  📋 Lista de Presença (CSV)
                </button>
                <button className="dor-btn-export" onClick={() => id && exportarFinanceiro(id)}>
                  💼 Balanço Financeiro (CSV)
                </button>
                <button className="dor-btn-voltar" onClick={() => navigate('/dashboard-organizador')}>
                  ← Voltar ao Overview
                </button>
              </div>
            </div>
          </>
        )}
      </main>
    </div>
  )
}
