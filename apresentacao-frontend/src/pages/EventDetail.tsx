import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { buscarEvento, type Evento } from '../services/eventoService'
import { listarMinhasInscricoes, type Inscricao } from '../services/inscricaoService'
import { buscarPerfil, type PerfilParticipante } from '../services/participanteService'
import { adicionarAoCarrinho } from '../services/carrinhoService'
import { registrarVisualizacao } from '../services/dashboardService'
import FaqPublico from '../components/FaqPublico'
import './Social.css'

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

export default function EventDetail() {
  const { eventoId } = useParams()
  const navigate = useNavigate()
  const { usuario } = useAuth()

  const [evento, setEvento] = useState<Evento | null>(null)
  const [perfil, setPerfil] = useState<PerfilParticipante | null>(null)
  const [minhas, setMinhas] = useState<Inscricao[]>([])
  const [erro, setErro] = useState('')
  const [carregando, setCarregando] = useState(false)

  useEffect(() => {
    if (!eventoId) return
    buscarEvento(eventoId).then(setEvento).catch(() => setErro('Erro ao carregar evento.'))
    // F17 - RN03/visualizacoes: incrementa contador (silencioso em caso de erro)
    registrarVisualizacao(eventoId).catch(() => {})
    if (usuario?.papel === 'PARTICIPANTE') {
      buscarPerfil(usuario.id).then(setPerfil).catch(() => {})
      listarMinhasInscricoes(usuario.id).then(setMinhas).catch(() => {})
    }
  }, [eventoId, usuario?.id])

  if (!evento) return null
  const ev = evento

  const precoBase = ev.loteAtual?.preco ?? 0
  const vagas = ev.loteAtual ? ev.loteAtual.quantidadeTotal - ev.loteAtual.quantidadeVendida : 0

  const idadeParticipante = perfil?.dataNascimento
    ? calcularIdade(perfil.dataNascimento, ev.dataHoraInicio)
    : null
  const bloqueadoPorIdade =
    ev.idadeMinima > 0 && (idadeParticipante === null || idadeParticipante < ev.idadeMinima)

  const conflito = minhas.some(
    (i) =>
      i.status === 'CONFIRMADA' &&
      periodoSobrepoe(i.evento.dataHoraInicio, i.evento.dataHoraFim, ev.dataHoraInicio, ev.dataHoraFim),
  )

  const jaInscrito = minhas.some(
    (i) => i.evento.id === ev.id && (i.status === 'CONFIRMADA' || i.status === 'CHECK_IN_REALIZADO'),
  )

  const eventoJaIniciado = new Date(ev.dataHoraInicio).getTime() <= Date.now()

  async function handleAdicionarCarrinho() {
    setErro('')
    if (!usuario) return setErro('Usuário não autenticado.')
    if (eventoJaIniciado) return setErro('Inscrições encerradas: o evento já começou.')
    if (!ev.loteAtual || !ev.loteAtual.ativo) return setErro('Não há lote ativo para este evento.')
    if (vagas <= 0) return setErro('Lote esgotado.')
    if (bloqueadoPorIdade) return setErro('Você não atende à idade mínima do evento.')
    if (conflito) return setErro('Conflito de agenda com outra inscrição confirmada.')
    if (jaInscrito) return setErro('Você já está inscrito neste evento.')

    setCarregando(true)
    try {
      await adicionarAoCarrinho(usuario.id, ev.id, ev.nome, 1, precoBase)
      navigate('/carrinho')
    } catch (e: any) {
      setErro(e?.response?.data?.mensagem ?? 'Erro ao adicionar ao carrinho.')
    } finally {
      setCarregando(false)
    }
  }

  return (
    <div className="social-bg">
      <header className="social-header">
        <button className="social-voltar" onClick={() => navigate(-1)}>Voltar</button>
        <span className="social-logo">Voke</span>
        <div style={{ width: 90 }} />
      </header>

      <main className="social-main">
        <section className="social-title">
          <h1>{ev.nome}</h1>
          <p>{ev.descricao}</p>
        </section>

        {erro && <p className="social-msg-erro">{erro}</p>}

        <div className="social-card">
          <p><strong>Local:</strong> {ev.local}</p>
          <p>
            <strong>Data:</strong>{' '}
            {new Date(ev.dataHoraInicio).toLocaleString('pt-BR')} –{' '}
            {new Date(ev.dataHoraFim).toLocaleString('pt-BR')}
          </p>
          {ev.idadeMinima > 0 && (
            <p><strong>Idade mínima:</strong> {ev.idadeMinima} anos</p>
          )}
          {ev.loteAtual && (
            <p>
              <strong>Lote:</strong> #{ev.loteAtual.numero} — {vagas > 0 ? `${vagas} vagas disponíveis` : 'Esgotado'}{' '}
              — <strong>{new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(precoBase)}</strong>
            </p>
          )}

          {/* Alertas de validação */}
          {bloqueadoPorIdade && (
            <p className="social-msg-erro">Você não atende à idade mínima ({ev.idadeMinima} anos) deste evento.</p>
          )}
          {conflito && (
            <p className="social-msg-erro">Você já tem uma inscrição confirmada no mesmo horário.</p>
          )}
          {jaInscrito && (
            <p className="social-msg-sucesso">Você já está inscrito neste evento.</p>
          )}
          {eventoJaIniciado && !jaInscrito && (
            <p className="social-msg-erro">Inscrições encerradas: o evento já começou.</p>
          )}

          {usuario?.papel === 'PARTICIPANTE' && !jaInscrito && (
            <div style={{ marginTop: '1rem' }}>
              <button
                onClick={handleAdicionarCarrinho}
                disabled={carregando || bloqueadoPorIdade || conflito || vagas <= 0 || eventoJaIniciado}
              >
                {carregando ? 'Adicionando...' : '🛒 Adicionar ao Carrinho'}
              </button>
            </div>
          )}

          {eventoId && <FaqPublico eventoId={eventoId} />}
        </div>
      </main>
    </div>
  )
}
