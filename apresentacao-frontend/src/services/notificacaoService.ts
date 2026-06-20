import api from './api'

export type StatusNotificacao = 'RASCUNHO' | 'AGENDADA' | 'ENVIADA' | 'CANCELADA'

export interface Notificacao {
  id: string
  eventoId: string
  conteudo: string
  status: StatusNotificacao
  dataEnvio: string
  dataAgendamento: string | null
  contadorEdicoes: number
  criterioSegmentacao: string | null
  totalDestinatarios: number
}

export interface EnviarPayload {
  eventoId: string
  conteudo: string
  numeroLote?: number | null
}

export interface AgendarPayload {
  eventoId: string
  conteudo: string
  dataAgendamento: string
}

export async function enviarNotificacao(payload: EnviarPayload): Promise<Notificacao> {
  const { data } = await api.post<Notificacao>('/notificacoes', payload)
  return data
}

export async function agendarNotificacao(payload: AgendarPayload): Promise<Notificacao> {
  const { data } = await api.post<Notificacao>('/notificacoes/agendar', payload)
  return data
}

export async function editarNotificacao(id: string, conteudo: string): Promise<void> {
  await api.put(`/notificacoes/${id}`, { conteudo })
}

export async function cancelarNotificacao(id: string): Promise<void> {
  await api.post(`/notificacoes/${id}/cancelar`)
}

export async function removerNotificacao(id: string): Promise<void> {
  await api.delete(`/notificacoes/${id}`)
}

export async function listarNotificacoesPorEvento(eventoId: string): Promise<Notificacao[]> {
  const { data } = await api.get<Notificacao[]>(`/notificacoes/evento/${eventoId}`)
  return data
}

export async function listarMinhasNotificacoes(): Promise<Notificacao[]> {
  const { data } = await api.get<Notificacao[]>('/notificacoes/minhas')
  return data
}
