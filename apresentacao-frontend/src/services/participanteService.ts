import api from './api'

export interface EditarPerfilPayload {
  id: string
  nome: string
  email: string
}

export async function editarPerfil(payload: EditarPerfilPayload): Promise<void> {
  await api.put('/participantes/perfil', payload)
}

export async function removerConta(id: string): Promise<void> {
  await api.delete(`/participantes/${id}`)
}

export interface AlterarSenhaPayload {
  id: string
  senhaAtual: string
  novaSenha: string
}

export async function alterarSenha(payload: AlterarSenhaPayload): Promise<void> {
  await api.put('/participantes/senha', payload)
}
