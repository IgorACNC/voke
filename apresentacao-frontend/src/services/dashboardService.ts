import api from './api'

export interface EstatisticaEventoResp {
  eventoId: string
  nomeEvento: string
  statusEvento: 'ATIVO' | 'CANCELADO' | 'ENCERRADO' | 'RASCUNHO' | 'PUBLICADO' | 'REMOVIDO'
  ingressosVendidos: number
  receitaConsolidada: number
  checkInsRealizados: number
  ausencias: number
  cuponsUtilizados: number
  descontoAcumulado: number
  visualizacoes: number
  congelado: boolean
  atualizadoEm: string
}

export interface OverviewResp {
  totalEventos: number
  totalIngressosVendidos: number
  receitaTotal: number
  totalCheckIns: number
  totalVisualizacoes: number
  porEvento: EstatisticaEventoResp[]
}

export interface PontoCurva {
  data: string
  ingressos: number
  receita: number
}

export async function consultarOverview(): Promise<OverviewResp> {
  const { data } = await api.get<OverviewResp>('/dashboard/overview')
  return data
}

export async function consultarEstatisticaEvento(eventoId: string): Promise<EstatisticaEventoResp> {
  const { data } = await api.get<EstatisticaEventoResp>(`/dashboard/eventos/${eventoId}`)
  return data
}

export async function consultarCurvaVendas(eventoId: string): Promise<PontoCurva[]> {
  const { data } = await api.get<PontoCurva[]>(`/dashboard/eventos/${eventoId}/curva-vendas`)
  return data
}

export async function registrarVisualizacao(eventoId: string): Promise<void> {
  await api.post(`/dashboard/eventos/${eventoId}/visualizar`)
}

async function baixarCsv(url: string, filename: string) {
  const resp = await api.get(url, { responseType: 'blob' })
  const blobUrl = window.URL.createObjectURL(new Blob([resp.data]))
  const link = document.createElement('a')
  link.href = blobUrl
  link.download = filename
  document.body.appendChild(link)
  link.click()
  link.remove()
  window.URL.revokeObjectURL(blobUrl)
}

export function exportarListaPresenca(eventoId: string) {
  return baixarCsv(`/dashboard/eventos/${eventoId}/lista-presenca.csv`, `lista-presenca-${eventoId}.csv`)
}

export function exportarFinanceiro(eventoId: string) {
  return baixarCsv(`/dashboard/eventos/${eventoId}/financeiro.csv`, `financeiro-${eventoId}.csv`)
}
