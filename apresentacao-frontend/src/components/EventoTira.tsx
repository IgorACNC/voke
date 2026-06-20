import { useNavigate } from 'react-router-dom'
import EventClock from './EventClock'
import './EventoTira.css'

interface Props {
  id: string
  nome: string
  local: string
  dataHoraInicio: string
  preco?: number | null
  loteNumero?: number | null
  vagasDisponiveis?: number | null
  /** Quando truthy, navega para /eventos/:id ao clicar. Caso contrário, render passivo. */
  href?: string
  /** Render alternativo do CTA (ex.: "Ver detalhe", "Inscrever-se"). Default: "Abrir". */
  ctaLabel?: string
  /** Status do evento para badge */
  status?: 'ATIVO' | 'ENCERRADO' | 'CANCELADO' | 'PUBLICADO' | 'RASCUNHO' | string
}

function fmtBRL(n: number) {
  return n.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })
}

export default function EventoTira({
  id, nome, local, dataHoraInicio, preco, loteNumero, vagasDisponiveis,
  href, ctaLabel = 'Abrir', status,
}: Props) {
  const navigate = useNavigate()
  const target = href ?? `/eventos/${id}`
  const isClosed = status === 'ENCERRADO' || status === 'CANCELADO'

  return (
    <article className={`evento-tira ${isClosed ? 'evento-tira--closed' : ''}`}>
      <div className="evento-tira__main">
        <h3 className="evento-tira__nome t-h2">{nome}</h3>
        <div className="evento-tira__meta">
          <span className="t-meta tone-hush">{local}</span>
          {loteNumero != null && (
            <span className="t-meta tone-hush">Lote {loteNumero}</span>
          )}
          {preco != null && (
            <span className="t-meta tone-ink">{fmtBRL(preco)}</span>
          )}
          {vagasDisponiveis != null && vagasDisponiveis > 0 && (
            <span className="t-meta tone-hush">{vagasDisponiveis} vagas</span>
          )}
          {status === 'CANCELADO' && <span className="badge badge--ember">Cancelado</span>}
        </div>
      </div>

      <div className="evento-tira__clock">
        <EventClock
          targetDate={dataHoraInicio}
          variant="compact"
          closed={isClosed}
        />
      </div>

      <div className="evento-tira__cta">
        <button
          type="button"
          className="btn btn--ghost btn--sm"
          onClick={() => navigate(target)}
        >
          {ctaLabel}
        </button>
      </div>
    </article>
  )
}
