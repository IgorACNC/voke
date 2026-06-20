import { useEffect, useState } from 'react'
import './EventClock.css'

type Variant = 'compact' | 'large' | 'meta'

interface Props {
  /** ISO date string ou Date — o instante até onde contar. */
  targetDate: string | Date
  /** Texto à esquerda do timecode (ex.: "PORTAS ABREM") */
  label?: string
  /** Texto à direita do timecode (ex.: nome do evento, "LIVE NOW") */
  trailing?: string
  /** Forma de exibição */
  variant?: Variant
  /** Quando true, exibe "CLOSED · {dataFormatada}" em vez de countdown. */
  closed?: boolean
  /** Override de classname para o wrapper. */
  className?: string
}

interface Parts {
  d: number; h: number; m: number; s: number; total: number
}

function calcParts(target: Date): Parts {
  const total = target.getTime() - Date.now()
  if (total <= 0) return { d: 0, h: 0, m: 0, s: 0, total }
  const d = Math.floor(total / (1000 * 60 * 60 * 24))
  const h = Math.floor((total / (1000 * 60 * 60)) % 24)
  const m = Math.floor((total / (1000 * 60)) % 60)
  const s = Math.floor((total / 1000) % 60)
  return { d, h, m, s, total }
}

function toneFor(total: number): string {
  if (total <= 0) return 'tone-hush'
  const oneDay = 24 * 60 * 60 * 1000
  const sevenDays = 7 * oneDay
  if (total < 60 * 60 * 1000) return 'tone-ember'      // < 1h
  if (total < oneDay) return 'tone-spot'                // < 24h
  if (total < sevenDays) return 'tone-ink'              // 1-7d
  return 'tone-hush'                                    // > 7d
}

function formatTimecode(p: Parts, variant: Variant): string {
  if (p.total <= 0) return '—'
  if (variant === 'compact') {
    if (p.d > 0) return `T−${p.d}D ${p.h}H`
    if (p.h > 0) return `T−${p.h}H ${p.m}M`
    return `T−${p.m}M ${p.s.toString().padStart(2, '0')}S`
  }
  if (variant === 'meta') {
    if (p.d > 0) return `T−${p.d}D`
    if (p.h > 0) return `T−${p.h}H`
    return `T−${p.m}M`
  }
  // large
  if (p.d > 0) return `T−${p.d}D ${p.h.toString().padStart(2, '0')}H ${p.m.toString().padStart(2, '0')}M`
  return `T−${p.h.toString().padStart(2, '0')}H ${p.m.toString().padStart(2, '0')}M ${p.s.toString().padStart(2, '0')}S`
}

function formatClosed(target: Date): string {
  const d = target.getDate().toString().padStart(2, '0')
  const monthsPT = ['JAN', 'FEV', 'MAR', 'ABR', 'MAI', 'JUN', 'JUL', 'AGO', 'SET', 'OUT', 'NOV', 'DEZ']
  const m = monthsPT[target.getMonth()]
  const y = target.getFullYear()
  return `CLOSED · ${d} ${m} ${y}`
}

export default function EventClock({
  targetDate, label, trailing, variant = 'compact', closed, className,
}: Props) {
  const target = typeof targetDate === 'string' ? new Date(targetDate) : targetDate
  const [parts, setParts] = useState<Parts>(() => calcParts(target))

  useEffect(() => {
    if (closed) return
    // Cadência conforme distância: <1h tick 1s, <1d tick 30s, senão tick 60s.
    const id = window.setInterval(() => setParts(calcParts(target)), 1000)
    return () => window.clearInterval(id)
  }, [target.getTime(), closed])

  if (closed) {
    return (
      <span className={`event-clock event-clock--${variant} tone-hush ${className ?? ''}`}>
        <span className="t-time">{formatClosed(target)}</span>
        {trailing && <span className="event-clock__sep">·</span>}
        {trailing && <span className="t-time">{trailing}</span>}
      </span>
    )
  }

  const tone = toneFor(parts.total)
  const isCritical = parts.total > 0 && parts.total < 60 * 60 * 1000
  const sizeClass = variant === 'large' ? 't-time-lg' : 't-time'

  return (
    <span
      className={`event-clock event-clock--${variant} ${tone} ${isCritical ? 'is-critical' : ''} ${className ?? ''}`}
      aria-live={isCritical ? 'polite' : 'off'}
    >
      {label && <span className="event-clock__label t-time tone-hush">{label}</span>}
      <span className={`event-clock__time ${sizeClass}`}>{formatTimecode(parts, variant)}</span>
      {trailing && <span className="event-clock__sep">·</span>}
      {trailing && <span className="event-clock__trailing t-time tone-hush">{trailing}</span>}
    </span>
  )
}
