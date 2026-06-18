import api from './api'

export type AtividadeParceiro =
  | 'DIVULGACAO_REDES_SOCIAIS'
  | 'DISTRIBUICAO_CUPONS'
  | 'AFILIACAO_DIGITAL'
  | 'INDICACAO_PARTICIPANTES'
  | 'PUBLICACAO_EVENTOS'

export const ATIVIDADES_LABELS: Record<AtividadeParceiro, string> = {
  DIVULGACAO_REDES_SOCIAIS: 'Divulgação em redes sociais',
  DISTRIBUICAO_CUPONS: 'Distribuição de cupons',
  AFILIACAO_DIGITAL: 'Afiliação digital',
  INDICACAO_PARTICIPANTES: 'Indicação de participantes',
  PUBLICACAO_EVENTOS: 'Publicação de eventos',
}

export interface Parceiro {
  id: string
  participanteId: string
  organizadorId: string
  atividades: AtividadeParceiro[]
  nomeParticipante: string
}

export interface Comissao {
  id: string
  cupomId: string
  inscricaoId: string
  valor: number
  status: 'CREDITADA' | 'ESTORNADA'
  dataHora: string
}

export async function cadastrarParceiro(payload: {
  participanteId: string
  organizadorId: string
  atividades: AtividadeParceiro[]
}): Promise<Parceiro> {
  const { data } = await api.post<Parceiro>('/parceiros', payload)
  return data
}

export async function listarParceiros(organizadorId: string): Promise<Parceiro[]> {
  const { data } = await api.get<Parceiro[]>(`/parceiros/organizador/${organizadorId}`)
  return data
}

export async function buscarParceiroPorParticipante(participanteId: string): Promise<Parceiro[]> {
  const { data } = await api.get<Parceiro[]>(`/parceiros/participante/${participanteId}`)
  return data
}

export async function adicionarAtividade(parceiroId: string, atividade: AtividadeParceiro): Promise<void> {
  await api.put(`/parceiros/${parceiroId}/atividades/adicionar`, { atividade })
}

export async function removerAtividade(parceiroId: string, atividade: AtividadeParceiro): Promise<void> {
  await api.put(`/parceiros/${parceiroId}/atividades/remover`, { atividade })
}

export async function removerParceiro(id: string): Promise<void> {
  await api.delete(`/parceiros/${id}`)
}

export async function consultarComissoes(parceiroId: string): Promise<Comissao[]> {
  const { data } = await api.get<Comissao[]>(`/parceiros/${parceiroId}/comissoes`)
  return data
}

export async function consultarSaldoComissoes(parceiroId: string): Promise<number> {
  const { data } = await api.get<{ saldo: number }>(`/parceiros/${parceiroId}/comissoes/saldo`)
  return data.saldo
}
