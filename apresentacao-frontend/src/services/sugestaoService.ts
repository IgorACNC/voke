import api from './api'

interface PreferenciasResp {
  configurado: boolean
  categoriaIds: string[]
}

export async function temPreferencias(participanteId: string): Promise<boolean> {
  const { data } = await api.get<PreferenciasResp>(`/sugestoes/preferencias/${participanteId}`)
  return data.configurado
}

export async function buscarPreferencias(participanteId: string): Promise<string[]> {
  const { data } = await api.get<PreferenciasResp>(`/sugestoes/preferencias/${participanteId}`)
  return data.categoriaIds ?? []
}

export async function salvarPreferencias(participanteId: string, categoriaIds: string[]): Promise<void> {
  await api.post('/sugestoes/preferencias', { participanteId, categoriaIds })
}

export type StatusSugestao = 'PENDENTE' | 'APROVADA' | 'REJEITADA' | 'EXPIRADA'

export interface Sugestao {
  id: string
  eventoId: string
  status: StatusSugestao
}

export async function listarMinhasSugestoes(participanteId: string): Promise<Sugestao[]> {
  const { data } = await api.get<Sugestao[]>(`/sugestoes/participante/${participanteId}`)
  return data
}

export async function avaliarSugestao(id: string, aprovada: boolean): Promise<void> {
  await api.post(`/sugestoes/${id}/avaliar`, { aprovada })
}

export async function descartarSugestao(id: string): Promise<void> {
  await api.delete(`/sugestoes/${id}`)
}

export async function gerarSugestoes(participanteId: string): Promise<Sugestao[]> {
  const { data } = await api.post<Sugestao[]>(`/sugestoes/gerar/${participanteId}`)
  return data
}
