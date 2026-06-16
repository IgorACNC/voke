import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { listarMeusEventos, cancelarEvento, type Evento } from '../services/eventoService'
import './MeusEventos.css'

function statusLabel(s: string) {
  if (s === 'ATIVO') return 'ATIVO'
  if (s === 'CANCELADO') return 'CANCELADO'
  if (s === 'ENCERRADO') return 'ENCERRADO'
  return s
}

export default function MeusEventos() {
  const { usuario, sair } = useAuth()
  const navigate = useNavigate()
  const [eventos, setEventos] = useState<Evento[]>([])
  const [carregando, setCarregando] = useState(false)
  const [erro, setErro] = useState('')
  const [confirmarCancelId, setConfirmarCancelId] = useState<string | null>(null)

  useEffect(() => { carregar() }, [])

  async function carregar() {
    setCarregando(true)
    setErro('')
    try {
      setEventos(await listarMeusEventos())
    } catch {
      setErro('Erro ao carregar eventos.')
    } finally {
      setCarregando(false)
    }
  }

  async function handleCancelar(id: string) {
    try {
      await cancelarEvento(id)
      setConfirmarCancelId(null)
      await carregar()
    } catch (err: unknown) {
      const msg = (err as { response?: { data?: { mensagem?: string } } })?.response?.data?.mensagem
      setErro(msg || 'Erro ao cancelar evento.')
    }
  }

  function handleSair() { sair(); navigate('/login') }

  return (
    <div className="mev-bg">
      <header className="mev-header">
        <span className="mev-logo" onClick={() => navigate('/dashboard')}>Voke</span>
        <div className="mev-header-right">
          <span className="mev-papel">{usuario?.papel}</span>
          <span className="mev-nome">{usuario?.nome}</span>
          <button className="mev-sair" onClick={handleSair}>Sair</button>
        </div>
      </header>

      <main className="mev-main">
        <div className="mev-topo">
          <div>
            <h1 className="mev-titulo">Meus Eventos</h1>
            <p className="mev-sub">Gerencie os eventos que você criou</p>
          </div>
          <button className="mev-btn-novo" onClick={() => navigate('/meus-eventos/novo')}>
            + Novo Evento
          </button>
        </div>

        {erro && <div className="mev-erro">{erro}</div>}
        {carregando && <p className="mev-vazio">Carregando...</p>}

        {!carregando && eventos.length === 0 && (
          <div className="mev-vazio-box">
            <p>Você ainda não criou nenhum evento.</p>
            <button className="mev-btn-novo" onClick={() => navigate('/meus-eventos/novo')}>
              Criar meu primeiro evento
            </button>
          </div>
        )}

        <div className="mev-lista">
          {eventos.map((ev) => (
            <div key={ev.id} className="mev-card">
              <div className="mev-card-topo">
                <div>
                  <h2 className="mev-card-nome">{ev.nome}</h2>
                  <p className="mev-card-data">
                    {new Date(ev.dataHoraInicio).toLocaleDateString('pt-BR')} &mdash;&nbsp;
                    {ev.local}
                  </p>
                </div>
                <span className={`mev-badge mev-badge--${ev.status.toLowerCase()}`}>
                  {statusLabel(ev.status)}
                </span>
              </div>

              {ev.loteAtual && (
                <p className="mev-card-lote">
                  Lote {ev.loteAtual.numero} &bull; R$&nbsp;{ev.loteAtual.preco.toFixed(2)} &bull;&nbsp;
                  {ev.loteAtual.quantidadeVendida}/{ev.loteAtual.quantidadeTotal} vendidos &bull;&nbsp;
                  {ev.loteAtual.ativo ? 'EM VENDA' : 'ENCERRADO'}
                </p>
              )}

              <div className="mev-card-acoes">
                {ev.status === 'ATIVO' && (
                  <>
                    <button className="mev-btn-editar" onClick={() => navigate(`/meus-eventos/${ev.id}/editar`)}>
                      Editar
                    </button>
                    <button className="mev-btn-grupo" onClick={() => navigate(`/eventos/${ev.id}/grupo`)}>
                      Grupo
                    </button>
                    <button className="mev-btn-cancelar" onClick={() => setConfirmarCancelId(ev.id)}>
                      Cancelar Evento
                    </button>
                  </>
                )}
                {ev.status !== 'ATIVO' && (
                  <button className="mev-btn-editar" onClick={() => navigate(`/meus-eventos/${ev.id}/editar`)}>
                    Ver detalhes
                  </button>
                )}
              </div>
            </div>
          ))}
        </div>
      </main>

      {confirmarCancelId && (
        <div className="mev-modal-bg" onClick={() => setConfirmarCancelId(null)}>
          <div className="mev-modal" onClick={(e) => e.stopPropagation()}>
            <h2>Cancelar evento?</h2>
            <p>Esta ação não pode ser desfeita. O evento será marcado como cancelado e as inscrições serão invalidadas.</p>
            <div className="mev-modal-acoes">
              <button className="mev-btn-editar" onClick={() => setConfirmarCancelId(null)}>Voltar</button>
              <button className="mev-btn-cancelar" onClick={() => handleCancelar(confirmarCancelId)}>
                Confirmar cancelamento
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
