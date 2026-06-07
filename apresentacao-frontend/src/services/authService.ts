import api from './api'
import type { LoginResposta, Papel } from '../types/auth'

export interface LoginPayload {
  email: string
  senha: string
}

export interface CadastroPayload {
  nome: string
  cpf: string
  email: string
  senha: string
  dataNascimento: string
}

export async function login(payload: LoginPayload): Promise<LoginResposta> {
  const { data } = await api.post<LoginResposta>('/auth/login', payload)
  return data
}

export async function cadastrar(payload: CadastroPayload, papel: Papel): Promise<LoginResposta> {
  const rota = papel === 'PARTICIPANTE' ? 'participante' : 'organizador'
  const { data } = await api.post<LoginResposta>(`/auth/cadastrar/${rota}`, payload)
  return data
}
