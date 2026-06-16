import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { temPreferencias } from '../services/sugestaoService'
import { consultarCarteira } from '../services/carteiraService'
import './Dashboard.css'

export default function Dashboard() {
  const { usuario, sair } = useAuth()
  const navigate = useNavigate()
  const [saldo, setSaldo] = useState<number | null>(null)

  useEffect(() => {
    if (usuario?.papel === 'PARTICIPANTE') {
      temPreferencias(usuario.id)
        .then((configurado) => { if (!configurado) navigate('/onboarding', { replace: true }) })
        .catch(() => { /* ignora erro silenciosamente */ })

      consultarCarteira(usuario.id)
        .then((c) => setSaldo(Number(c.saldo)))
        .catch(() => { /* saldo indisponivel, exibe nada */ })
    }
  }, [usuario, navigate])

  function handleSair() { sair(); navigate('/login') }

  const cardsOrganizador = [
    { icon: '📅', label: 'Meus Eventos', rota: '/meus-eventos' },
    { icon: '🎟️', label: 'Ingressos / Lotes', rota: '/meus-eventos' },
    { icon: '🏷️', label: 'Cupons', rota: '/cupons' },
    { icon: '🎁', label: 'Recompensas' },
    { icon: '📊', label: 'Relatórios' },
    { icon: '🔔', label: 'Notificações' },
  ]

  const cardsParticipante = [
    { icon: '🔍', label: 'Explorar Eventos', rota: '/explorar-eventos' },
    { icon: '🎫', label: 'Minhas Inscrições', rota: '/minhas-inscricoes' },
    { icon: '🛒', label: 'Carrinho' },
    { icon: '⭐', label: 'Favoritos', rota: '/favoritos' },
    { icon: '🏆', label: 'Meus Pontos' },
    { icon: '💲', label: 'Carteira Virtual', rota: '/carteira' },
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
          {saldo !== null && (
            <span className="dash-saldo">
              {saldo.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}
            </span>
          )}
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
