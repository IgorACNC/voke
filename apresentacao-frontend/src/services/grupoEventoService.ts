import api from './api'

export interface GrupoEvento {
  id: string
  nome: string
  regras: string
  eventoId: string
  organizadorId: string
  membrosIds: string[]
}

export async function buscarGrupoPorEvento(eventoId: string): Promise<GrupoEvento | null> {
  try {
    const { data } = await api.get<GrupoEvento>(`/grupos/evento/${eventoId}`)
    return data
  } catch (err: unknown) {
    const status = (err as { response?: { status?: number } })?.response?.status
    if (status === 404) return null
    throw err
  }
}

export async function criarGrupo(eventoId: string, nome: string, regras: string): Promise<GrupoEvento> {
  const { data } = await api.post<GrupoEvento>('/grupos', { eventoId, nome, regras })
  return data
}

export async function entrarNoGrupo(grupoId: string): Promise<void> {
  await api.post(`/grupos/${grupoId}/membros`)
}

export async function removerMembro(grupoId: string, participanteId: string): Promise<void> {
  await api.delete(`/grupos/${grupoId}/membros/${participanteId}`)
}

export async function editarRegras(grupoId: string, regras: string): Promise<void> {
  await api.put(`/grupos/${grupoId}/regras`, { regras })
}

export async function excluirGrupo(grupoId: string): Promise<void> {
  await api.delete(`/grupos/${grupoId}`)
}
