import api from './api'

export async function temPreferencias(participanteId: string): Promise<boolean> {
  const { data } = await api.get<{ configurado: boolean }>(`/sugestoes/preferencias/${participanteId}`)
  return data.configurado
}

export async function salvarPreferencias(participanteId: string, categoriaIds: string[]): Promise<void> {
  await api.post('/sugestoes/preferencias', { participanteId, categoriaIds })
}
