import { useEffect, useMemo, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import {
  listarRecompensasAtivas,
  resgatarRecompensa,
  type Recompensa,
  type CategoriaRecompensa,
} from '../services/recompensaService'
import { consultarSaldoPontos } from '../services/pontosService'
import './Social.css'
import './CatalogoRecompensas.css'

type Filtro = 'TODOS' | CategoriaRecompensa

const CATEGORIAS: { valor: Filtro; label: string }[] = [
  { valor: 'TODOS', label: 'Todos' },
  { valor: 'CUPOM', label: 'Cupom' },
  { valor: 'CREDITO_CARTEIRA', label: 'Crédito' },
]

const CATEGORIA_LABEL: Record<CategoriaRecompensa, string> = {
  DESCONTO: 'DESCONTO',
  BRINDE: 'BRINDE',
  BENEFICIO: 'BENEFÍCIO',
  CREDITO_CARTEIRA: 'CRÉDITO',
  CUPOM: 'CUPOM',
}

export default function CatalogoRecompensas() {
  const navigate = useNavigate()
  const { usuario } = useAuth()

  const [recompensas, setRecompensas] = useState<Recompensa[]>([])
  const [saldo, setSaldo] = useState<number>(0)
  const [filtro, setFiltro] = useState<Filtro>('TODOS')
  const [carregando, setCarregando] = useState(false)
  const [resgatando, setResgatando] = useState<string | null>(null)
  const [erro, setErro] = useState('')
  const [mensagem, setMensagem] = useState('')

  useEffect(() => {
    if (!usuario) return
    carregar()
  }, [usuario?.id])

  async function carregar() {
    setCarregando(true)
    setErro('')
    try {
      const [r, s] = await Promise.all([
        listarRecompensasAtivas(),
        consultarSaldoPontos(usuario!.id).catch(() => 0),
      ])
      setRecompensas(r)
      setSaldo(s)
    } catch {
      setErro('Não foi possível carregar as recompensas.')
    } finally {
      setCarregando(false)
    }
  }

  async function handleResgatar(r: Recompensa) {
    if (saldo < r.custoEmPontos) return
    if (!confirm(`Resgatar "${r.nome}" por ${r.custoEmPontos} pontos?`)) return
    setResgatando(r.id)
    setErro('')
    setMensagem('')
    try {
      const resp = await resgatarRecompensa(r.id, usuario!.id)
      let detalhe = ''
      if (r.categoria === 'CREDITO_CARTEIRA' && r.valor) {
        detalhe = ` R$ ${r.valor.toFixed(2)} creditados na sua carteira virtual.`
      } else if (r.categoria === 'CUPOM' && resp.codigoCupom) {
        const escopo = r.global ? 'em qualquer evento do sistema' : 'nos eventos deste organizador'
        const valorTxt = r.valor ? ` de R$ ${r.valor.toFixed(2)}` : ''
        detalhe = ` Seu código de cupom${valorTxt} é: ${resp.codigoCupom} — digite este código no carrinho para usar ${escopo}.`
        try { await navigator.clipboard.writeText(resp.codigoCupom) } catch {}
        alert(`Cupom resgatado!\n\nCódigo: ${resp.codigoCupom}\n\n(já copiado para a área de transferência)\n\nUse este código exato no carrinho para receber o desconto.`)
      } else {
        detalhe = ' Em breve entraremos em contato com instruções.'
      }
      setMensagem(`Você resgatou "${r.nome}"!${detalhe}`)
      await carregar()
    } catch (e: any) {
      setErro(e?.response?.data?.mensagem ?? 'Erro ao resgatar.')
    } finally {
      setResgatando(null)
    }
  }

  const visíveis = useMemo(
    () => filtro === 'TODOS' ? recompensas : recompensas.filter((r) => r.categoria === filtro),
    [recompensas, filtro],
  )

  if (!usuario) return null

  return (
    <div className="social-bg">
      <header className="social-header">
        <button className="social-voltar" onClick={() => navigate('/carteira-pontos')}>Voltar</button>
        <span className="social-logo">Voke</span>
        <div style={{ width: 90 }} />
      </header>

      <main className="social-main">
        <section className="social-title">
          <h1>Catálogo de Recompensas</h1>
          <p>Troque seus pontos por descontos, brindes e benefícios.</p>
        </section>

        <div className="catalogo-saldo">
          <span className="catalogo-saldo-label">SEU SALDO</span>
          <strong className="catalogo-saldo-valor">{saldo} pts</strong>
        </div>

        {mensagem && <p className="social-msg-sucesso">{mensagem}</p>}
        {erro && <p className="social-msg-erro">{erro}</p>}

        {/* Filtros */}
        <div className="catalogo-filtros">
          {CATEGORIAS.map((c) => (
            <button
              key={c.valor}
              className={`catalogo-filtro ${filtro === c.valor ? 'ativo' : ''}`}
              onClick={() => setFiltro(c.valor)}
            >
              {c.label}
            </button>
          ))}
        </div>

        {carregando ? (
          <p style={{ color: '#9ca3af', textAlign: 'center', padding: '2rem' }}>Carregando catálogo...</p>
        ) : visíveis.length === 0 ? (
          <p className="social-vazio">Nenhuma recompensa disponível nesta categoria.</p>
        ) : (
          <div className="catalogo-grid">
            {visíveis.map((r) => {
              const podeResgatar = saldo >= r.custoEmPontos && r.estoqueDisponivel > 0
              const faltam = r.custoEmPontos - saldo
              const esgotada = r.estoqueDisponivel <= 0
              return (
                <div key={r.id} className="catalogo-card">
                  <div className="catalogo-card-topo">
                    <span className={`catalogo-categoria categoria-${r.categoria.toLowerCase()}`}>
                      {CATEGORIA_LABEL[r.categoria]}
                    </span>
                    <span className="catalogo-custo">{r.custoEmPontos} pts</span>
                  </div>
                  <h3 className="catalogo-nome">{r.nome}</h3>
                  <p className="catalogo-descricao">{r.descricao}</p>
                  {r.estoqueDisponivel > 0 && r.estoqueDisponivel < 10 && (
                    <p className="catalogo-estoque">Restam {r.estoqueDisponivel} unidades</p>
                  )}
                  <button
                    className={`catalogo-btn ${podeResgatar ? 'pode' : 'faltam'}`}
                    onClick={() => handleResgatar(r)}
                    disabled={!podeResgatar || resgatando === r.id}
                  >
                    {resgatando === r.id
                      ? 'Resgatando...'
                      : esgotada
                        ? 'ESGOTADA'
                        : podeResgatar
                          ? `RESGATAR (${r.custoEmPontos} pts)`
                          : `FALTAM ${faltam} pts`}
                  </button>
                </div>
              )
            })}
          </div>
        )}
      </main>
    </div>
  )
}
