import { useEffect, useMemo, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import Header from '../components/Header'
import EventClock from '../components/EventClock'
import EventoTira from '../components/EventoTira'
import { listarEventosAtivos, type Evento } from '../services/eventoService'
import './CatalogoPublico.css'

export default function CatalogoPublico() {
  const navigate = useNavigate()
  const [eventos, setEventos] = useState<Evento[]>([])
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState('')
  const [busca, setBusca] = useState('')

  useEffect(() => {
    listarEventosAtivos()
      .then(setEventos)
      .catch(() => setErro('Não foi possível carregar os eventos.'))
      .finally(() => setCarregando(false))
  }, [])

  const eventosOrdenados = useMemo(() => {
    return [...eventos].sort(
      (a, b) => new Date(a.dataHoraInicio).getTime() - new Date(b.dataHoraInicio).getTime(),
    )
  }, [eventos])

  const eventosFiltrados = useMemo(() => {
    const termo = busca.trim().toLowerCase()
    if (!termo) return eventosOrdenados
    return eventosOrdenados.filter(
      (ev) => ev.nome.toLowerCase().includes(termo) || ev.local.toLowerCase().includes(termo),
    )
  }, [eventosOrdenados, busca])

  const headline = eventosOrdenados[0]
  const restantes = eventosFiltrados.filter((ev) => ev.id !== headline?.id)

  return (
    <div className="cat">
      <Header eyebrow={<span className="t-time tone-on-ink-soft">Catálogo aberto · sem login</span>} />

      <section className="cat-hero">
        <div className="cat-hero__inner">
          <p className="t-eyebrow tone-spot cat-hero__kicker">Em destaque agora</p>

          {carregando && (
            <p className="t-body tone-on-ink-soft cat-hero__loading">Carregando programação…</p>
          )}

          {!carregando && headline && (
            <>
              <h1 className="t-mega tone-on-ink cat-hero__title">{headline.nome}</h1>
              <div className="cat-hero__clock">
                <EventClock
                  targetDate={headline.dataHoraInicio}
                  variant="large"
                  label="Começa em"
                  trailing={headline.local}
                />
              </div>
              <div className="cat-hero__meta">
                {headline.loteAtual && (
                  <span className="t-meta tone-on-ink-soft">
                    Lote {headline.loteAtual.numero} · {headline.loteAtual.preco.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}
                  </span>
                )}
                {headline.loteAtual && (
                  <span className="t-meta tone-on-ink-soft">
                    {headline.loteAtual.quantidadeTotal - headline.loteAtual.quantidadeVendida} vagas
                  </span>
                )}
                {headline.idadeMinima > 0 && (
                  <span className="t-meta tone-on-ink-soft">{headline.idadeMinima}+</span>
                )}
              </div>
              <div className="cat-hero__actions">
                <button type="button" className="btn btn--primary btn--lg" onClick={() => navigate('/login')}>
                  Entrar para se inscrever
                </button>
                <button type="button" className="btn btn--ghost btn--lg cat-hero__ghost" onClick={() => document.getElementById('cat-prox')?.scrollIntoView({ behavior: 'smooth' })}>
                  Ver agenda completa
                </button>
              </div>
            </>
          )}

          {!carregando && !headline && !erro && (
            <p className="t-h2 tone-on-ink-soft cat-hero__empty">
              Nenhum evento publicado por enquanto. Volte logo.
            </p>
          )}

          {erro && (
            <p className="t-body tone-spot cat-hero__error">{erro}</p>
          )}
        </div>
      </section>

      <main className="cat-main container--wide" id="cat-prox">
        <div className="cat-sectionhead">
          <div className="cat-sectionhead__title">
            <p className="t-eyebrow tone-hush">Programação</p>
            <h2 className="t-display">Próximos eventos</h2>
          </div>
          <div className="cat-sectionhead__count">
            <span className="t-time tone-hush">
              {eventosFiltrados.length.toString().padStart(2, '0')} EVENTOS
            </span>
          </div>
        </div>

        <div className="cat-search">
          <label className="sr-only" htmlFor="cat-busca">Buscar evento por nome ou local</label>
          <input
            id="cat-busca"
            className="cat-search__input"
            placeholder="Buscar por nome ou local"
            value={busca}
            onChange={(e) => setBusca(e.target.value)}
          />
        </div>

        {carregando && (
          <p className="t-body tone-hush cat-info">Carregando próximos eventos…</p>
        )}

        {!carregando && eventosFiltrados.length === 0 && (
          <div className="cat-empty">
            <p className="t-h2">Nada por aqui</p>
            <p className="t-body tone-hush">Ajuste a busca ou volte mais tarde.</p>
          </div>
        )}

        <div className="cat-lista">
          {restantes.map((ev) => (
            <EventoTira
              key={ev.id}
              id={ev.id}
              nome={ev.nome}
              local={ev.local}
              dataHoraInicio={ev.dataHoraInicio}
              preco={ev.loteAtual?.preco ?? null}
              loteNumero={ev.loteAtual?.numero ?? null}
              vagasDisponiveis={
                ev.loteAtual ? ev.loteAtual.quantidadeTotal - ev.loteAtual.quantidadeVendida : null
              }
              href="/login"
              ctaLabel="Entrar para ver"
              status={ev.status}
            />
          ))}
        </div>
      </main>

      <footer className="cat-footer container--wide">
        <span className="t-time tone-hush">VOKE · {new Date().getFullYear()}</span>
      </footer>
    </div>
  )
}
