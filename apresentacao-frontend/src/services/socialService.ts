import api from './api'

export interface ParticipanteResumo {
  id: string
  nome: string
  email: string
}

export interface Amizade {
  id: string
  solicitanteId: string
  receptorId: string
  status: 'PENDENTE' | 'ATIVA' | 'RECUSADA' | 'DESFEITA'
  amigo: ParticipanteResumo | null
}

export interface Comunidade {
  id: string
  nome: string
  criadorId: string
  membros: ParticipanteResumo[]
  eventosCompartilhados: EventoResumo[]
}

export interface EventoResumo {
  id: string
  nome: string
  local: string
  dataHoraInicio: string
}

export interface MensagemPrivada {
  id: string
  remetenteId: string
  destinatarioId: string
  conteudo: string
  enviadaEm: string
}

export async function solicitarAmizade(solicitanteId: string, receptorId: string): Promise<void> {
  await api.post('/social/amizades', { solicitanteId, receptorId })
}

export async function buscarParticipantes(termo: string, participanteAtualId: string): Promise<ParticipanteResumo[]> {
  const { data } = await api.get('/social/participantes', { params: { termo, participanteAtualId } })
  return data
}

export async function listarAmizades(participanteId: string): Promise<Amizade[]> {
  const { data } = await api.get('/social/amizades', { params: { participanteId } })
  return data
}

export async function aceitarAmizade(id: string): Promise<void> {
  await api.put(`/social/amizades/${id}/aceitar`)
}

export async function recusarAmizade(id: string): Promise<void> {
  await api.put(`/social/amizades/${id}/recusar`)
}

export async function desfazerAmizade(id: string): Promise<void> {
  await api.put(`/social/amizades/${id}/desfazer`)
}

export async function criarComunidade(criadorId: string, nome: string): Promise<Comunidade> {
  const { data } = await api.post('/social/comunidades', { criadorId, nome })
  return data
}

export async function listarComunidades(participanteId: string): Promise<Comunidade[]> {
  const { data } = await api.get('/social/comunidades', { params: { participanteId } })
  return data
}

export async function adicionarMembroComunidade(comunidadeId: string, criadorId: string, participanteId: string): Promise<Comunidade> {
  const { data } = await api.post(`/social/comunidades/${comunidadeId}/membros`, { criadorId, participanteId })
  return data
}

export async function compartilharEventoComunidade(comunidadeId: string, criadorId: string, eventoId: string): Promise<Comunidade> {
  const { data } = await api.post(`/social/comunidades/${comunidadeId}/eventos`, { criadorId, eventoId })
  return data
}

export async function listarConversa(participanteId: string, amigoId: string): Promise<MensagemPrivada[]> {
  const { data } = await api.get('/chats/privado/conversa', { params: { participanteId, amigoId } })
  return data
}

export async function enviarMensagem(remetenteId: string, destinatarioId: string, conteudo: string): Promise<MensagemPrivada> {
  const { data } = await api.post('/chats/privado/mensagens', { remetenteId, destinatarioId, conteudo })
  return data
}
