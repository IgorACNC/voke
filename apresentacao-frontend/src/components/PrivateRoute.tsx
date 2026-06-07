import { Navigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import type { ReactNode } from 'react'
import type { Papel } from '../types/auth'

interface Props {
  children: ReactNode
  papelRequerido?: Papel
}

export default function PrivateRoute({ children, papelRequerido }: Props) {
  const { usuario, estaAutenticado } = useAuth()
  if (!estaAutenticado) return <Navigate to="/login" replace />
  if (papelRequerido && usuario?.papel !== papelRequerido) return <Navigate to="/dashboard" replace />
  return <>{children}</>
}
