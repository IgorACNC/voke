import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext'
import PrivateRoute from './components/PrivateRoute'
import Auth from './pages/Auth'
import Dashboard from './pages/Dashboard'
import MeuPerfil from './pages/MeuPerfil'
import AmigosComunidades from './pages/AmigosComunidades'
import Avaliacoes from './pages/Avaliacoes'
import ChatPrivado from './pages/ChatPrivado'
import EsqueciSenha from './pages/EsqueciSenha'
import RedefinirSenha from './pages/RedefinirSenha'
import Admin from './pages/Admin'
import Onboarding from './pages/Onboarding'

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<Auth />} />
          <Route path="/esqueci-senha" element={<EsqueciSenha />} />
          <Route path="/redefinir-senha" element={<RedefinirSenha />} />
          <Route path="/dashboard" element={<PrivateRoute><Dashboard /></PrivateRoute>} />
          <Route path="/admin" element={<PrivateRoute papelRequerido="ADMIN"><Admin /></PrivateRoute>} />
          <Route path="/onboarding" element={<PrivateRoute papelRequerido="PARTICIPANTE"><Onboarding /></PrivateRoute>} />
          <Route path="/meu-perfil" element={<PrivateRoute papelRequerido="PARTICIPANTE"><MeuPerfil /></PrivateRoute>} />
          <Route path="/amigos-comunidades" element={<PrivateRoute papelRequerido="PARTICIPANTE"><AmigosComunidades /></PrivateRoute>} />
          <Route path="/avaliacoes" element={<PrivateRoute papelRequerido="PARTICIPANTE"><Avaliacoes /></PrivateRoute>} />
          <Route path="/avaliacoes/:eventoId" element={<PrivateRoute papelRequerido="PARTICIPANTE"><Avaliacoes /></PrivateRoute>} />
          <Route path="/chat-privado" element={<PrivateRoute papelRequerido="PARTICIPANTE"><ChatPrivado /></PrivateRoute>} />
          <Route path="*" element={<Navigate to="/login" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  )
}
