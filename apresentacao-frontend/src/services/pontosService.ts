import api from './api'

export type TipoTransacaoPontos = 'GANHO_PRESENCA' | 'RESGATE_RECOMPENSA' | 'EXPIRACAO'
export type DirecaoPontos = 'ENTRADA' | 'SAIDA'

export interface TransacaoPontos {
  id: string
  tipo: TipoTransacaoPontos
  pontos: number
  descricao: string
  dataHora: string
  direcao: DirecaoPontos
  referenciaId: string | null
}

export async function consultarSaldoPontos(participanteId: string): Promise<number> {
  const { data } = await api.get<{ saldo: number }>(`/recompensas/participante/${participanteId}/saldo-pontos`)
  return data.saldo
}

export async function consultarExtratoPontos(participanteId: string): Promise<TransacaoPontos[]> {
  const { data } = await api.get<TransacaoPontos[]>(`/fidelidade/pontos/${participanteId}/extrato`)
  return data
}

export async function realizarCheckIn(inscricaoId: string): Promise<{ mensagem: string; pontosGanhos: number }> {
  const { data } = await api.post<{ mensagem: string; pontosGanhos: number }>(`/inscricoes/${inscricaoId}/check-in`)
  return data
}
