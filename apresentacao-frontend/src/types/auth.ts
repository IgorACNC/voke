export type Papel = 'PARTICIPANTE' | 'ORGANIZADOR' | 'ADMIN'

export interface Usuario {
  id: string
  nome: string
  email: string
  papel: Papel
}

export interface LoginResposta {
  token: string
  papel: Papel
  id: string
  nome: string
  email: string
}
