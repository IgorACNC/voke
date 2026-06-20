import { useEffect, useMemo, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import Header from '../components/Header'
import EventClock from '../components/EventClock'
import { useAuth } from '../context/AuthContext'
import { temPreferencias } from '../services/sugestaoService'
import { consultarCarteira } from '../services/carteiraService'
import { listarMinhasInscricoes, type Inscricao } from '../services/inscricaoService'
import './Dashboard.css'

interface Atalho { icon: string; label: string; rota: string }
interface Grupo { titulo: string; atalhos: Atalho[] }

const organizadorGrupos: Grupo[] = [
  {
    titulo: 'Operação',
    atalhos: [
      { icon: '📅', label: 'Meus eventos', rota: '/meus-eventos' },
      { icon: '🎟️', label: 'Ingressos & lotes', rota: '/meus-eventos' },
      { icon: '🔔', label: 'Notificações', rota: '/meus-eventos' },
    ],
  },
  {
    titulo: 'Crescimento',
    atalhos: [
      { icon: '🏷️', label: 'Cupons', rota: '/cupons' },
      { icon: '🤝', label: 'Parceiros', rota: '/parceiros' },
      { icon: '🎁', label: 'Recompensas', rota: '/recompensas' },
    ],
  },
  {
    titulo: 'Análise',
    atalhos: [
      { icon: '📊', label: 'Dashboard & relatórios', rota: '/dashboard-organizador' },
      { icon: '👤', label: 'Minha conta', rota: '/minha-conta-organizador' },
    ],
  },
]

const participanteGrupos: Grupo[] = [
  {
    titulo: 'Descobrir',
    atalhos: [
      { icon: '🔍', label: 'Explorar eventos', rota: '/explorar-eventos' },
      { icon: '⭐', label: 'Favoritos', rota: '/favoritos' },
      { icon: '💡', label: 'Sugestões pra você', rota: '/sugestoes' },
    ],
  },
  {
    titulo: 'Meus ingressos',
    atalhos: [
      { icon: '🎫', label: 'Minhas inscrições', rota: '/minhas-inscricoes' },
      { icon: '🛒', label: 'Carrinho', rota: '/carrinho' },
      { icon: '✉️', label: 'Convites', rota: '/convites' },
      { icon: '★', label: 'Avaliações', rota: '/avaliacoes' },
    ],
  },
  {
    titulo: 'Carteira',
    atalhos: [
      { icon: '💲', label: 'Carteira virtual', rota: '/carteira' },
      { icon: '🏆', label: 'Meus pontos', rota: '/carteira-pontos' },
      { icon: '🎁', label: 'Catálogo de recompensas', rota: '/catalogo-recompensas' },
      { icon: '🤝', label: 'Painel parceiro', rota: '/painel-parceiro' },
    ],
  },
  {
    titulo: 'Comunidade',
    atalhos: [
      { icon: '👥', label: 'Amigos & comunidades', rota: '/amigos-comunidades' },
      { icon: '💬', label: 'Chat privado', rota: '/chat-privado' },
      { icon: '🔔', label: 'Notificações', rota: '/notificacoes' },
      { icon: '👤', label: 'Minha conta', rota: '/meu-perfil' },
    ],
  },
]

function fmtBRL(n: number) {
  return n.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })
}

export default function Dashboard() {
  const { usuario } = useAuth()
  const navigate = useNavigate()
  const [saldo, setSaldo] = useState<number | null>(null)
  const [proxInscricao, setProxInscricao] = useState<Inscricao | null>(null)

  useEffect(() => {
    if (usuario?.papel === 'PARTICIPANTE') {
      temPreferencias(usuario.id)
        .then((configurado) => { if (!configurado) navigate('/onboarding', { replace: true }) })
        .catch(() => {})

      consultarCarteira(usuario.id)
        .then((c) => setSaldo(Number(c.saldo)))
        .catch(() => {})

      listarMinhasInscricoes(usuario.id)
        .then((lista) => {
          const futuras = lista
            .filter((i) => new Date(i.evento.dataHoraInicio).getTime() > Date.now())
            .sort(
              (a, b) =>
                new Date(a.evento.dataHoraInicio).getTime() -
                new Date(b.evento.dataHoraInicio).getTime(),
            )
          setProxInscricao(futuras[0] ?? null)
        })
        .catch(() => {})
    }
  }, [usuario, navigate])

  const grupos = useMemo(
    () => (usuario?.papel === 'ORGANIZADOR' ? organizadorGrupos : participanteGrupos),
    [usuario?.papel],
  )

  const primeiroNome = usuario?.nome?.split(' ')[0] ?? ''

  const eyebrow = usuario?.papel === 'ORGANIZADOR'
    ? <span className="t-time">PAINEL · ORGANIZADOR</span>
    : saldo !== null
      ? <span className="t-time">SALDO · {fmtBRL(saldo)}</span>
      : <span className="t-time">CARTEIRA</span>

  return (
    <div className="dash">
      <Header eyebrow={eyebrow} />

      <main className="dash-main container--wide">
        <section className="dash-welcome">
          <p className="t-eyebrow tone-hush">Bem-vindo de volta</p>
          <h1 className="t-mega dash-welcome__title">Olá, {primeiroNome}.</h1>
          <p className="t-body tone-hush dash-welcome__sub">
            {usuario?.papel === 'ORGANIZADOR'
              ? 'Acompanhe vendas, presença e receita dos seus eventos.'
              : 'Descubra o que está próximo e mantenha seus ingressos em dia.'}
          </p>

          {usuario?.papel === 'PARTICIPANTE' && proxInscricao && (
            <button
              type="button"
              className="dash-next"
              onClick={() => navigate(`/eventos/${proxInscricao.evento.id}`)}
            >
              <div className="dash-next__head">
                <p className="t-eyebrow tone-spot">Sua próxima entrada</p>
                <p className="t-h2 dash-next__title">{proxInscricao.evento.nome}</p>
                <p className="t-meta tone-hush">{proxInscricao.evento.local}</p>
              </div>
              <EventClock
                targetDate={proxInscricao.evento.dataHoraInicio}
                variant="large"
              />
            </button>
          )}
        </section>

        <section className="dash-shortcuts">
          {grupos.map((g) => (
            <div key={g.titulo} className="dash-group">
              <h2 className="t-eyebrow tone-hush dash-group__title">{g.titulo}</h2>
              <ul className="dash-group__list">
                {g.atalhos.map((a) => (
                  <li key={a.label} className="dash-tira">
                    <button type="button" className="dash-tira__btn" onClick={() => navigate(a.rota)}>
                      <span className="dash-tira__icon" aria-hidden="true">{a.icon}</span>
                      <span className="t-h3 dash-tira__label">{a.label}</span>
                      <span className="dash-tira__arrow t-time tone-hush" aria-hidden="true">→</span>
                    </button>
                  </li>
                ))}
              </ul>
            </div>
          ))}
        </section>
      </main>
    </div>
  )
}
