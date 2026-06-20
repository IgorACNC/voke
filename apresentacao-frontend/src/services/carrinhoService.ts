import api from './api'

export type MetodoPagamentoCarrinho = 'PIX' | 'CARTAO_CREDITO'

export const TAXA_CARTAO_CARRINHO = 0.05
export const MINUTOS_EXPIRACAO_CARRINHO = 15

export interface ItemCarrinho {
  eventoId: string
  nomeEvento: string
  quantidade: number
  precoUnitario: number
  subtotal: number
}

export interface CarrinhoResp {
  participanteId: string
  itens: ItemCarrinho[]
  cupomAplicado: string | null
  descontoCupom: number
  criadoEm: string
  expirado: boolean
}

export interface FinalizarResp {
  total: number
  inscricoesIds: string[]
}

export async function consultarCarrinho(participanteId: string): Promise<CarrinhoResp | null> {
  try {
    const { data } = await api.get<CarrinhoResp>(`/carrinho/${participanteId}`)
    return data
  } catch (e: any) {
    if (e?.response?.status === 404) return null
    throw e
  }
}

export async function adicionarAoCarrinho(
  participanteId: string,
  eventoId: string,
  nomeEvento: string,
  quantidade: number,
  precoUnitario: number,
): Promise<CarrinhoResp> {
  const { data } = await api.post<CarrinhoResp>('/carrinho/itens', {
    participanteId,
    eventoId,
    nomeEvento,
    quantidade,
    precoUnitario,
  })
  return data
}

export async function removerDoCarrinho(participanteId: string, eventoId: string): Promise<void> {
  await api.delete(`/carrinho/itens/${participanteId}/${eventoId}`)
}

export async function decrementarItemCarrinho(
  participanteId: string,
  eventoId: string,
): Promise<CarrinhoResp> {
  const { data } = await api.patch<CarrinhoResp>(
    `/carrinho/itens/${participanteId}/${eventoId}/decrementar`,
  )
  return data
}

export async function aplicarCupomCarrinho(
  participanteId: string,
  codigoCupom: string,
  cpfParticipante: string,
): Promise<CarrinhoResp> {
  const { data } = await api.post<CarrinhoResp>('/carrinho/cupom', {
    participanteId,
    codigoCupom,
    cpfParticipante,
  })
  return data
}

export async function removerCupomCarrinho(
  participanteId: string,
  cpfParticipante: string,
): Promise<CarrinhoResp> {
  const { data } = await api.delete<CarrinhoResp>(`/carrinho/cupom/${participanteId}`, {
    params: { cpfParticipante },
  })
  return data
}

export async function finalizarCompra(
  participanteId: string,
  metodoPagamento: MetodoPagamentoCarrinho,
): Promise<FinalizarResp> {
  const { data } = await api.post<FinalizarResp>('/carrinho/finalizar', {
    participanteId,
    metodoPagamento,
  })
  return data
}

export function calcularTotalCarrinho(
  subtotal: number,
  desconto: number,
  metodo: MetodoPagamentoCarrinho,
): number {
  const comDesconto = Math.max(0, subtotal - desconto)
  return metodo === 'CARTAO_CREDITO'
    ? Math.round(comDesconto * (1 + TAXA_CARTAO_CARRINHO) * 100) / 100
    : comDesconto
}
