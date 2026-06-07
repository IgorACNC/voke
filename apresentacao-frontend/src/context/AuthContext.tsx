import { createContext, useContext, useState, useCallback } from 'react'
import type { ReactNode } from 'react'
import type { Usuario, LoginResposta } from '../types/auth'

interface AuthContextValue {
  usuario: Usuario | null
  salvarSessao: (resposta: LoginResposta) => void
  sair: () => void
  estaAutenticado: boolean
}

const AuthContext = createContext<AuthContextValue | null>(null)

function carregarUsuario(): Usuario | null {
  try {
    const json = localStorage.getItem('voke_usuario')
    return json ? JSON.parse(json) : null
  } catch {
    return null
  }
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [usuario, setUsuario] = useState<Usuario | null>(carregarUsuario)

  const salvarSessao = useCallback((resposta: LoginResposta) => {
    const u: Usuario = { id: resposta.id, nome: resposta.nome, email: resposta.email, papel: resposta.papel }
    localStorage.setItem('voke_token', resposta.token)
    localStorage.setItem('voke_usuario', JSON.stringify(u))
    setUsuario(u)
  }, [])

  const sair = useCallback(() => {
    localStorage.removeItem('voke_token')
    localStorage.removeItem('voke_usuario')
    setUsuario(null)
  }, [])

  return (
    <AuthContext.Provider value={{ usuario, salvarSessao, sair, estaAutenticado: !!usuario }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth deve ser usado dentro de AuthProvider')
  return ctx
}
