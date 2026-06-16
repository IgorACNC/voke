import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { buscarEvento, type Evento } from '../services/eventoService'
import { criarInscricao, listarMinhasInscricoes, validarInscricao, type Inscricao } from '../services/inscricaoService'
import { validarCupom } from '../services/cupomService'
import { buscarPerfil, type PerfilParticipante } from '../services/participanteService'
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

  const [cupom, setCupom] = useState('')
  const [cupomValido, setCupomValido] = useState<any>(null)
  const [erro, setErro] = useState('')
  const [mensagem, setMensagem] = useState('')
  const [carregando, setCarregando] = useState(false)

  useEffect(() => {
    if (!eventoId) return
    buscarEvento(eventoId).then(setEvento).catch(() => setErro('Erro ao carregar evento.'))
    if (usuario?.papel === 'PARTICIPANTE') {
      buscarPerfil(usuario.id).then(setPerfil).catch(() => { /* ignore */ })
      listarMinhasInscricoes(usuario.id).then(setMinhas).catch(() => { /* ignore */ })
    }
  }, [eventoId, usuario?.id])

  if (!evento) return null
  const ev = evento!

  const precoBase = ev.loteAtual?.preco ?? 0
  const vagas = ev.loteAtual ? ev.loteAtual.quantidadeTotal - ev.loteAtual.quantidadeVendida : 0

  const idadeParticipante = perfil?.dataNascimento ? calcularIdade(perfil.dataNascimento, ev.dataHoraInicio) : null
  const bloqueadoPorIdade = ev.idadeMinima > 0 && (idadeParticipante === null || idadeParticipante < ev.idadeMinima)

  const conflito = minhas.some((i) => i.status === 'CONFIRMADA' && periodoSobrepoe(i.evento.dataHoraInicio, i.evento.dataHoraFim, ev.dataHoraInicio, ev.dataHoraFim))


  const precoFinal = (() => {
    if (!cupomValido) return precoBase
    if (cupomValido.percentual) return Math.max(0, precoBase * (1 - cupomValido.valor / 100))
    return Math.max(0, precoBase - cupomValido.valor)
  })()

  async function aplicarCupom() {
    setErro('')
    setMensagem('')
    if (!cupom) return setErro('Informe um codigo de cupom.')
    try {
      const res = await validarCupom(cupom, evento!.id)
      if (!res.valido) return setErro('Cupom invalido para este evento.')
      setCupomValido(res.cupom)
      setMensagem('Cupom aplicado.')
    } catch (e: unknown) {
      setErro((e as any)?.response?.data?.mensagem ?? 'Erro ao validar cupom.')
    }
  }

  async function comprar() {
    setErro('')
    setMensagem('')
    if (!usuario) return setErro('Usuario nao autenticado.')
    if (!ev.loteAtual || !ev.loteAtual.ativo) return setErro('Nao ha lote ativo.')
    if (vagas <= 0) return setErro('Lote esgotado.')
    if (bloqueadoPorIdade) return setErro('Voce nao atende a idade minima do evento.')
    if (conflito) return setErro('Conflito de agenda com outra inscricao confirmada.')

    // tentar validar no servidor regras compostas (idade, conflito, limite por CPF, vigencia do lote)
    // se a validação remota falhar (rota não existente ou indisponível), prosseguir com validações client-side já aplicadas acima
    try {
      const valid = await validarInscricao(usuario.id, ev.id)
      if (!valid.ok) {
        setErro(valid.motivos?.join('; ') || 'Validacao falhou no servidor.')
        return
      }
    } catch (e: unknown) {
      console.warn('validarInscricao nao disponivel, aplicando validacoes client-side como fallback', e)
      // continua com verificacoes locais
    }

    setCarregando(true)
    try {
      await criarInscricao(usuario.id, ev.id, precoFinal)
      setMensagem('Inscricao realizada com sucesso.')
      navigate('/minhas-inscricoes')
    } catch (e: unknown) {
      const err = e as any
      const msg = err?.response?.data?.mensagem
      if (err?.response?.status === 409) {
        setErro(msg ?? 'Conflito: possivelmente lote ja reservado por outra transacao.')
      } else if (err?.response?.status === 410) {
        setErro(msg ?? 'Lote Esgotado.')
      } else {
        setErro(msg ?? 'Erro ao criar inscricao. Pode ser lote esgotado.')
      }
    } finally {
      setCarregando(false)
    }
  }

  return (
    <div className="social-bg">
      <header className="social-header">
        <button className="social-voltar" onClick={() => navigate('/explorar-eventos')}>Voltar</button>
        <span className="social-logo">Voke</span>
        <div style={{ width: 90 }} />
      </header>

      <main className="social-main">
        <section className="social-title">
          <h1>{ev.nome}</h1>
          <p>{ev.descricao}</p>
        </section>

        {mensagem && <p className="social-msg-sucesso">{mensagem}</p>}
        {erro && <p className="social-msg-erro">{erro}</p>}

        <div className="social-card">
          <p><strong>Local:</strong> {ev.local}</p>
          <p><strong>Data:</strong> {new Date(ev.dataHoraInicio).toLocaleString('pt-BR')} - {new Date(ev.dataHoraFim).toLocaleString('pt-BR')}</p>
          {ev.idadeMinima > 0 && <p><strong>Idade minima:</strong> {ev.idadeMinima} anos</p>}
          {ev.loteAtual && (
            <p><strong>Lote atual:</strong> #{ev.loteAtual.numero} — R$ {ev.loteAtual.preco.toFixed(2)} — {vagas} vagas</p>
          )}

          <div style={{ marginTop: 12 }}>
            <label>
              Codigo do Cupom
              <input value={cupom} onChange={(e) => setCupom(e.target.value)} />
            </label>
            <button className="social-btn-sec" onClick={aplicarCupom}>Aplicar Cupom</button>
          </div>

          <div style={{ marginTop: 12 }}>
            <p><strong>Preco final:</strong> R$ {precoFinal.toFixed(2)}</p>
            {usuario?.papel === 'PARTICIPANTE' && (
              <div>
                <button disabled={carregando} onClick={comprar}>Confirmar Inscricao</button>
              </div>
            )}
          </div>
        </div>
      </main>
    </div>
  )
}
