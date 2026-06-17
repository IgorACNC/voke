import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import {
  type Convite,
  enviarConvite,
  listarConvitesRecebidos,
  listarConvitesEnviados,
  aceitarConvite,
  rejeitarConvite,
  cancelarConvite,
} from '../services/conviteService'
import { listarEventosAtivos, type Evento } from '../services/eventoService'
import './Social.css'

type Aba = 'recebidos' | 'enviados'

const STATUS_LABEL: Record<string, string> = {
  PENDENTE: 'Pendente',
  ACEITO: 'Aceito',
  REJEITADO: 'Rejeitado',
  CANCELADO: 'Cancelado',
  EXPIRADO: 'Expirado',
}

const STATUS_COR: Record<string, string> = {
  PENDENTE: '#f59e0b',
  ACEITO: '#10b981',
  REJEITADO: '#ef4444',
  CANCELADO: '#6b7280',
  EXPIRADO: '#6b7280',
}

export default function Convites() {
  const navigate = useNavigate()
  const { usuario } = useAuth()
  const [aba, setAba] = useState<Aba>('recebidos')
  const [recebidos, setRecebidos] = useState<Convite[]>([])
  const [enviados, setEnviados] = useState<Convite[]>([])
  const [eventos, setEventos] = useState<Evento[]>([])
  const [carregando, setCarregando] = useState(false)
  const [erro, setErro] = useState('')
  const [mensagem, setMensagem] = useState('')

  const [emailDestinatario, setEmailDestinatario] = useState('')
  const [eventoId, setEventoId] = useState('')
  const [enviando, setEnviando] = useState(false)

  useEffect(() => {
    if (!usuario) return
    carregar()
    listarEventosAtivos()
      .then(setEventos)
      .catch(() => {})
  }, [usuario?.id])

  async function carregar() {
    if (!usuario) return
    setErro('')
    setCarregando(true)
    try {
      const [rec, env] = await Promise.all([
        listarConvitesRecebidos(usuario.id),
        listarConvitesEnviados(usuario.id),
      ])
      setRecebidos(rec)
      setEnviados(env)
    } catch {
      setErro('Não foi possível carregar os convites.')
    } finally {
      setCarregando(false)
    }
  }

  async function handleEnviar(e: React.FormEvent) {
    e.preventDefault()
    if (!usuario) return
    setErro('')
    setEnviando(true)
    try {
      await enviarConvite(usuario.id, emailDestinatario, eventoId)
      setMensagem('Convite enviado com sucesso.')
      setEmailDestinatario('')
      setEventoId('')
      await carregar()
    } catch (err: unknown) {
      setErro((err as any)?.response?.data?.mensagem ?? 'Erro ao enviar convite.')
    } finally {
      setEnviando(false)
    }
  }

  async function handleAceitar(c: Convite) {
    if (!usuario) return
    setErro('')
    try {
      await aceitarConvite(c.id, usuario.id)
      setMensagem('Convite aceito.')
      await carregar()
    } catch (err: unknown) {
      setErro((err as any)?.response?.data?.mensagem ?? 'Erro ao aceitar convite.')
    }
  }

  async function handleRejeitar(c: Convite) {
    if (!usuario) return
    setErro('')
    try {
      await rejeitarConvite(c.id, usuario.id)
      setMensagem('Convite rejeitado.')
      await carregar()
    } catch (err: unknown) {
      setErro((err as any)?.response?.data?.mensagem ?? 'Erro ao rejeitar convite.')
    }
  }

  async function handleCancelar(c: Convite) {
    if (!usuario) return
    if (!confirm('Deseja cancelar este convite?')) return
    setErro('')
    try {
      await cancelarConvite(c.id, usuario.id)
      setMensagem('Convite cancelado.')
      await carregar()
    } catch (err: unknown) {
      setErro((err as any)?.response?.data?.mensagem ?? 'Erro ao cancelar convite.')
    }
  }

  if (!usuario) return null

  const lista = aba === 'recebidos' ? recebidos : enviados

  return (
    <div className="social-bg">
      <header className="social-header">
        <button className="social-voltar" onClick={() => navigate('/dashboard')}>Voltar</button>
        <span className="social-logo">Voke</span>
        <div style={{ width: 90 }} />
      </header>

      <main className="social-main">
        <section className="social-title">
          <h1>Convites</h1>
          <p>Envie e gerencie convites para eventos.</p>
        </section>

        {mensagem && <p className="social-msg-sucesso" onClick={() => setMensagem('')}>{mensagem}</p>}
        {erro && <p className="social-msg-erro" onClick={() => setErro('')}>{erro}</p>}

        <div className="social-card">
          <h2>Enviar Convite</h2>
          <form className="social-form-col" onSubmit={handleEnviar}>
            <label>
              E-mail do participante
              <input
                type="email"
                placeholder="participante@email.com"
                value={emailDestinatario}
                onChange={(e) => setEmailDestinatario(e.target.value)}
                required
              />
            </label>
            <label>
              Evento
              <select
                value={eventoId}
                onChange={(e) => setEventoId(e.target.value)}
                required
              >
                <option value="">Selecione um evento...</option>
                {eventos.map((ev) => (
                  <option key={ev.id} value={ev.id}>
                    {ev.nome} — {new Date(ev.dataHoraInicio).toLocaleDateString('pt-BR')}
                  </option>
                ))}
              </select>
            </label>
            <button disabled={enviando}>{enviando ? 'Enviando...' : 'Enviar Convite'}</button>
          </form>
        </div>

        <div style={{ display: 'flex', gap: 8, margin: '16px 0' }}>
          <button
            className={aba === 'recebidos' ? 'social-btn-ativo' : 'social-btn-sec'}
            onClick={() => setAba('recebidos')}
          >
            Recebidos ({recebidos.length})
          </button>
          <button
            className={aba === 'enviados' ? 'social-btn-ativo' : 'social-btn-sec'}
            onClick={() => setAba('enviados')}
          >
            Enviados ({enviados.length})
          </button>
        </div>

        {carregando ? (
          <p>Carregando...</p>
        ) : (
          <div className="social-card">
            {lista.length === 0 && (
              <p className="social-vazio">
                {aba === 'recebidos' ? 'Nenhum convite recebido.' : 'Nenhum convite enviado.'}
              </p>
            )}
            {lista.map((c) => (
              <div key={c.id} className="convite-item">
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <strong>{c.eventoNome}</strong>
                  <span style={{
                    fontSize: '0.75rem',
                    fontWeight: 600,
                    color: STATUS_COR[c.status] ?? '#6b7280',
                    background: (STATUS_COR[c.status] ?? '#6b7280') + '22',
                    padding: '2px 8px',
                    borderRadius: 12,
                  }}>
                    {STATUS_LABEL[c.status] ?? c.status}
                  </span>
                </div>
                <p style={{ fontSize: '0.85rem', color: '#9ca3af', margin: '4px 0' }}>
                  {aba === 'recebidos'
                    ? `De: ${c.remetenteNome}`
                    : `Para: ${c.destinatarioNome}`}
                </p>
                <p style={{ fontSize: '0.75rem', color: '#6b7280' }}>
                  Expira em: {new Date(c.expiraEm).toLocaleString('pt-BR')}
                </p>
                {aba === 'recebidos' && c.status === 'PENDENTE' && (
                  <div style={{ display: 'flex', gap: 8, marginTop: 8 }}>
                    <button onClick={() => handleAceitar(c)}>Aceitar</button>
                    <button className="social-btn-sec" onClick={() => handleRejeitar(c)}>Rejeitar</button>
                  </div>
                )}
                {aba === 'recebidos' && c.status === 'ACEITO' && (
                  <button
                    style={{ marginTop: 8 }}
                    onClick={() => navigate(`/eventos/${c.eventoId}`)}
                  >
                    Ver Evento
                  </button>
                )}
                {aba === 'enviados' && c.status === 'PENDENTE' && (
                  <button
                    className="social-btn-sec"
                    style={{ marginTop: 8 }}
                    onClick={() => handleCancelar(c)}
                  >
                    Cancelar Convite
                  </button>
                )}
              </div>
            ))}
          </div>
        )}
      </main>
    </div>
  )
}
