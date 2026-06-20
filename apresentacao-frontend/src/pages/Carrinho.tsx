import { useEffect, useRef, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import {
  consultarCarrinho,
  removerDoCarrinho,
  aplicarCupomCarrinho,
  removerCupomCarrinho,
  finalizarCompra,
  calcularTotalCarrinho,
  TAXA_CARTAO_CARRINHO,
  MINUTOS_EXPIRACAO_CARRINHO,
  type CarrinhoResp,
  type FinalizarResp,
  type MetodoPagamentoCarrinho,
} from '../services/carrinhoService'
import { buscarPerfil } from '../services/participanteService'
import { consultarCarteira } from '../services/carteiraService'
import './Social.css'
import './Carrinho.css'

const fmt = (v: number) => v.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })

function calcularSegundosRestantes(criadoEm: string): number {
  const expiracao = new Date(criadoEm).getTime() + MINUTOS_EXPIRACAO_CARRINHO * 60 * 1000
  return Math.max(0, Math.floor((expiracao - Date.now()) / 1000))
}

function formatarTimer(segundos: number): string {
  const m = Math.floor(segundos / 60)
  const s = segundos % 60
  return `${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`
}

export default function Carrinho() {
  const navigate = useNavigate()
  const { usuario } = useAuth()

  const [carrinho, setCarrinho] = useState<CarrinhoResp | null>(null)
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState('')
  const [mensagem, setMensagem] = useState('')

  const [cpf, setCpf] = useState<string | null>(null)
  const [saldo, setSaldo] = useState<number | null>(null)

  const [codigoCupom, setCodigoCupom] = useState('')
  const [aplicandoCupom, setAplicandoCupom] = useState(false)
  const [removendoCupom, setRemovendoCupom] = useState(false)

  const [metodo, setMetodo] = useState<MetodoPagamentoCarrinho>('PIX')
  const [finalizando, setFinalizando] = useState(false)
  const [removendo, setRemovendo] = useState<string | null>(null)

  const [resultado, setResultado] = useState<FinalizarResp | null>(null)

  const [segundosRestantes, setSegundosRestantes] = useState(0)
  const timerRef = useRef<ReturnType<typeof setInterval> | null>(null)

  useEffect(() => {
    if (!usuario) return
    carregar()
    buscarPerfil(usuario.id).then((p) => setCpf(p.cpf ?? null)).catch(() => {})
    consultarCarteira(usuario.id).then((c) => setSaldo(Number(c.saldo))).catch(() => {})
  }, [usuario?.id])

  useEffect(() => {
    if (!carrinho) return
    setSegundosRestantes(calcularSegundosRestantes(carrinho.criadoEm))

    timerRef.current = setInterval(() => {
      setSegundosRestantes((prev) => {
        if (prev <= 1) {
          clearInterval(timerRef.current!)
          setCarrinho((c) => c ? { ...c, expirado: true } : c)
          return 0
        }
        return prev - 1
      })
    }, 1000)

    return () => { if (timerRef.current) clearInterval(timerRef.current) }
  }, [carrinho?.criadoEm])

  if (!usuario) return null

  async function carregar() {
    setCarregando(true)
    setErro('')
    try {
      const c = await consultarCarrinho(usuario!.id)
      setCarrinho(c)
      if (c) setSegundosRestantes(calcularSegundosRestantes(c.criadoEm))
    } catch {
      setErro('Não foi possível carregar o carrinho.')
    } finally {
      setCarregando(false)
    }
  }

  async function handleRemover(eventoId: string) {
    setErro('')
    setRemovendo(eventoId)
    try {
      await removerDoCarrinho(usuario!.id, eventoId)
      await carregar()
    } catch (e: any) {
      setErro(e?.response?.data?.mensagem ?? 'Erro ao remover item.')
    } finally {
      setRemovendo(null)
    }
  }

  async function handleAplicarCupom(e: React.FormEvent) {
    e.preventDefault()
    if (!codigoCupom.trim()) return setErro('Informe o código do cupom.')
    if (!cpf) return setErro('CPF não cadastrado no seu perfil. Atualize em "Minha Conta".')
    setErro('')
    setMensagem('')
    setAplicandoCupom(true)
    try {
      const atualizado = await aplicarCupomCarrinho(usuario!.id, codigoCupom.trim(), cpf)
      setCarrinho(atualizado)
      setCodigoCupom('')
      setMensagem('Cupom aplicado com sucesso!')
    } catch (e: any) {
      setErro(e?.response?.data?.mensagem ?? 'Cupom inválido ou expirado.')
    } finally {
      setAplicandoCupom(false)
    }
  }

  async function handleRemoverCupom() {
    setErro('')
    setMensagem('')
    setRemovendoCupom(true)
    try {
      const atualizado = await removerCupomCarrinho(usuario!.id, cpf ?? '')
      setCarrinho(atualizado)
      setMensagem('Cupom removido.')
    } catch (e: any) {
      setErro(e?.response?.data?.mensagem ?? 'Erro ao remover cupom.')
    } finally {
      setRemovendoCupom(false)
    }
  }

  async function handleFinalizar() {
    setErro('')
    setMensagem('')
    if (!carrinho || carrinho.itens.length === 0) return setErro('Carrinho está vazio.')
    if (carrinho.expirado || segundosRestantes === 0)
      return setErro('O tempo do carrinho expirou. Adicione os itens novamente.')
    const total = calcularTotalCarrinho(subtotal, carrinho.descontoCupom, metodo)
    if (saldo !== null && saldo < total)
      return setErro(`Saldo insuficiente. Seu saldo: ${fmt(saldo)} · Total: ${fmt(total)}`)
    setFinalizando(true)
    try {
      const res = await finalizarCompra(usuario!.id, metodo)
      if (timerRef.current) clearInterval(timerRef.current)
      setResultado(res)
    } catch (e: any) {
      setErro(e?.response?.data?.mensagem ?? 'Erro ao finalizar compra.')
    } finally {
      setFinalizando(false)
    }
  }

  // ── Sucesso ──
  if (resultado) {
    return (
      <div className="social-bg">
        <header className="social-header">
          <div style={{ width: 90 }} />
          <span className="social-logo">Voke</span>
          <div style={{ width: 90 }} />
        </header>
        <main className="social-main">
          <div className="carrinho-sucesso-card">
            <span className="carrinho-sucesso-icon">🎉</span>
            <h2 className="carrinho-sucesso-titulo">Pagamento confirmado!</h2>
            <p className="carrinho-sucesso-subtitulo">Suas inscrições foram realizadas com sucesso.</p>
            <div className="carrinho-sucesso-valor">{fmt(resultado.total)}</div>
            <br />
            <span className="carrinho-sucesso-ids">
              ✓ {resultado.inscricoesIds.length} inscrição(ões) confirmada(s)
            </span>
            <div className="carrinho-sucesso-acoes">
              <button className="carrinho-btn-primario" onClick={() => navigate('/minhas-inscricoes')}>
                Ver minhas inscrições
              </button>
              <button className="carrinho-btn-secundario" onClick={() => navigate('/explorar-eventos')}>
                Explorar eventos
              </button>
            </div>
          </div>
        </main>
      </div>
    )
  }

  const subtotal = carrinho?.itens.reduce((acc, i) => acc + i.subtotal, 0) ?? 0
  const desconto = carrinho?.descontoCupom ?? 0
  const taxa = metodo === 'CARTAO_CREDITO' ? Math.max(0, subtotal - desconto) * TAXA_CARTAO_CARRINHO : 0
  const total = calcularTotalCarrinho(subtotal, desconto, metodo)
  const expirado = carrinho?.expirado || segundosRestantes === 0
  const vazio = !carrinho || carrinho.itens.length === 0

  return (
    <div className="social-bg">
      <header className="social-header">
        <button className="social-voltar" onClick={() => navigate(-1)}>Voltar</button>
        <span className="social-logo">Voke</span>
        <div style={{ width: 90 }} />
      </header>

      <main className="social-main">
        <section className="social-title">
          <h1>Carrinho</h1>
          <p>Revise seus itens e finalize o pagamento via carteira virtual.</p>
        </section>

        {mensagem && <p className="social-msg-sucesso">{mensagem}</p>}
        {erro && <p className="social-msg-erro">{erro}</p>}

        {/* Timer */}
        {carrinho && !vazio && (
          <div className={`carrinho-timer-card ${expirado ? 'expirado' : ''}`}>
            <div className="carrinho-timer-esq">
              <span className="carrinho-timer-label">
                {expirado ? 'Tempo expirado' : 'Tempo restante'}
              </span>
              <div className="carrinho-timer-count">
                {expirado ? '00:00' : formatarTimer(segundosRestantes)}
              </div>
            </div>
            <span className="carrinho-timer-aviso">
              {expirado
                ? 'Adicione os itens novamente ao carrinho.'
                : `Itens reservados por ${MINUTOS_EXPIRACAO_CARRINHO} min.`}
            </span>
          </div>
        )}

        {carregando ? (
          <p style={{ color: '#9ca3af', textAlign: 'center', padding: '2rem' }}>Carregando carrinho...</p>
        ) : vazio ? (
          <div className="carrinho-vazio">
            <span className="carrinho-vazio-icon">🛒</span>
            <p className="carrinho-vazio-msg">Seu carrinho está vazio</p>
            <p className="carrinho-vazio-sub">Explore os eventos e adicione ingressos ao carrinho.</p>
            <button className="carrinho-btn-primario" onClick={() => navigate('/explorar-eventos')}>
              Explorar eventos
            </button>
          </div>
        ) : (
          <div className="carrinho-layout">

            {/* ── Coluna esquerda ── */}
            <div className="carrinho-col-esq">

              {/* Itens */}
              <div className="carrinho-card">
                <h2>🎫 Itens ({carrinho!.itens.length})</h2>
                {carrinho!.itens.map((item) => (
                  <div key={item.eventoId} className="carrinho-item">
                    <div className="carrinho-item-info">
                      <p className="carrinho-item-nome">{item.nomeEvento}</p>
                      <p className="carrinho-item-detalhe">
                        {item.quantidade}× · {fmt(item.precoUnitario)} cada
                      </p>
                    </div>
                    <span className="carrinho-item-subtotal">{fmt(item.subtotal)}</span>
                    <button
                      className="carrinho-item-remover"
                      onClick={() => handleRemover(item.eventoId)}
                      disabled={expirado || removendo === item.eventoId}
                    >
                      {removendo === item.eventoId ? '...' : 'Remover'}
                    </button>
                  </div>
                ))}
              </div>

              {/* Cupom */}
              <div className="carrinho-card">
                <h2>🏷️ Cupom de desconto</h2>
                {!carrinho!.cupomAplicado ? (
                  <form onSubmit={handleAplicarCupom}>
                    <div className="carrinho-cupom-row">
                      <input
                        placeholder="Ex: VOKE-A3K9PQ27"
                        value={codigoCupom}
                        onChange={(e) => setCodigoCupom(e.target.value.toUpperCase())}
                        disabled={expirado}
                      />
                      <button type="submit" className="carrinho-cupom-btn" disabled={aplicandoCupom || expirado}>
                        {aplicandoCupom ? 'Aplicando...' : 'Aplicar'}
                      </button>
                    </div>
                    <p style={{ fontSize: '0.78rem', color: '#6b7280', marginTop: 6 }}>
                      O código é gerado quando você resgata um cupom em <strong>Catálogo de Recompensas</strong>.
                    </p>
                  </form>
                ) : (
                  <div className="carrinho-cupom-aplicado">
                    <span className="carrinho-cupom-badge">
                      ✅ {carrinho!.cupomAplicado} — -{fmt(desconto)}
                    </span>
                    <button
                      type="button"
                      className="carrinho-cupom-remover"
                      onClick={handleRemoverCupom}
                      disabled={removendoCupom || expirado}
                    >
                      {removendoCupom ? 'Removendo...' : 'Remover'}
                    </button>
                  </div>
                )}
              </div>

            </div>

            {/* ── Coluna direita ── */}
            <div className="carrinho-col-dir">

              {/* Resumo */}
              <div className="carrinho-card">
                <h2>🧾 Resumo</h2>
                <div className="carrinho-resumo">
                  <div className="carrinho-resumo-linha">
                    <span>Subtotal</span>
                    <span>{fmt(subtotal)}</span>
                  </div>
                  {desconto > 0 && (
                    <div className="carrinho-resumo-linha desconto">
                      <span>Desconto ({carrinho!.cupomAplicado})</span>
                      <span>-{fmt(desconto)}</span>
                    </div>
                  )}
                  {taxa > 0 && (
                    <div className="carrinho-resumo-linha taxa">
                      <span>Taxa cartão ({(TAXA_CARTAO_CARRINHO * 100).toFixed(0)}%)</span>
                      <span>+{fmt(taxa)}</span>
                    </div>
                  )}
                  <div className="carrinho-resumo-total">
                    <span>Total</span>
                    <span>{fmt(total)}</span>
                  </div>
                  {saldo !== null && (
                    <div className={`carrinho-saldo-linha ${saldo >= total ? 'carrinho-saldo-ok' : 'carrinho-saldo-insuficiente'}`}>
                      <span>Seu saldo</span>
                      <span>{fmt(saldo)}</span>
                    </div>
                  )}
                </div>
              </div>

              {/* Pagamento */}
              <div className="carrinho-card">
                <h2>💳 Pagamento</h2>
                <p className="carrinho-secao-sub">Debitado da sua carteira virtual.</p>

                <div className="carrinho-metodo-grupo">
                  <label className={`carrinho-metodo-opcao ${metodo === 'PIX' ? 'selecionado' : ''}`}>
                    <input type="radio" name="metodo" value="PIX" checked={metodo === 'PIX'} onChange={() => setMetodo('PIX')} />
                    <span className="carrinho-metodo-check">✓</span>
                    <span className="carrinho-metodo-icon">🏦</span>
                    <span className="carrinho-metodo-label">PIX</span>
                    <span className="carrinho-metodo-sub">Sem taxas</span>
                  </label>

                  <label className={`carrinho-metodo-opcao ${metodo === 'CARTAO_CREDITO' ? 'selecionado' : ''}`}>
                    <input type="radio" name="metodo" value="CARTAO_CREDITO" checked={metodo === 'CARTAO_CREDITO'} onChange={() => setMetodo('CARTAO_CREDITO')} />
                    <span className="carrinho-metodo-check">✓</span>
                    <span className="carrinho-metodo-icon">💳</span>
                    <span className="carrinho-metodo-label">Cartão</span>
                    <span className="carrinho-metodo-sub">+{(TAXA_CARTAO_CARRINHO * 100).toFixed(0)}% taxa</span>
                  </label>
                </div>

                <button
                  className="carrinho-btn-finalizar"
                  onClick={handleFinalizar}
                  disabled={finalizando || expirado || vazio}
                >
                  {finalizando ? 'Processando...' : expirado ? 'Carrinho expirado' : `Pagar ${fmt(total)}`}
                </button>
              </div>

            </div>
          </div>
        )}
      </main>
    </div>
  )
}
