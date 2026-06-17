import api from './api'

export interface Recompensa {
  id: string
  nome: string
  descricao: string
  custoEmPontos: number
  estoqueDisponivel: number
  estoqueTotal: number
  ativa: boolean
}

export async function criarRecompensa(payload: {
  nome: string
  descricao: string
  custoEmPontos: number
  estoqueTotal: number
  organizadorId: string
}): Promise<Recompensa> {
  const { data } = await api.post<Recompensa>('/recompensas', payload)
  return data
}

export async function editarRecompensa(id: string, payload: {
  novaDescricao?: string
  novoCusto?: number
}): Promise<void> {
  await api.put(`/recompensas/${id}`, payload)
}

export async function inativarRecompensa(id: string): Promise<void> {
  await api.patch(`/recompensas/${id}/inativar`)
}

export async function removerRecompensa(id: string): Promise<void> {
  await api.delete(`/recompensas/${id}`)
}

export async function listarRecompensasOrganizador(organizadorId: string): Promise<Recompensa[]> {
  const { data } = await api.get<Recompensa[]>(`/recompensas/organizador/${organizadorId}`)
  return data
}

export async function listarRecompensasAtivas(): Promise<Recompensa[]> {
  const { data } = await api.get<Recompensa[]>('/recompensas/ativas')
  return data
}

export async function resgatarRecompensa(recompensaId: string, participanteId: string): Promise<void> {
  await api.post(`/recompensas/${recompensaId}/resgatar?participanteId=${participanteId}`)
}

export async function consultarSaldoPontos(participanteId: string): Promise<number> {
  const { data } = await api.get<{ saldo: number }>(`/recompensas/participante/${participanteId}/saldo-pontos`)
  return data.saldo
}
