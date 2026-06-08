import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import './Dashboard.css'

export default function Dashboard() {
  const { usuario, sair } = useAuth()
  const navigate = useNavigate()

  function handleSair() { sair(); navigate('/login') }

  const cardsOrganizador = [
    { icon: '📅', label: 'Meus Eventos' },
    { icon: '🎟️', label: 'Ingressos / Lotes' },
    { icon: '🏷️', label: 'Cupons' },
    { icon: '🎁', label: 'Recompensas' },
    { icon: '📊', label: 'Relatórios' },
    { icon: '🔔', label: 'Notificações' },
  ]

  const cardsParticipante = [
    { icon: '🔍', label: 'Explorar Eventos' },
    { icon: '🎫', label: 'Minhas Inscrições' },
    { icon: '🛒', label: 'Carrinho' },
    { icon: '⭐', label: 'Favoritos' },
    { icon: '🏆', label: 'Meus Pontos' },
    { icon: '👛', label: 'Carteira Virtual' },
    { icon: '★', label: 'Avaliacoes', rota: '/avaliacoes' },
    { icon: '👥', label: 'Amigos e Comunidades', rota: '/amigos-comunidades' },
    { icon: '💬', label: 'Chat Privado', rota: '/chat-privado' },
    { icon: '👤', label: 'Minha Conta', rota: '/meu-perfil' },
  ]

  const cards = usuario?.papel === 'ORGANIZADOR' ? cardsOrganizador : cardsParticipante

  return (
    <div className="dash-bg">
      <header className="dash-header">
        <span className="dash-logo">Voke</span>
        <div className="dash-header-right">
          <span className="dash-papel">{usuario?.papel}</span>
          <span className="dash-nome">{usuario?.nome}</span>
          <button className="dash-sair" onClick={handleSair}>Sair</button>
        </div>
      </header>

      <main className="dash-main">
        <div className="dash-boas-vindas">
          <h1>Olá, {usuario?.nome?.split(' ')[0]}!</h1>
          <p>{usuario?.papel === 'ORGANIZADOR' ? 'Gerencie seus eventos, ingressos e recompensas.' : 'Descubra eventos, faça inscrições e acompanhe suas recompensas.'}</p>
        </div>
        <div className="dash-cards">
          {cards.map((c) => (
            <div
              key={c.label}
              className="dash-card"
              onClick={() => 'rota' in c && c.rota && navigate(c.rota)}
            >
              <span className="dash-card-icon">{c.icon}</span>
              <span className="dash-card-label">{c.label}</span>
            </div>
          ))}
        </div>
      </main>
    </div>
  )
}
