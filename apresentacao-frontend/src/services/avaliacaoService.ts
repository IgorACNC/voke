import api from './api'

export interface Avaliacao {
  id: string
  participanteId: string
  eventoId: string
  nota: number
  comentario: string
}

export interface EventoAvaliavel {
  id: string
  nome: string
  local: string
  dataHoraFim: string
  avaliado: boolean
}

export interface AvaliarPayload {
  participanteId: string
  eventoId: string
  nota: number
  comentario: string
}

export async function avaliarEvento(payload: AvaliarPayload): Promise<Avaliacao> {
  const { data } = await api.post('/avaliacoes', payload)
  return data
}

export async function listarEventosAvaliaveis(participanteId: string): Promise<EventoAvaliavel[]> {
  const { data } = await api.get('/avaliacoes/eventos-avaliaveis', { params: { participanteId } })
  return data
}

export async function buscarAvaliacao(participanteId: string, eventoId: string): Promise<Avaliacao | null> {
  try {
    const { data } = await api.get('/avaliacoes', { params: { participanteId, eventoId } })
    return data
  } catch (err: unknown) {
    if ((err as { response?: { status?: number } })?.response?.status === 404) return null
    throw err
  }
}

export async function editarAvaliacao(id: string, nota: number, comentario: string): Promise<void> {
  await api.put(`/avaliacoes/${id}`, { nota, comentario })
}

export async function removerAvaliacao(id: string): Promise<void> {
  await api.delete(`/avaliacoes/${id}`)
}
