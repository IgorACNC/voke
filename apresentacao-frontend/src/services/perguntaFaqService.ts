import api from './api'
import { endpoints } from './endpoints'

export interface PerguntaFaq {
  id: string
  eventoId: string
  pergunta: string
  resposta: string
  posicao: number
}

export interface PerguntaFaqPayload {
  pergunta: string
  resposta: string
}

export async function listarFaq(eventoId: string): Promise<PerguntaFaq[]> {
  const { data } = await api.get<PerguntaFaq[]>(endpoints.listarFaq(eventoId))
  return data
}

export async function criarPergunta(eventoId: string, payload: PerguntaFaqPayload): Promise<PerguntaFaq> {
  const { data } = await api.post<PerguntaFaq>(endpoints.criarPerguntaFaq(eventoId), payload)
  return data
}

export async function editarPergunta(eventoId: string, id: string, payload: PerguntaFaqPayload): Promise<PerguntaFaq> {
  const { data } = await api.put<PerguntaFaq>(endpoints.editarPerguntaFaq(eventoId, id), payload)
  return data
}

export async function excluirPergunta(eventoId: string, id: string): Promise<void> {
  await api.delete(endpoints.excluirPerguntaFaq(eventoId, id))
}

export async function reordenarFaq(eventoId: string, idsOrdenados: string[]): Promise<PerguntaFaq[]> {
  const { data } = await api.put<PerguntaFaq[]>(endpoints.reordenarFaq(eventoId), { ordem: idsOrdenados })
  return data
}
