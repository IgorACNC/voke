import api from './api'
import { endpoints } from './endpoints'

export interface Cupom {
  id: string
  codigo: string
  percentual: boolean
  valor: number
  eventoId?: string | null
  usos: number
  vigenciaInicio?: string | null
  vigenciaFim?: string | null
}

export async function listarCupons(): Promise<Cupom[]> {
  const { data } = await api.get<Cupom[]>(endpoints.listarCupons)
  return data
}

export async function criarCupom(payload: { codigo: string; percentual: boolean; valor: number; eventoId?: string | null }): Promise<Cupom> {
  const { data } = await api.post<Cupom>(endpoints.criarCupom, payload)
  return data
}

export async function editarCupom(id: string, payload: Partial<Cupom>): Promise<Cupom> {
  const { data } = await api.put<Cupom>(endpoints.editarCupom(id), payload)
  return data
}

export async function excluirCupom(id: string): Promise<void> {
  await api.delete(endpoints.excluirCupom(id))
}

export async function validarCupom(codigo: string, eventoId?: string): Promise<{ valido: boolean; cupom?: Cupom }> {
  const { data } = await api.get(endpoints.validarCupom, { params: { codigo, eventoId } })
  return data
}
