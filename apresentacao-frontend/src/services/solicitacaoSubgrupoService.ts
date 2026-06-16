import api from './api'

export type StatusSolicitacao = 'PENDENTE' | 'APROVADA' | 'REJEITADA'

export interface SolicitacaoSubgrupo {
  id: string
  subgrupoId: string
  participanteId: string
  mensagem: string
  status: StatusSolicitacao
  dataSolicitacao: string
  dataDecisao: string | null
  decididoPor: string | null
}

export async function solicitarEntrada(subgrupoId: string, mensagem: string): Promise<SolicitacaoSubgrupo> {
  const { data } = await api.post<SolicitacaoSubgrupo>(
    `/subgrupos/${subgrupoId}/solicitacoes`, { mensagem }
  )
  return data
}

export async function listarSolicitacoesDoSubgrupo(subgrupoId: string): Promise<SolicitacaoSubgrupo[]> {
  const { data } = await api.get<SolicitacaoSubgrupo[]>(`/subgrupos/${subgrupoId}/solicitacoes`)
  return data
}

export async function listarMinhasSolicitacoes(): Promise<SolicitacaoSubgrupo[]> {
  const { data } = await api.get<SolicitacaoSubgrupo[]>('/subgrupos/solicitacoes/minhas')
  return data
}

export async function aprovarSolicitacao(id: string): Promise<SolicitacaoSubgrupo> {
  const { data } = await api.put<SolicitacaoSubgrupo>(`/subgrupos/solicitacoes/${id}/aprovar`)
  return data
}

export async function rejeitarSolicitacao(id: string): Promise<SolicitacaoSubgrupo> {
  const { data } = await api.put<SolicitacaoSubgrupo>(`/subgrupos/solicitacoes/${id}/rejeitar`)
  return data
}
