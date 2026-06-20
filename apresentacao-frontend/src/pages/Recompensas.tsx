import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import {
  listarRecompensasOrganizador,
  listarTodasRecompensas,
  criarRecompensa,
  criarRecompensaGlobal,
  inativarRecompensa,
  removerRecompensa,
  type Recompensa,
  type CategoriaRecompensa,
} from '../services/recompensaService'
import './Social.css'

const CATEGORIAS_ORGANIZADOR: { valor: CategoriaRecompensa; label: string }[] = [
  { valor: 'CUPOM', label: 'Cupom de desconto' },
]

const CATEGORIAS_ADMIN: { valor: CategoriaRecompensa; label: string }[] = [
  { valor: 'CUPOM', label: 'Cupom de desconto (todos os eventos)' },
  { valor: 'CREDITO_CARTEIRA', label: 'Crédito na carteira' },
]

export default function Recompensas() {
  const navigate = useNavigate()
  const { usuario } = useAuth()
  const isAdmin = usuario?.papel === 'ADMIN'

  const [recompensas, setRecompensas] = useState<Recompensa[]>([])
  const [carregando, setCarregando] = useState(false)
  const [erro, setErro] = useState('')
  const [mensagem, setMensagem] = useState('')

  const [nome, setNome] = useState('')
  const [descricao, setDescricao] = useState('')
  const [custo, setCusto] = useState('')
  const [estoque, setEstoque] = useState('')
  const [categoria, setCategoria] = useState<CategoriaRecompensa>('CUPOM')
  const [valor, setValor] = useState('')

  const categoriasDisponiveis = isAdmin ? CATEGORIAS_ADMIN : CATEGORIAS_ORGANIZADOR

  useEffect(() => {
    if (!usuario) return
    carregar()
  }, [usuario?.id])

  async function carregar() {
    setErro('')
    setCarregando(true)
    try {
      const dados = isAdmin
        ? await listarTodasRecompensas()
        : await listarRecompensasOrganizador(usuario!.id)
      setRecompensas(dados)
    } catch {
      setErro('Não foi possível carregar as recompensas.')
    } finally {
      setCarregando(false)
    }
  }

  async function handleCriar(e: React.FormEvent) {
    e.preventDefault()
    setErro(''); setMensagem('')
    const custoNum = parseInt(custo || '0', 10)
    const estoqueNum = parseInt(estoque || '0', 10)
    const valorNum = parseFloat(valor || '0')
    if (!nome.trim() || custoNum <= 0 || estoqueNum <= 0) {
      setErro('Preencha nome, custo (> 0) e estoque (> 0).')
      return
    }
    if (categoria === 'CREDITO_CARTEIRA' && valorNum <= 0) {
      setErro('Crédito na carteira precisa de um valor em R$ maior que zero.')
      return
    }
    if (categoria === 'CUPOM' && valorNum <= 0) {
      setErro('Cupom precisa de um valor de desconto em R$ maior que zero.')
      return
    }
    const valorFinal = categoria === 'CREDITO_CARTEIRA' || categoria === 'CUPOM' ? valorNum || null : null
    try {
      if (isAdmin) {
        await criarRecompensaGlobal({
          nome, descricao, custoEmPontos: custoNum, estoqueTotal: estoqueNum, categoria,
          valor: valorFinal,
        })
      } else {
        await criarRecompensa({
          nome, descricao, custoEmPontos: custoNum, estoqueTotal: estoqueNum,
          organizadorId: usuario!.id, categoria, valor: valorFinal,
        })
      }
      setMensagem('Recompensa criada!')
      setNome(''); setDescricao(''); setCusto(''); setEstoque('')
      setCategoria('CUPOM'); setValor('')
      await carregar()
    } catch (e: any) {
      setErro(e?.response?.data?.mensagem ?? 'Erro ao criar recompensa.')
    }
  }

  async function handleInativar(id: string) {
    try {
      await inativarRecompensa(id)
      await carregar()
    } catch {
      setErro('Não foi possível inativar.')
    }
  }

  async function handleRemover(id: string) {
    if (!confirm('Excluir esta recompensa?')) return
    try {
      await removerRecompensa(id)
      await carregar()
    } catch {
      setErro('Não foi possível remover.')
    }
  }

  if (!usuario) return null
  if (usuario.papel === 'PARTICIPANTE') {
    navigate('/dashboard')
    return null
  }

  return (
    <div className="social-bg">
      <header className="social-header">
        <button className="social-voltar" onClick={() => navigate(isAdmin ? '/admin' : '/dashboard')}>
          Voltar
        </button>
        <span className="social-logo">Voke</span>
        <div style={{ width: 90 }} />
      </header>

      <main className="social-main">
        <section className="social-title">
          <h1>Recompensas</h1>
          <p>
            {isAdmin
              ? 'Crie recompensas globais disponíveis para todos os eventos.'
              : 'Crie recompensas resgatáveis com pontos pelos participantes dos seus eventos.'}
          </p>
        </section>

        {mensagem && <p className="social-msg-sucesso">{mensagem}</p>}
        {erro && <p className="social-msg-erro">{erro}</p>}

        <div className="social-card">
          <h2>{isAdmin ? 'Nova recompensa global' : 'Nova recompensa'}</h2>
          <form className="social-form-col" onSubmit={handleCriar}>
            <label>
              Nome
              <input value={nome} onChange={(e) => setNome(e.target.value)}
                     placeholder="Ex: Cupom 10% OFF" required />
            </label>
            <label>
              Descrição
              <textarea value={descricao} onChange={(e) => setDescricao(e.target.value)}
                        placeholder="Detalhe a recompensa..." rows={3} />
            </label>
            <label>
              Categoria
              <select value={categoria} onChange={(e) => setCategoria(e.target.value as CategoriaRecompensa)}>
                {categoriasDisponiveis.map((c) => (
                  <option key={c.valor} value={c.valor}>{c.label}</option>
                ))}
              </select>
            </label>
            {(categoria === 'CREDITO_CARTEIRA' || categoria === 'CUPOM') && (
              <label>
                {categoria === 'CREDITO_CARTEIRA'
                  ? 'Valor a creditar (R$)'
                  : 'Valor do desconto do cupom (R$)'}
                <input type="number" step="0.01" min="0.01"
                       value={valor} onChange={(e) => setValor(e.target.value)}
                       required />
              </label>
            )}

            <label>
              Custo em pontos
              <input type="number" min="1" value={custo} onChange={(e) => setCusto(e.target.value)} required />
            </label>
            <label>
              Estoque total
              <input type="number" min="1" value={estoque} onChange={(e) => setEstoque(e.target.value)} required />
            </label>
            <button>Criar recompensa</button>
          </form>
        </div>

        {carregando ? (
          <p>Carregando...</p>
        ) : (
          <div className="social-card">
            <h2>{isAdmin ? 'Todas as recompensas' : 'Minhas recompensas'}</h2>
            {recompensas.length === 0 && <p className="social-vazio">Nenhuma recompensa cadastrada.</p>}
            {recompensas.map((r) => (
              <div key={r.id} className="cupom-item" style={{ opacity: r.ativa ? 1 : 0.55 }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <strong>{r.nome}</strong>
                  <span style={{
                    fontSize: '0.7rem',
                    padding: '2px 10px',
                    borderRadius: 12,
                    background: r.ativa ? '#dcfce7' : '#fee2e2',
                    color: r.ativa ? '#15803d' : '#b91c1c',
                    fontWeight: 700,
                  }}>
                    {r.ativa ? 'ATIVA' : 'INATIVA'}
                  </span>
                </div>
                <p style={{ margin: '0.4rem 0', color: '#4b5563', fontSize: '0.9rem' }}>{r.descricao}</p>
                <p style={{ margin: '0.2rem 0', fontSize: '0.85rem' }}>
                  <strong>{r.custoEmPontos} pts</strong> •
                  Estoque: {r.estoqueDisponivel}/{r.estoqueTotal} •
                  {r.global ? ' 🌐 Global' : ` Categoria: ${r.categoria}`}
                </p>
                <div style={{ marginTop: 8, display: 'flex', gap: 8 }}>
                  {r.ativa && (
                    <button onClick={() => handleInativar(r.id)}>Inativar</button>
                  )}
                  <button className="social-btn-sec" onClick={() => handleRemover(r.id)}>
                    Excluir
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}
      </main>
    </div>
  )
}
