import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { listarCupons, criarCupom, excluirCupom, type Cupom } from '../services/cupomService'
import './Social.css'

export default function Cupons() {
  const navigate = useNavigate()
  const { usuario } = useAuth()
  const [cupons, setCupons] = useState<Cupom[]>([])
  const [carregando, setCarregando] = useState(false)
  const [erro, setErro] = useState('')
  const [mensagem, setMensagem] = useState('')

  const [codigo, setCodigo] = useState('')
  const [desconto, setDesconto] = useState('')
  const [percentual, setPercentual] = useState(true)

  useEffect(() => {
    if (!usuario) return
    carregar()
  }, [usuario?.id])

  async function carregar() {
    setErro('')
    setCarregando(true)
    try {
      const dados = await listarCupons()
      setCupons(dados)
    } catch {
      setErro('Nao foi possivel carregar os cupons.')
    } finally {
      setCarregando(false)
    }
  }

  async function handleCriar(e: React.FormEvent) {
    e.preventDefault()
    setErro('')
    try {
      await criarCupom({ codigo, percentual, valor: parseFloat(desconto || '0') })
      setMensagem('Cupom criado com sucesso.')
      setCodigo('')
      setDesconto('')
      await carregar()
    } catch (e: unknown) {
      setErro((e as any)?.response?.data?.mensagem ?? 'Erro ao criar cupom.')
    }
  }

  async function handleExcluir(id: string) {
    if (!confirm('Deseja excluir este cupom?')) return
    try {
      await excluirCupom(id)
      setMensagem('Cupom excluido.')
      await carregar()
    } catch {
      setErro('Nao foi possivel excluir o cupom.')
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
          <h1>Cupons</h1>
          <p>Gerencie cupons promocionais para seus eventos.</p>
        </section>

        {mensagem && <p className="social-msg-sucesso">{mensagem}</p>}
        {erro && <p className="social-msg-erro">{erro}</p>}

        <div className="social-card">
          <h2>Criar Cupom</h2>
          <form className="social-form-col" onSubmit={handleCriar}>
            <label>
              Codigo
              <input value={codigo} onChange={(e) => setCodigo(e.target.value)} required />
            </label>
            <label>
              Tipo
              <select value={percentual ? 'percentual' : 'fixo'} onChange={(e) => setPercentual(e.target.value === 'percentual')}>
                <option value="percentual">Percentual</option>
                <option value="fixo">Valor fixo</option>
              </select>
            </label>
            <label>
              Valor
              <input type="number" step="0.01" value={desconto} onChange={(e) => setDesconto(e.target.value)} required />
            </label>
            <button>Criar</button>
          </form>
        </div>

        {carregando ? (
          <p>Carregando...</p>
        ) : (
          <div className="social-card">
            {cupons.length === 0 && <p className="social-vazio">Nenhum cupom cadastrado.</p>}
            {cupons.map((c) => (
              <div key={c.id} className="cupom-item">
                <strong>{c.codigo}</strong>
                <p>{c.percentual ? `${c.valor}%` : `R$ ${c.valor.toFixed(2)}`}</p>
                <div style={{ marginTop: 8 }}>
                  <button onClick={() => navigate(`/meus-eventos/${c.eventoId}/editar`)} disabled={c.usos > 0}>Editar</button>
                  <button className="social-btn-sec" onClick={() => handleExcluir(c.id)}>Excluir</button>
                </div>
              </div>
            ))}
          </div>
        )}
      </main>
    </div>
  )
}
