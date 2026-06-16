import api from './api'

export type TipoCanalChat = 'GRUPO_EVENTO' | 'SUBGRUPO'

export interface MensagemCanal {
  id: string
  remetenteId: string
  remetenteNome: string
  canalTipo: TipoCanalChat
  canalId: string
  conteudo: string
  enviadaEm: string
}

export async function listarMensagensGrupo(grupoId: string): Promise<MensagemCanal[]> {
  const resp = await api.get<MensagemCanal[]>(`/grupos/${grupoId}/mensagens`)
  return resp.data
}

export async function enviarMensagemGrupo(grupoId: string, conteudo: string): Promise<MensagemCanal> {
  const resp = await api.post<MensagemCanal>(`/grupos/${grupoId}/mensagens`, { conteudo })
  return resp.data
}

export async function listarMensagensSubgrupo(subgrupoId: string): Promise<MensagemCanal[]> {
  const resp = await api.get<MensagemCanal[]>(`/subgrupos/${subgrupoId}/mensagens`)
  return resp.data
}

export async function enviarMensagemSubgrupo(subgrupoId: string, conteudo: string): Promise<MensagemCanal> {
  const resp = await api.post<MensagemCanal>(`/subgrupos/${subgrupoId}/mensagens`, { conteudo })
  return resp.data
}
