import api from './api'

export interface PerfilOrganizadorPublico {
  id: string
  nome: string
  email: string
}

export interface PerfilOrganizadorCompleto extends PerfilOrganizadorPublico {
  cpf?: string | null
  dataNascimento?: string | null
}

export interface EditarOrganizadorPayload {
  id: string
  nome: string
  email: string
}

export interface AlterarSenhaOrganizadorPayload {
  id: string
  senhaAtual: string
  novaSenha: string
}

export async function buscarPerfilPublico(id: string): Promise<PerfilOrganizadorPublico> {
  const { data } = await api.get<PerfilOrganizadorPublico>(`/organizadores/${id}`)
  return data
}

export async function buscarPerfilCompleto(id: string): Promise<PerfilOrganizadorCompleto> {
  const { data } = await api.get<PerfilOrganizadorCompleto>(`/organizadores/me/${id}`)
  return data
}

export async function editarPerfilOrganizador(payload: EditarOrganizadorPayload): Promise<void> {
  await api.put('/organizadores/perfil', payload)
}

export async function alterarSenhaOrganizador(payload: AlterarSenhaOrganizadorPayload): Promise<void> {
  await api.put('/organizadores/senha', payload)
}

export async function removerContaOrganizador(id: string): Promise<void> {
  await api.delete(`/organizadores/${id}`)
}
