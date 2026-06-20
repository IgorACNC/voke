import { useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { criarGrupo } from '../services/grupoEventoService'
import './CriarGrupo.css'

export default function CriarGrupo() {
  const { eventoId } = useParams<{ eventoId: string }>()
  const { usuario, sair } = useAuth()
  const navigate = useNavigate()

  const [nome, setNome] = useState('')
  const [regras, setRegras] = useState('')
  const [carregando, setCarregando] = useState(false)
  const [erro, setErro] = useState('')

  async function handleCriar(e: React.FormEvent) {
    e.preventDefault()
    setErro('')
    if (!nome.trim()) { setErro('Informe o nome do grupo.'); return }
    setCarregando(true)
    try {
      await criarGrupo(eventoId!, nome.trim(), regras.trim())
      navigate(`/eventos/${eventoId}/grupo`)
    } catch (err: unknown) {
      const msg = (err as { response?: { data?: { mensagem?: string } } })?.response?.data?.mensagem
      setErro(msg || 'Erro ao criar grupo.')
    } finally {
      setCarregando(false)
    }
  }

  function handleSair() { sair(); navigate('/login') }

  return (
    <div className="cg-bg">
      <header className="cg-header">
        <span className="cg-logo" onClick={() => navigate('/meus-eventos')}>Voke</span>
        <div className="cg-header-right">
          <span className="cg-papel">{usuario?.papel}</span>
          <span className="cg-nome">{usuario?.nome}</span>
          <button className="cg-sair" onClick={handleSair}>Sair</button>
        </div>
      </header>

      <main className="cg-main">
        <button className="cg-voltar" onClick={() => navigate(`/eventos/${eventoId}/grupo`)}>
          ← Voltar
        </button>

        <form className="cg-form" onSubmit={handleCriar}>
          <h1 className="cg-titulo">Criar Grupo do Evento</h1>

          {erro && <div className="cg-erro">{erro}</div>}

          <label className="cg-label">Nome do Grupo *</label>
          <input className="cg-input" value={nome}
            onChange={(e) => setNome(e.target.value)}
            placeholder="ex: Tech Conference 2026 — Grupo"
            required />

          <label className="cg-label">Regras de Convivência</label>
          <textarea className="cg-textarea" rows={4} value={regras}
            onChange={(e) => setRegras(e.target.value)}
            placeholder="Descreva as regras para os participantes do grupo..." />

          <p className="cg-info-aviso">
            O grupo ficará restrito a participantes com inscrição confirmada e maiores de 18 anos.
          </p>

          <button className="cg-btn-primario" type="submit" disabled={carregando}>
            {carregando ? 'Criando...' : 'Criar Grupo'}
          </button>
        </form>
      </main>
    </div>
  )
}
