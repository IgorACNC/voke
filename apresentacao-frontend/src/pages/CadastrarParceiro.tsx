import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import {
  cadastrarParceiro,
  ATIVIDADES_LABELS,
  type AtividadeParceiro,
} from '../services/parceiroService'
import './Social.css'
import './Parceiros.css'

const TODAS_ATIVIDADES = Object.keys(ATIVIDADES_LABELS) as AtividadeParceiro[]

export default function CadastrarParceiro() {
  const navigate = useNavigate()
  const { usuario } = useAuth()

  const [participanteId, setParticipanteId] = useState('')
  const [atividades, setAtividades] = useState<Set<AtividadeParceiro>>(new Set())
  const [carregando, setCarregando] = useState(false)
  const [erro, setErro] = useState('')
  const [mensagem, setMensagem] = useState('')

  function toggleAtividade(a: AtividadeParceiro) {
    setAtividades((prev) => {
      const next = new Set(prev)
      next.has(a) ? next.delete(a) : next.add(a)
      return next
    })
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    setErro(''); setMensagem('')
    if (!participanteId.trim()) { setErro('Informe o ID do participante.'); return }
    if (atividades.size === 0) { setErro('Selecione ao menos uma atividade.'); return }
    setCarregando(true)
    try {
      await cadastrarParceiro({
        participanteId: participanteId.trim(),
        organizadorId: usuario!.id,
        atividades: [...atividades],
      })
      setMensagem('Parceiro cadastrado com sucesso!')
      setParticipanteId('')
      setAtividades(new Set())
    } catch (e: any) {
      setErro(e?.response?.data?.mensagem ?? 'Erro ao cadastrar parceiro.')
    } finally {
      setCarregando(false)
    }
  }

  if (!usuario) return null

  return (
    <div className="social-bg">
      <header className="social-header">
        <button className="social-voltar" onClick={() => navigate('/parceiros')}>Voltar</button>
        <span className="social-logo">Voke</span>
        <div style={{ width: 90 }} />
      </header>

      <main className="social-main social-main-curto">
        <section className="social-title">
          <h1>Cadastrar Parceiro</h1>
          <p>Cadastre um participante como parceiro do seu evento.</p>
        </section>

        <div className="parceiro-aviso">
          ⚠️ O participante precisa ter participado de <strong>pelo menos 5 eventos</strong> seus para ser elegível como parceiro.
        </div>

        {mensagem && <p className="social-msg-sucesso">{mensagem}</p>}
        {erro && <p className="social-msg-erro">{erro}</p>}

        <div className="social-card">
          <h2>Dados do parceiro</h2>
          <form className="social-form-col" onSubmit={handleSubmit}>
            <label>
              ID do Participante
              <input
                value={participanteId}
                onChange={(e) => setParticipanteId(e.target.value)}
                placeholder="UUID do participante"
                required
              />
            </label>

            <label>Atividades</label>
            <div className="parceiro-check-group">
              {TODAS_ATIVIDADES.map((a) => (
                <label key={a}>
                  <input
                    type="checkbox"
                    checked={atividades.has(a)}
                    onChange={() => toggleAtividade(a)}
                  />
                  {ATIVIDADES_LABELS[a]}
                </label>
              ))}
            </div>

            <button disabled={carregando}>
              {carregando ? 'Cadastrando...' : 'Cadastrar Parceiro'}
            </button>
          </form>
        </div>
      </main>
    </div>
  )
}
