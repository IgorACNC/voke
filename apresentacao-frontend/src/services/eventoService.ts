import api from './api'
import { endpoints } from './endpoints'

export interface LoteEvento {
  numero: number
  preco: number
  quantidadeTotal: number
  quantidadeVendida: number
  ativo: boolean
}

export interface Evento {
  id: string
  nome: string
  descricao: string
  local: string
  dataHoraInicio: string
  dataHoraFim: string
  capacidadeMaxima: number
  status: 'ATIVO' | 'CANCELADO' | 'ENCERRADO'
  idadeMinima: number
  loteAtual: LoteEvento | null
}

export interface CriarEventoPayload {
  nome: string
  descricao: string
  local: string
  dataHoraInicio: string
  dataHoraFim: string
  capacidadeMaxima: number
  idadeMinima: number
  categoriaIds: string[]
  precoLote: number
  quantidadeLote: number
}

export interface EditarEventoPayload {
  nome: string
  local: string
  dataHoraInicio: string
  dataHoraFim: string
  capacidadeMaxima: number
}

export async function listarEventosAtivos(): Promise<Evento[]> {
  const { data } = await api.get<Evento[]>(endpoints.listarEventos)
  return data
}

export async function listarMeusEventos(): Promise<Evento[]> {
  const { data } = await api.get<Evento[]>(endpoints.listarMeusEventos)
  return data
}

export async function buscarEvento(id: string): Promise<Evento> {
  const { data } = await api.get<Evento>(endpoints.buscarEvento(id))
  return data
}

export async function criarEvento(payload: CriarEventoPayload): Promise<Evento> {
  const { data } = await api.post<Evento>(endpoints.criarEvento, payload)
  return data
}

export async function editarEvento(id: string, payload: EditarEventoPayload): Promise<Evento> {
  const { data } = await api.put<Evento>(endpoints.editarEvento(id), payload)
  return data
}

export async function cancelarEvento(id: string): Promise<void> {
  await api.delete(endpoints.cancelarEvento(id))
}

export async function criarLote(eventoId: string, preco: number, quantidade: number): Promise<Evento> {
  const { data } = await api.post<Evento>(endpoints.criarLote(eventoId), { preco, quantidade })
  return data
}
