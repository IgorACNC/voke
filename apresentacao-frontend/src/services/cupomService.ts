import api from './api'

export type TipoDescontoCupom = 'FIXO' | 'PERCENTUAL'

export interface Cupom {
  id: string
  codigo: string
  desconto: number
  tipoDesconto: TipoDescontoCupom
  organizadorId: string | null
  eventoId: string | null
  quantidadeMaxima: number
  quantidadeUtilizada: number
  ativo: boolean
  global: boolean
}

export interface CriarCupomPayload {
  codigo: string
  desconto: number
  tipoDesconto: TipoDescontoCupom
  organizadorId: string
  eventoId: string | null
  quantidadeMaxima: number
}

export interface CriarCupomGlobalPayload {
  codigo: string
  desconto: number
  tipoDesconto: TipoDescontoCupom
  quantidadeMaxima: number
}

export async function listarMeusCupons(organizadorId: string): Promise<Cupom[]> {
  const { data } = await api.get<Cupom[]>('/cupons/meus', { params: { organizadorId } })
  return data
}

export async function listarTodosCupons(): Promise<Cupom[]> {
  const { data } = await api.get<Cupom[]>('/cupons')
  return data
}

export async function criarCupom(payload: CriarCupomPayload): Promise<Cupom> {
  const { data } = await api.post<Cupom>('/cupons', payload)
  return data
}

export async function criarCupomGlobal(payload: CriarCupomGlobalPayload): Promise<Cupom> {
  const { data } = await api.post<Cupom>('/cupons/global', payload)
  return data
}

export async function editarCupom(
  id: string,
  payload: { novoDesconto: number; novaQuantidade: number },
): Promise<void> {
  await api.put(`/cupons/${id}`, payload)
}

export async function alterarAtivoCupom(id: string, ativo: boolean): Promise<void> {
  await api.patch(`/cupons/${id}/ativo`, { ativo })
}

export async function excluirCupom(id: string): Promise<void> {
  await api.delete(`/cupons/${id}`)
}
