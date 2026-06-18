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
import CarteiraVirtual from './pages/CarteiraVirtual'
import MeusEventos from './pages/MeusEventos'
import CriarEvento from './pages/CriarEvento'
import EditarEvento from './pages/EditarEvento'
import GrupoEvento from './pages/GrupoEvento'
import CriarGrupo from './pages/CriarGrupo'
import ExplorarEventos from './pages/ExplorarEventos'
import CatalogoPublico from './pages/CatalogoPublico'
import Subgrupos from './pages/Subgrupos'
import CriarSubgrupo from './pages/CriarSubgrupo'
import SubgrupoDetalhe from './pages/SubgrupoDetalhe'
import MinhasSolicitacoesSubgrupo from './pages/MinhasSolicitacoesSubgrupo'
import MinhasInscricoes from './pages/MinhasInscricoes'
import Cupons from './pages/Cupons'
import EventDetail from './pages/EventDetail'
import Favoritos from './pages/Favoritos'
import PerfilOrganizador from './pages/PerfilOrganizador'
import MeuPerfilOrganizador from './pages/MeuPerfilOrganizador'
import NotificacoesEvento from './pages/NotificacoesEvento'
import MinhasNotificacoes from './pages/MinhasNotificacoes'
import Carrinho from './pages/Carrinho'
import Convites from './pages/Convites'

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<CatalogoPublico />} />
          <Route path="/catalogo" element={<CatalogoPublico />} />
          <Route path="/login" element={<Auth />} />
          <Route path="/esqueci-senha" element={<EsqueciSenha />} />
          <Route path="/redefinir-senha" element={<RedefinirSenha />} />

          <Route path="/dashboard" element={<PrivateRoute><Dashboard /></PrivateRoute>} />
          <Route path="/admin" element={<PrivateRoute papelRequerido="ADMIN"><Admin /></PrivateRoute>} />

          {/* Rotas do Organizador */}
          <Route path="/organizadores/:id" element={<PerfilOrganizador />} />
          <Route path="/minha-conta-organizador" element={<PrivateRoute papelRequerido="ORGANIZADOR"><MeuPerfilOrganizador /></PrivateRoute>} />
          <Route path="/meus-eventos" element={<PrivateRoute papelRequerido="ORGANIZADOR"><MeusEventos /></PrivateRoute>} />
          <Route path="/eventos/:eventoId/notificacoes" element={<PrivateRoute papelRequerido="ORGANIZADOR"><NotificacoesEvento /></PrivateRoute>} />
          <Route path="/notificacoes" element={<PrivateRoute papelRequerido="PARTICIPANTE"><MinhasNotificacoes /></PrivateRoute>} />
          <Route path="/meus-eventos/novo" element={<PrivateRoute papelRequerido="ORGANIZADOR"><CriarEvento /></PrivateRoute>} />
          <Route path="/meus-eventos/:id/editar" element={<PrivateRoute papelRequerido="ORGANIZADOR"><EditarEvento /></PrivateRoute>} />
          <Route path="/eventos/:eventoId/grupo/criar" element={<PrivateRoute papelRequerido="ORGANIZADOR"><CriarGrupo /></PrivateRoute>} />

          {/* Grupo acessível por Organizador e Participante */}
          <Route path="/eventos/:eventoId/grupo" element={<PrivateRoute><GrupoEvento /></PrivateRoute>} />

          {/* Subgrupos */}
          <Route path="/eventos/:eventoId/grupo/subgrupos" element={<PrivateRoute><Subgrupos /></PrivateRoute>} />
          <Route path="/eventos/:eventoId/grupo/subgrupos/novo" element={<PrivateRoute papelRequerido="ORGANIZADOR"><CriarSubgrupo /></PrivateRoute>} />
          <Route path="/subgrupos/:id" element={<PrivateRoute><SubgrupoDetalhe /></PrivateRoute>} />
          <Route path="/minhas-solicitacoes-subgrupo" element={<PrivateRoute papelRequerido="PARTICIPANTE"><MinhasSolicitacoesSubgrupo /></PrivateRoute>} />

          {/* Rotas do Participante */}
          <Route path="/explorar-eventos" element={<PrivateRoute papelRequerido="PARTICIPANTE"><ExplorarEventos /></PrivateRoute>} />
          <Route path="/onboarding" element={<PrivateRoute papelRequerido="PARTICIPANTE"><Onboarding /></PrivateRoute>} />
          <Route path="/meu-perfil" element={<PrivateRoute papelRequerido="PARTICIPANTE"><MeuPerfil /></PrivateRoute>} />
          <Route path="/amigos-comunidades" element={<PrivateRoute papelRequerido="PARTICIPANTE"><AmigosComunidades /></PrivateRoute>} />
          <Route path="/avaliacoes" element={<PrivateRoute papelRequerido="PARTICIPANTE"><Avaliacoes /></PrivateRoute>} />
          <Route path="/avaliacoes/:eventoId" element={<PrivateRoute papelRequerido="PARTICIPANTE"><Avaliacoes /></PrivateRoute>} />
          <Route path="/chat-privado" element={<PrivateRoute papelRequerido="PARTICIPANTE"><ChatPrivado /></PrivateRoute>} />
          <Route path="/carteira" element={<PrivateRoute papelRequerido="PARTICIPANTE"><CarteiraVirtual /></PrivateRoute>} />
          <Route path="/minhas-inscricoes" element={<PrivateRoute papelRequerido="PARTICIPANTE"><MinhasInscricoes /></PrivateRoute>} />
          <Route path="/carrinho" element={<PrivateRoute papelRequerido="PARTICIPANTE"><Carrinho /></PrivateRoute>} />
          <Route path="/favoritos" element={<PrivateRoute papelRequerido="PARTICIPANTE"><Favoritos /></PrivateRoute>} />
          <Route path="/eventos/:eventoId" element={<PrivateRoute><EventDetail /></PrivateRoute>} />

          <Route path="/cupons" element={<PrivateRoute><Cupons /></PrivateRoute>} />
          <Route path="/convites" element={<PrivateRoute papelRequerido="PARTICIPANTE"><Convites /></PrivateRoute>} />

          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  )
}
