import api from './api'

export type CategoriaRecompensa = 'DESCONTO' | 'BRINDE' | 'BENEFICIO' | 'CREDITO_CARTEIRA' | 'CUPOM'

export interface Recompensa {
  id: string
  nome: string
  descricao: string
  custoEmPontos: number
  estoqueDisponivel: number
  estoqueTotal: number
  categoria: CategoriaRecompensa
  valor: number | null
  organizadorId: string | null
  global: boolean
  ativa: boolean
}

export interface CriarRecompensaPayload {
  nome: string
  descricao: string
  custoEmPontos: number
  estoqueTotal: number
  organizadorId: string
  categoria: CategoriaRecompensa
  valor?: number | null
}

export interface CriarRecompensaGlobalPayload {
  nome: string
  descricao: string
  custoEmPontos: number
  estoqueTotal: number
  categoria: CategoriaRecompensa
  valor?: number | null
}

export async function criarRecompensa(payload: CriarRecompensaPayload): Promise<Recompensa> {
  const { data } = await api.post<Recompensa>('/recompensas', payload)
  return data
}

export async function criarRecompensaGlobal(payload: CriarRecompensaGlobalPayload): Promise<Recompensa> {
  const { data } = await api.post<Recompensa>('/recompensas/global', payload)
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

export async function listarTodasRecompensas(): Promise<Recompensa[]> {
  const { data } = await api.get<Recompensa[]>('/recompensas')
  return data
}

export interface ResgateRecompensaResp {
  codigoCupom: string | null
}

export async function resgatarRecompensa(
  recompensaId: string,
  participanteId: string,
): Promise<ResgateRecompensaResp> {
  const { data } = await api.post<ResgateRecompensaResp>(
    `/recompensas/${recompensaId}/resgatar?participanteId=${participanteId}`,
  )
  return data ?? { codigoCupom: null }
}

export interface MeuCupom {
  id: string
  codigoCupom: string
  recompensaNome: string
  valor: number | null
  global: boolean
  dataResgate: string
  utilizado: boolean
  ativo: boolean
}

export async function listarMeusCupons(participanteId: string): Promise<MeuCupom[]> {
  const { data } = await api.get<MeuCupom[]>(
    `/recompensas/participante/${participanteId}/meus-cupons`,
  )
  return data
}
