import { useState } from 'react'
import { useNavigate, useParams, useSearchParams } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { criarSubgrupo, type CategoriaSubgrupo, type TipoSubgrupo } from '../services/subgrupoService'
import './Subgrupo.css'

const CATEGORIAS: { value: CategoriaSubgrupo; label: string; emoji: string }[] = [
  { value: 'CARONA', label: 'Carona', emoji: '🚗' },
  { value: 'INTERESSE', label: 'Interesse', emoji: '🎯' },
  { value: 'SOCIAL', label: 'Social', emoji: '👥' },
  { value: 'OPERACIONAL', label: 'Operacional', emoji: '🛠️' },
  { value: 'OUTRO', label: 'Outro', emoji: '📌' },
]

export default function CriarSubgrupo() {
  const { eventoId } = useParams<{ eventoId: string }>()
  const [searchParams] = useSearchParams()
  const grupoEventoId = searchParams.get('grupoId') || ''
  const { usuario, sair } = useAuth()
  const navigate = useNavigate()

  const [nome, setNome] = useState('')
  const [descricao, setDescricao] = useState('')
  const [regras, setRegras] = useState('')
  const [categoria, setCategoria] = useState<CategoriaSubgrupo>('SOCIAL')
  const [tipo, setTipo] = useState<TipoSubgrupo>('ABERTO')
  const [limite, setLimite] = useState(0)
  const [carregando, setCarregando] = useState(false)
  const [erro, setErro] = useState('')

  async function handleCriar(e: React.FormEvent) {
    e.preventDefault()
    setErro('')
    if (!nome.trim()) { setErro('Informe o nome do subgrupo.'); return }
    if (!grupoEventoId) { setErro('Grupo de evento não identificado.'); return }
    setCarregando(true)
    try {
      await criarSubgrupo({
        grupoEventoId, nome: nome.trim(), descricao: descricao.trim(),
        regras: regras.trim(), categoria, tipo, limiteMembros: limite,
      })
      navigate(`/eventos/${eventoId}/grupo/subgrupos`)
    } catch (err: unknown) {
      const msg = (err as { response?: { data?: { mensagem?: string } } })?.response?.data?.mensagem
      setErro(msg || 'Erro ao criar subgrupo.')
    } finally {
      setCarregando(false)
    }
  }

  function handleSair() { sair(); navigate('/login') }

  return (
    <div className="sub-bg">
      <header className="sub-header">
        <span className="sub-logo" onClick={() => navigate('/dashboard')}>Voke</span>
        <div className="sub-header-right">
          <span className="sub-papel">{usuario?.papel}</span>
          <span className="sub-nome">{usuario?.nome}</span>
          <button className="sub-sair" onClick={handleSair}>Sair</button>
        </div>
      </header>

      <main className="sub-main" style={{ maxWidth: 580 }}>
        <button className="sub-voltar" onClick={() => navigate(`/eventos/${eventoId}/grupo/subgrupos`)}>
          ← Voltar
        </button>

        <form className="sub-form" onSubmit={handleCriar}>
          <h1 className="sub-titulo">Criar Subgrupo</h1>

          {erro && <div className="sub-erro">{erro}</div>}

          <label className="sub-label">Nome do Subgrupo *</label>
          <input className="sub-input" value={nome}
            onChange={(e) => setNome(e.target.value)}
            placeholder="ex: Carona Recife — Zona Sul" required />

          <label className="sub-label">Descrição</label>
          <input className="sub-input" value={descricao}
            onChange={(e) => setDescricao(e.target.value)}
            placeholder="Curta descrição visível na lista" />

          <label className="sub-label">Categoria</label>
          <div className="sub-cats">
            {CATEGORIAS.map(c => (
              <button key={c.value} type="button"
                className={`sub-cat ${categoria === c.value ? 'sub-cat--ativa' : ''}`}
                onClick={() => setCategoria(c.value)}>
                <span>{c.emoji}</span> {c.label}
              </button>
            ))}
          </div>

          <label className="sub-label">Tipo</label>
          <div className="sub-tipo">
            <label className={`sub-tipo-opt ${tipo === 'ABERTO' ? 'sub-tipo-opt--ativa' : ''}`}>
              <input type="radio" checked={tipo === 'ABERTO'} onChange={() => setTipo('ABERTO')} />
              <div>
                <strong>Aberto</strong>
                <p>Qualquer membro do grupo principal pode entrar livremente.</p>
              </div>
            </label>
            <label className={`sub-tipo-opt ${tipo === 'FECHADO' ? 'sub-tipo-opt--ativa' : ''}`}>
              <input type="radio" checked={tipo === 'FECHADO'} onChange={() => setTipo('FECHADO')} />
              <div>
                <strong>Fechado</strong>
                <p>Entrada por solicitação aprovada pelo organizador ou moderador.</p>
              </div>
            </label>
          </div>

          <label className="sub-label">Limite de Membros (0 = ilimitado)</label>
          <input className="sub-input" type="number" min={0} value={limite}
            onChange={(e) => setLimite(Number(e.target.value) || 0)} />

          <label className="sub-label">Regras do Subgrupo</label>
          <textarea className="sub-textarea" rows={4} value={regras}
            onChange={(e) => setRegras(e.target.value)}
            placeholder="Regras específicas do subgrupo..." />

          <button className="sub-btn-primario" type="submit" disabled={carregando}>
            {carregando ? 'Criando...' : 'Criar Subgrupo'}
          </button>
        </form>
      </main>
    </div>
  )
}
