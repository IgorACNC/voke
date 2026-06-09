import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import {
  depositar,
  consultarCarteira,
  consultarExtrato,
  sacar,
  LIMITE_INSERCAO_DIARIA,
  LIMITE_SAQUE_DIARIO,
  LIMITE_SAQUE_VALOR,
  TAXA_CARTAO,
  type MetodoPagamento,
  type Transacao,
} from '../services/carteiraService'
import './Social.css'
import './CarteiraVirtual.css'

const fmt = (v: number) => v.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })

export default function CarteiraVirtual() {
  const navigate = useNavigate()
  const { usuario } = useAuth()

  const [saldo, setSaldo] = useState<number | null>(null)
  const [extrato, setExtrato] = useState<Transacao[]>([])
  const [carregando, setCarregando] = useState(false)
  const [mensagem, setMensagem] = useState('')
  const [erro, setErro] = useState('')

  const [valorDeposito, setValorDeposito] = useState('')
  const [metodo, setMetodo] = useState<MetodoPagamento>('PIX')

  const [valorSaque, setValorSaque] = useState('')
  const [filtroData, setFiltroData] = useState('')

  useEffect(() => {
    if (!usuario) return
    carregarDados()
  }, [usuario?.id])

  if (!usuario) return null

  async function carregarDados() {
    try {
      const [carteira, historico] = await Promise.all([
        consultarCarteira(usuario!.id),
        consultarExtrato(usuario!.id),
      ])
      setSaldo(Number(carteira.saldo))
      setExtrato(historico)
    } catch {
      setErro('Nao foi possivel carregar a carteira.')
    }
  }

  function calcularDeposito(): { creditado: number; taxa: number } {
    const v = parseFloat(valorDeposito) || 0
    if (metodo !== 'CARTAO_CREDITO') return { creditado: v, taxa: 0 }
    // espelha exatamente o backend: valor * (1 - TAXA), RoundingMode.FLOOR
    const creditado = Math.floor(v * (1 - TAXA_CARTAO) * 100) / 100
    const taxa = parseFloat((v - creditado).toFixed(2))
    return { creditado, taxa }
  }

  async function executar(acao: () => Promise<void>, sucesso: string) {
    setErro('')
    setMensagem('')
    setCarregando(true)
    try {
      await acao()
      await carregarDados()
      setMensagem(sucesso)
    } catch (err: unknown) {
      setErro(
        (err as { response?: { data?: { mensagem?: string } } })?.response?.data?.mensagem ??
        'Operacao nao concluida.',
      )
    } finally {
      setCarregando(false)
    }
  }

  async function handleDeposito(e: React.FormEvent) {
    e.preventDefault()
    const valor = parseFloat(valorDeposito)
    if (!valor || valor <= 0) return
    await executar(async () => {
      await depositar(usuario!.id, valor, metodo)
      setValorDeposito('')
    }, 'Saldo adicionado com sucesso.')
  }

  async function handleSaque(e: React.FormEvent) {
    e.preventDefault()
    const valor = parseFloat(valorSaque)
    if (!valor || valor <= 0) return
    await executar(async () => {
      await sacar(usuario!.id, valor)
      setValorSaque('')
    }, 'Saque solicitado com sucesso.')
  }

  const valorDepositoNum = parseFloat(valorDeposito) || 0

  const extratoFiltrado = filtroData
    ? extrato.filter((t) => t.dataHora.startsWith(filtroData))
    : extrato

  return (
    <div className="social-bg">
      <header className="social-header">
        <button className="social-voltar" onClick={() => navigate('/dashboard')}>Voltar</button>
        <span className="social-logo">Voke</span>
        <div style={{ width: 90 }} />
      </header>

      <main className="social-main">
        <section className="social-title">
          <h1>Carteira Virtual</h1>
          <p>Gerencie seu saldo, realize depositos e saques.</p>
        </section>

        {mensagem && <p className="social-msg-sucesso">{mensagem}</p>}
        {erro && <p className="social-msg-erro">{erro}</p>}

        <div className="carteira-saldo-card">
          <span className="carteira-saldo-label">Saldo disponivel</span>
          <span className="carteira-saldo-valor">
            {saldo !== null ? fmt(saldo) : '—'}
          </span>
          <div className="carteira-saldo-badges">
            <span>Deposito diario: ate {fmt(LIMITE_INSERCAO_DIARIA)}</span>
            <span>Saque diario: ate {fmt(LIMITE_SAQUE_VALOR)}</span>
            <span>{LIMITE_SAQUE_DIARIO}x saque por dia</span>
          </div>
        </div>

        <div className="social-grid">
          <div className="social-card">
            <h2>Depositar</h2>
            <form className="social-form-col" onSubmit={handleDeposito}>
              <label>
                Valor (R$)
                <input
                  type="number"
                  min="0.01"
                  step="0.01"
                  placeholder="0,00"
                  value={valorDeposito}
                  onChange={(e) => setValorDeposito(e.target.value)}
                  required
                />
              </label>

              <label>
                Metodo de pagamento
                <select value={metodo} onChange={(e) => setMetodo(e.target.value as MetodoPagamento)}>
                  <option value="PIX">PIX — sem taxas</option>
                  <option value="CARTAO_CREDITO">
                    Cartao de Credito (+{(TAXA_CARTAO * 100).toFixed(2).replace('.', ',')}% taxa)
                  </option>
                </select>
              </label>

              {metodo === 'CARTAO_CREDITO' && valorDepositoNum > 0 && (() => {
                const { creditado, taxa } = calcularDeposito()
                return (
                  <div className="carteira-taxa-aviso">
                    <span>Taxa retida ({(TAXA_CARTAO * 100).toFixed(2).replace('.', ',')}%): -{fmt(taxa)}</span>
                    <strong>Saldo creditado na carteira: {fmt(creditado)}</strong>
                  </div>
                )
              })()}

              <button disabled={carregando}>Depositar</button>
            </form>
          </div>

          <div className="social-card">
            <h2>Sacar</h2>
            <p className="social-card-sub">Solicite transferencia para sua conta bancaria.</p>
            <form className="social-form-col" onSubmit={handleSaque}>
              <label>
                Valor (R$)
                <input
                  type="number"
                  min="0.01"
                  step="0.01"
                  max={saldo ?? undefined}
                  placeholder="0,00"
                  value={valorSaque}
                  onChange={(e) => setValorSaque(e.target.value)}
                  required
                />
              </label>

              <div className="carteira-limites-info">
                <span>Limite por saque: {fmt(LIMITE_SAQUE_VALOR)}</span>
                <span>Maximo {LIMITE_SAQUE_DIARIO}x por dia</span>
                <span className="carteira-aviso-promo">Saldo promocional nao pode ser sacado</span>
              </div>

              <button className="carteira-btn-saque" disabled={carregando}>Sacar</button>
            </form>
          </div>
        </div>

        <div className="social-card" style={{ marginTop: '1.25rem' }}>
          <div className="carteira-extrato-topo">
            <div>
              <h2>Extrato</h2>
              <p className="social-card-sub">Historico de entradas e saidas.</p>
            </div>
            <div className="carteira-extrato-filtro">
              <input
                type="date"
                value={filtroData}
                onChange={(e) => setFiltroData(e.target.value)}
                title="Filtrar por dia"
              />
              {filtroData && (
                <button type="button" className="social-btn-sec" onClick={() => setFiltroData('')}>
                  Ver todos
                </button>
              )}
            </div>
          </div>

          {filtroData && (
            <p className="carteira-extrato-resumo">
              {extratoFiltrado.length} {extratoFiltrado.length === 1 ? 'movimentacao' : 'movimentacoes'} em{' '}
              {new Date(filtroData + 'T12:00:00').toLocaleDateString('pt-BR')}
            </p>
          )}

          {extratoFiltrado.length > 0 ? (
            <div className="carteira-extrato-lista">
              {extratoFiltrado.map((t) => (
                <div key={t.id} className="carteira-extrato-item">
                  <div>
                    <p className="carteira-extrato-desc">{t.descricao}</p>
                    <p className="carteira-extrato-data">
                      {new Date(t.dataHora).toLocaleString('pt-BR')}
                    </p>
                  </div>
                  <span className={`carteira-extrato-valor ${t.direcao === 'ENTRADA' ? 'entrada' : 'saida'}`}>
                    {t.direcao === 'ENTRADA' ? '+' : '-'}{fmt(t.valor)}
                  </span>
                </div>
              ))}
            </div>
          ) : (
            <div className="carteira-extrato-vazio">
              <span>
                {filtroData ? 'Nenhuma movimentacao neste dia.' : 'Nenhuma movimentacao registrada.'}
              </span>
            </div>
          )}
        </div>
      </main>
    </div>
  )
}
