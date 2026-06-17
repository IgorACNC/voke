import api from './api'
import { endpoints } from './endpoints'

export interface Convite {
  id: string
  eventoId: string
  eventoNome: string
  remetenteId: string
  remetenteNome: string
  destinatarioId: string
  destinatarioNome: string
  status: 'PENDENTE' | 'ACEITO' | 'REJEITADO' | 'CANCELADO' | 'EXPIRADO'
  criadoEm: string
  expiraEm: string
}

export async function enviarConvite(remetenteId: string, emailDestinatario: string, eventoId: string): Promise<Convite> {
  const { data } = await api.post<Convite>(endpoints.enviarConvite, { remetenteId, emailDestinatario, eventoId })
  return data
}

export async function listarConvitesRecebidos(participanteId: string): Promise<Convite[]> {
  const { data } = await api.get<Convite[]>(endpoints.listarConvitesRecebidos, { params: { participanteId } })
  return data
}

export async function listarConvitesEnviados(participanteId: string): Promise<Convite[]> {
  const { data } = await api.get<Convite[]>(endpoints.listarConvitesEnviados, { params: { participanteId } })
  return data
}

export async function aceitarConvite(id: string, participanteId: string): Promise<void> {
  await api.patch(endpoints.aceitarConvite(id), { participanteId })
}

export async function rejeitarConvite(id: string, participanteId: string): Promise<void> {
  await api.patch(endpoints.rejeitarConvite(id), { participanteId })
}

export async function cancelarConvite(id: string, remetenteId: string): Promise<void> {
  await api.delete(endpoints.cancelarConvite(id), { params: { remetenteId } })
}
