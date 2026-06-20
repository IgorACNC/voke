import api from './api'

export type MetodoPagamento = 'PIX' | 'CARTAO_CREDITO'

export interface SaldoCarteira {
  participanteId: string
  saldo: number
  saldoReal: number
  saldoPromocional: number
}

export type TipoTransacao = 'DEPOSITO' | 'SAQUE' | 'DEBITO_COMPRA' | 'CREDITO_BONUS' | 'ESTORNO'
export type DirecaoTransacao = 'ENTRADA' | 'SAIDA'

export interface Transacao {
  id: string
  tipo: TipoTransacao
  direcao: DirecaoTransacao
  valor: number
  descricao: string
  dataHora: string
}

// RN5: taxa de conveniência para cartão de crédito
export const TAXA_CARTAO = 0.0299

// RN1: limite diário de inserção (InsercaoSaldoPadrao)
export const LIMITE_INSERCAO_DIARIA = 5000

// RN2: limite por saque e frequência diária
export const LIMITE_SAQUE_VALOR = 500
export const LIMITE_SAQUE_DIARIO = 1

export async function consultarCarteira(participanteId: string): Promise<SaldoCarteira> {
  const { data } = await api.get(`/fidelidade/carteira/${participanteId}`)
  return data
}

export async function depositar(
  participanteId: string,
  valor: number,
  metodoPagamento: MetodoPagamento,
): Promise<void> {
  // metodoPagamento é enviado para uso futuro pelo backend (RN5 — taxa de conveniência)
  await api.post('/fidelidade/carteira/adicionar', { participanteId, valor, metodoPagamento })
}

export async function sacar(participanteId: string, valor: number): Promise<void> {
  await api.post('/fidelidade/carteira/remover', { participanteId, valor })
}

// Preparado para quando o backend implementar o extrato de transações (RN: movimentações de entrada/saída)
export async function consultarExtrato(participanteId: string): Promise<Transacao[]> {
  const { data } = await api.get(`/fidelidade/carteira/${participanteId}/extrato`)
  return data
}
