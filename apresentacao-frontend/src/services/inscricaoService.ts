import api from './api'
import { endpoints } from './endpoints'
import type { Evento } from './eventoService'

export interface Inscricao {
  id: string
  evento: Evento
  loteNumero?: number
  status: 'PENDENTE' | 'CONFIRMADA' | 'CANCELADA' | 'CHECK_IN_REALIZADO'
  dataHoraInscricao: string
  valorPago: number
}

export async function listarMinhasInscricoes(participanteId: string): Promise<Inscricao[]> {
  const { data } = await api.get<Inscricao[]>(endpoints.listarMinhasInscricoes, { params: { participanteId } })
  return data
}

export async function validarInscricao(participanteId: string, eventoId: string): Promise<{ ok: boolean; motivos?: string[] }> {
  const { data } = await api.get(endpoints.validarInscricao(eventoId), { params: { participanteId } })
  return data
}

export async function criarInscricao(eventoId: string, loteNumero?: number): Promise<Inscricao> {
  const { data } = await api.post<Inscricao>(endpoints.criarInscricao(eventoId), { loteNumero })
  return data
}

export async function estimarEstorno(inscricaoId: string): Promise<{ percentual: number; valor: number }> {
  const { data } = await api.get(endpoints.estimarEstorno(inscricaoId))
  return data
}

export async function cancelarInscricao(inscricaoId: string): Promise<void> {
  await api.delete(endpoints.cancelarInscricao(inscricaoId))
}
