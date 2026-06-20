import { useEffect, useMemo, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import Header from '../components/Header'
import EventClock from '../components/EventClock'
import { buscarEvento, type Evento } from '../services/eventoService'
import { listarMinhasInscricoes, type Inscricao } from '../services/inscricaoService'
import { buscarPerfil, type PerfilParticipante } from '../services/participanteService'
import { adicionarAoCarrinho } from '../services/carrinhoService'
import { registrarVisualizacao } from '../services/dashboardService'
import { listarCategorias, type Categoria } from '../services/categoriaService'
import FaqPublico from '../components/FaqPublico'
import './EventDetail.css'

function calcularIdade(dataNascimento: string, referencia: string) {
  const dob = new Date(dataNascimento)
  const ref = new Date(referencia)
  let idade = ref.getFullYear() - dob.getFullYear()
  const m = ref.getMonth() - dob.getMonth()
  if (m < 0 || (m === 0 && ref.getDate() < dob.getDate())) idade--
  return idade
}

function periodoSobrepoe(aStart: string, aEnd: string, bStart: string, bEnd: string) {
  const as = new Date(aStart).getTime()
  const ae = new Date(aEnd).getTime()
  const bs = new Date(bStart).getTime()
  const be = new Date(bEnd).getTime()
  return as < be && bs < ae
}

function fmtBRL(n: number) {
  return n.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })
}

function fmtData(iso: string) {
  return new Date(iso).toLocaleString('pt-BR', {
    weekday: 'long', day: '2-digit', month: 'long', year: 'numeric',
    hour: '2-digit', minute: '2-digit',
  })
}

export default function EventDetail() {
  const { eventoId } = useParams()
  const navigate = useNavigate()
  const { usuario } = useAuth()

  const [evento, setEvento] = useState<Evento | null>(null)
  const [perfil, setPerfil] = useState<PerfilParticipante | null>(null)
  const [minhas, setMinhas] = useState<Inscricao[]>([])
  const [categorias, setCategorias] = useState<Categoria[]>([])
  const [erro, setErro] = useState('')
  const [carregando, setCarregando] = useState(false)

  useEffect(() => {
    if (!eventoId) return
    buscarEvento(eventoId).then(setEvento).catch(() => setErro('Não conseguimos carregar este evento.'))
    listarCategorias().then(setCategorias).catch(() => {})
    registrarVisualizacao(eventoId).catch(() => {})
    if (usuario?.papel === 'PARTICIPANTE') {
      buscarPerfil(usuario.id).then(setPerfil).catch(() => {})
      listarMinhasInscricoes(usuario.id).then(setMinhas).catch(() => {})
    }
  }, [eventoId, usuario?.id])

  const ev = evento
  const precoBase = ev?.loteAtual?.preco ?? 0
  const vagas = ev?.loteAtual ? ev.loteAtual.quantidadeTotal - ev.loteAtual.quantidadeVendida : 0

  const idadeParticipante = ev && perfil?.dataNascimento
    ? calcularIdade(perfil.dataNascimento, ev.dataHoraInicio)
    : null
  const bloqueadoPorIdade =
    !!ev && ev.idadeMinima > 0 && (idadeParticipante === null || idadeParticipante < ev.idadeMinima)

  const conflito = !!ev && minhas.some(
    (i) =>
      i.status === 'CONFIRMADA' &&
      periodoSobrepoe(i.evento.dataHoraInicio, i.evento.dataHoraFim, ev.dataHoraInicio, ev.dataHoraFim),
  )

  const jaInscrito = !!ev && minhas.some(
    (i) => i.evento.id === ev.id && (i.status === 'CONFIRMADA' || i.status === 'CHECK_IN_REALIZADO'),
  )

  const eventoJaIniciado = !!ev && new Date(ev.dataHoraInicio).getTime() <= Date.now()
  const eventoEncerrado = ev?.status === 'ENCERRADO' || ev?.status === 'CANCELADO'

  const categoriasDoEvento = useMemo(() => {
    if (!ev) return [] as string[]
    return ev.categoriaIds
      .map((id) => categorias.find((c) => c.id === id)?.nome)
      .filter(Boolean) as string[]
  }, [ev, categorias])

  async function handleAdicionarCarrinho() {
    setErro('')
    if (!ev || !usuario) return setErro('Usuário não autenticado.')
    if (eventoJaIniciado) return setErro('Inscrições encerradas: o evento já começou.')
    if (!ev.loteAtual || !ev.loteAtual.ativo) return setErro('Não há lote ativo neste evento.')
    if (vagas <= 0) return setErro('Lote esgotado.')
    if (bloqueadoPorIdade) return setErro('Você não atende à idade mínima do evento.')
    if (conflito) return setErro('Conflito de agenda com outra inscrição confirmada.')
    if (jaInscrito) return setErro('Você já está inscrito neste evento.')
    setCarregando(true)
    try {
      await adicionarAoCarrinho(usuario.id, ev.id, ev.nome, 1, precoBase)
      navigate('/carrinho')
    } catch (e: any) {
      setErro(e?.response?.data?.mensagem ?? 'Não foi possível adicionar ao carrinho.')
    } finally {
      setCarregando(false)
    }
  }

  if (!ev) {
    return (
      <div className="evd">
        <Header />
        <main className="evd-main container">
          {erro ? (
            <p className="t-body tone-ember">{erro}</p>
          ) : (
            <p className="t-body tone-hush">Carregando evento…</p>
          )}
        </main>
      </div>
    )
  }

  const ctaDisabled =
    carregando || bloqueadoPorIdade || conflito || vagas <= 0 || eventoJaIniciado || eventoEncerrado

  return (
    <div className="evd">
      <Header
        eyebrow={
          <EventClock
            targetDate={ev.dataHoraInicio}
            variant="compact"
            label="Começa em"
            closed={eventoEncerrado}
          />
        }
      />

      <main className="evd-main container--wide">
        <button type="button" className="evd-back t-meta tone-hush" onClick={() => navigate(-1)}>
          ← Voltar
        </button>

        <div className="evd-grid">
          {/* Programação */}
          <article className="evd-program">
            <header className="evd-program__head">
              <p className="t-eyebrow tone-hush">Evento</p>
              <h1 className="t-display evd-program__title">{ev.nome}</h1>
              {categoriasDoEvento.length > 0 && (
                <ul className="evd-tags" aria-label="Categorias">
                  {categoriasDoEvento.map((c) => (
                    <li key={c} className="badge">{c}</li>
                  ))}
                </ul>
              )}
            </header>

            {ev.descricao && (
              <section className="evd-section">
                <p className="t-eyebrow tone-hush">Sobre</p>
                <p className="t-body evd-desc">{ev.descricao}</p>
              </section>
            )}

            <section className="evd-section">
              <p className="t-eyebrow tone-hush">Quando</p>
              <p className="t-h3 evd-when">{fmtData(ev.dataHoraInicio)}</p>
              <p className="t-meta tone-hush">até {fmtData(ev.dataHoraFim)}</p>
            </section>

            <section className="evd-section">
              <p className="t-eyebrow tone-hush">Onde</p>
              <p className="t-h3">{ev.local}</p>
              {ev.idadeMinima > 0 && (
                <p className="t-meta tone-hush">Classificação: {ev.idadeMinima}+</p>
              )}
            </section>

            {eventoId && (
              <section className="evd-section">
                <FaqPublico eventoId={eventoId} />
              </section>
            )}
          </article>

          {/* Guichê (ticket office) */}
          <aside className="evd-box">
            <div className="evd-box__inner">
              <p className="t-eyebrow tone-hush">Guichê</p>
              <div className="evd-box__clock">
                <EventClock
                  targetDate={ev.dataHoraInicio}
                  variant="large"
                  closed={eventoEncerrado}
                />
              </div>

              {ev.loteAtual && (
                <div className="evd-box__lote">
                  <p className="t-meta tone-hush">Lote em venda</p>
                  <p className="t-mega evd-box__preco">{fmtBRL(precoBase)}</p>
                  <p className="t-meta tone-hush">
                    Lote {ev.loteAtual.numero} ·{' '}
                    {vagas > 0 ? `${vagas} de ${ev.loteAtual.quantidadeTotal} vagas` : 'esgotado'}
                  </p>
                </div>
              )}

              {erro && (
                <p className="t-meta evd-box__erro" role="alert">{erro}</p>
              )}

              {jaInscrito && (
                <p className="badge badge--moss evd-box__badge">Você já está inscrito</p>
              )}
              {!jaInscrito && bloqueadoPorIdade && (
                <p className="t-meta tone-ember">Você não atende à idade mínima ({ev.idadeMinima}+).</p>
              )}
              {!jaInscrito && conflito && (
                <p className="t-meta tone-ember">Conflito de agenda com outra inscrição confirmada.</p>
              )}
              {!jaInscrito && eventoJaIniciado && (
                <p className="t-meta tone-ember">Inscrições encerradas: o evento já começou.</p>
              )}
              {eventoEncerrado && (
                <p className="t-meta tone-hush">Este evento foi {ev.status === 'CANCELADO' ? 'cancelado' : 'encerrado'}.</p>
              )}

              {usuario?.papel === 'PARTICIPANTE' && !jaInscrito && (
                <button
                  type="button"
                  className="btn btn--primary btn--lg evd-box__cta"
                  onClick={handleAdicionarCarrinho}
                  disabled={ctaDisabled}
                >
                  {carregando ? 'Adicionando…' : 'Adicionar ao carrinho'}
                </button>
              )}

              {!usuario && (
                <button
                  type="button"
                  className="btn btn--primary btn--lg evd-box__cta"
                  onClick={() => navigate('/login')}
                >
                  Entrar para se inscrever
                </button>
              )}
            </div>
          </aside>
        </div>
      </main>
    </div>
  )
}
