import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { listarMinhasInscricoes, cancelarInscricao, estimarEstorno, type Inscricao } from '../services/inscricaoService'
import './Social.css'

export default function MinhasInscricoes() {
  const navigate = useNavigate()
  const { usuario } = useAuth()
  const [inscricoes, setInscricoes] = useState<Inscricao[]>([])
  const [carregando, setCarregando] = useState(false)
  const [erro, setErro] = useState('')
  const [mensagem, setMensagem] = useState('')

  useEffect(() => {
    if (!usuario) return
    carregar()
  }, [usuario?.id])

  async function carregar() {
    setErro('')
    setCarregando(true)
    try {
      const dados = await listarMinhasInscricoes(usuario!.id)
      setInscricoes(dados)
    } catch {
      setErro('Nao foi possivel carregar suas inscricoes.')
    } finally {
      setCarregando(false)
    }
  }

  function calcularPercentualEstorno(dataInicioEvento: string): number {
    const now = Date.now()
    const inicio = new Date(dataInicioEvento).getTime()
    const diffMs = inicio - now
    const diffHours = diffMs / (1000 * 60 * 60)
    const diffDays = diffHours / 24
    // regras: >=7 dias => 100%; >=48 horas => 50%; <48 horas => 0%
    if (diffDays >= 7) return 1
    if (diffHours >= 48) return 0.5
    return 0
  }

  function eventoFinalizado(dataFimEvento: string): boolean {
    return new Date(dataFimEvento).getTime() < Date.now()
  }

  function podeAvaliar(inscricao: Inscricao): boolean {
    return (inscricao.status === 'CONFIRMADA' || inscricao.status === 'CHECK_IN_REALIZADO')
      && eventoFinalizado(inscricao.evento.dataHoraFim)
  }

  async function handleCancelar(id: string) {
    const inscr = inscricoes.find((x) => x.id === id)
    if (!inscr) return
    setErro('')
    let valorEstorno: number | null = null
    try {
      const resp = await estimarEstorno(id)
      valorEstorno = Math.round(resp.valor * 100) / 100
    } catch {
      const percentual = calcularPercentualEstorno(inscr.evento.dataHoraInicio)
      valorEstorno = Math.round(inscr.valorPago * percentual * 100) / 100
    }
    if (!confirm(`Deseja cancelar esta inscricao? Estorno estimado: R$ ${valorEstorno.toFixed(2)}`)) return

    try {
      await cancelarInscricao(id)
      setMensagem('Inscricao cancelada com sucesso.')
      await carregar()
    } catch (e: unknown) {
      setErro((e as any)?.response?.data?.mensagem ?? 'Nao foi possivel cancelar a inscricao.')
    }
  }

  if (!usuario) return null

  return (
    <div className="social-bg">
      <header className="social-header">
        <button className="social-voltar" onClick={() => navigate('/dashboard')}>Voltar</button>
        <span className="social-logo">Voke</span>
        <div style={{ width: 90 }} />
      </header>

      <main className="social-main">
        <section className="social-title">
          <h1>Minhas Inscrições</h1>
          <p>Visualize seus ingressos confirmados e cancele sua participação quando permitido.</p>
        </section>

        {mensagem && <p className="social-msg-sucesso">{mensagem}</p>}
        {erro && <p className="social-msg-erro">{erro}</p>}

        {carregando ? (
          <p>Carregando...</p>
        ) : (
          <div className="social-card">
            {inscricoes.length === 0 && <p className="social-vazio">Nenhuma inscricao encontrada.</p>}
            {inscricoes.map((i) => (
              <div key={i.id} className="inscricao-item">
                <h3>{i.evento.nome}</h3>
                <p>{new Date(i.evento.dataHoraInicio).toLocaleString('pt-BR')} - {new Date(i.evento.dataHoraFim).toLocaleString('pt-BR')}</p>
                <p>Lote: {i.loteNumero ?? '—'} | Status: {i.status}</p>
                <div style={{ marginTop: 8 }}>
                  <button onClick={() => navigate(`/eventos/${i.evento.id}/grupo`)}>Ver evento</button>
                  {podeAvaliar(i) && (
                    <button onClick={() => navigate(`/avaliacoes/${i.evento.id}`, { state: { evento: i.evento } })}>
                      Avaliar evento
                    </button>
                  )}
                  {i.status === 'CONFIRMADA' && (
                    <button className="social-btn-sec" onClick={() => handleCancelar(i.id)}>Cancelar Inscrição</button>
                  )}
                </div>
              </div>
            ))}
          </div>
        )}
      </main>
    </div>
  )
}
