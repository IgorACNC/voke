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
