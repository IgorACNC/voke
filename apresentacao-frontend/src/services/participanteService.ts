import api from './api'
import { endpoints } from './endpoints'

export interface EditarPerfilPayload {
  id: string
  nome: string
  email: string
}

export interface PerfilParticipante {
  id: string
  nome: string
  email: string
  cpf?: string | null
  dataNascimento?: string | null
}

export async function editarPerfil(payload: EditarPerfilPayload): Promise<void> {
  await api.put(endpoints.editarPerfil, payload)
}

export async function removerConta(id: string): Promise<void> {
  await api.delete(endpoints.removerConta(id))
}

export interface AlterarSenhaPayload {
  id: string
  senhaAtual: string
  novaSenha: string
}

export async function alterarSenha(payload: AlterarSenhaPayload): Promise<void> {
  await api.put(endpoints.alterarSenha, payload)
}

export async function buscarPerfil(id: string): Promise<PerfilParticipante> {
  const { data } = await api.get<PerfilParticipante>(endpoints.perfilParticipante(id))
  return data
}
