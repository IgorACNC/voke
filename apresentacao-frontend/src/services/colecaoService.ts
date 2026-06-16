import api from './api'
import { endpoints } from './endpoints'

export interface ItemColecao {
  eventoId: string
  nomeEvento: string
  local: string
  dataHoraInicio: string | null
  ordem: number
}

export interface ColecaoResumo {
  id: string
  nome: string
  visibilidade: 'PUBLICA' | 'PRIVADA'
  dataCriacao: string
  quantidadeItens: number
}

export interface ColecaoDetalhe {
  id: string
  nome: string
  visibilidade: 'PUBLICA' | 'PRIVADA'
  dataCriacao: string
  itens: ItemColecao[]
}

export async function listarColecoes(): Promise<ColecaoResumo[]> {
  const { data } = await api.get<ColecaoResumo[]>(endpoints.listarColecoes)
  return data
}

export async function buscarColecao(id: string): Promise<ColecaoDetalhe> {
  const { data } = await api.get<ColecaoDetalhe>(endpoints.buscarColecao(id))
  return data
}

export async function criarColecao(nome: string, visibilidade: 'PUBLICA' | 'PRIVADA'): Promise<ColecaoResumo> {
  const { data } = await api.post<ColecaoResumo>(endpoints.criarColecao, { nome, visibilidade })
  return data
}

export async function editarColecao(id: string, nome: string, visibilidade: 'PUBLICA' | 'PRIVADA'): Promise<ColecaoResumo> {
  const { data } = await api.patch<ColecaoResumo>(endpoints.editarColecao(id), { nome, visibilidade })
  return data
}

export async function excluirColecao(id: string): Promise<void> {
  await api.delete(endpoints.excluirColecao(id))
}

export async function adicionarEventoColecao(colecaoId: string, eventoId: string): Promise<ColecaoDetalhe> {
  const { data } = await api.post<ColecaoDetalhe>(endpoints.adicionarEventoColecao(colecaoId), { eventoId })
  return data
}

export async function removerEventoColecao(colecaoId: string, eventoId: string): Promise<ColecaoDetalhe> {
  const { data } = await api.delete<ColecaoDetalhe>(endpoints.removerEventoColecao(colecaoId, eventoId))
  return data
}

export async function moverEventoColecao(origemId: string, destinoId: string, eventoId: string): Promise<ColecaoDetalhe> {
  const { data } = await api.post<ColecaoDetalhe>(endpoints.moverEventoColecao(origemId), { destinoId, eventoId })
  return data
}

export async function duplicarColecao(id: string): Promise<ColecaoResumo> {
  const { data } = await api.post<ColecaoResumo>(endpoints.duplicarColecao(id))
  return data
}
