import api from './api'

export type TipoSubgrupo = 'ABERTO' | 'FECHADO'
export type CategoriaSubgrupo = 'CARONA' | 'INTERESSE' | 'SOCIAL' | 'OPERACIONAL' | 'OUTRO'

export interface Subgrupo {
  id: string
  nome: string
  descricao: string
  regras: string
  grupoEventoId: string
  categoria: CategoriaSubgrupo
  tipo: TipoSubgrupo
  limiteMembros: number
  moderadorId: string | null
  membrosIds: string[]
}

export interface CriarSubgrupoPayload {
  grupoEventoId: string
  nome: string
  descricao: string
  regras: string
  categoria: CategoriaSubgrupo
  tipo: TipoSubgrupo
  limiteMembros: number
}

export async function listarSubgruposDoGrupo(grupoId: string): Promise<Subgrupo[]> {
  const { data } = await api.get<Subgrupo[]>(`/subgrupos/grupo/${grupoId}`)
  return data
}

export async function buscarSubgrupo(id: string): Promise<Subgrupo> {
  const { data } = await api.get<Subgrupo>(`/subgrupos/${id}`)
  return data
}

export async function criarSubgrupo(payload: CriarSubgrupoPayload): Promise<Subgrupo> {
  const { data } = await api.post<Subgrupo>('/subgrupos', payload)
  return data
}

export async function entrarNoSubgrupo(id: string): Promise<void> {
  await api.post(`/subgrupos/${id}/membros`)
}

export async function adicionarMembroSubgrupo(id: string, participanteId: string): Promise<void> {
  await api.post(`/subgrupos/${id}/membros/${participanteId}`)
}

export async function removerMembroSubgrupo(id: string, participanteId: string): Promise<void> {
  await api.delete(`/subgrupos/${id}/membros/${participanteId}`)
}

export async function editarRegrasSubgrupo(id: string, regras: string, descricao: string): Promise<void> {
  await api.put(`/subgrupos/${id}/regras`, { regras, descricao })
}

export async function excluirSubgrupo(id: string): Promise<void> {
  await api.delete(`/subgrupos/${id}`)
}

export async function promoverModerador(id: string, participanteId: string): Promise<void> {
  await api.put(`/subgrupos/${id}/moderador/${participanteId}`)
}

export async function removerModerador(id: string): Promise<void> {
  await api.delete(`/subgrupos/${id}/moderador`)
}
