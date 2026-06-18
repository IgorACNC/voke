import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import {
  listarParceiros,
  removerParceiro,
  adicionarAtividade,
  removerAtividade as removerAtiv,
  ATIVIDADES_LABELS,
  type Parceiro,
  type AtividadeParceiro,
} from '../services/parceiroService'
import './Social.css'
import './Parceiros.css'

const TODAS_ATIVIDADES = Object.keys(ATIVIDADES_LABELS) as AtividadeParceiro[]

export default function ListaParceiros() {
  const navigate = useNavigate()
  const { usuario } = useAuth()

  const [parceiros, setParceiros] = useState<Parceiro[]>([])
  const [carregando, setCarregando] = useState(false)
  const [erro, setErro] = useState('')
  const [mensagem, setMensagem] = useState('')
  const [editandoId, setEditandoId] = useState<string | null>(null)

  useEffect(() => { if (usuario) carregar() }, [usuario?.id])

  async function carregar() {
    setCarregando(true); setErro('')
    try {
      setParceiros(await listarParceiros(usuario!.id))
    } catch {
      setErro('Não foi possível carregar parceiros.')
    } finally {
      setCarregando(false)
    }
  }

  async function handleInativar(id: string) {
    if (!confirm('Tem certeza que deseja inativar este parceiro?')) return
    try {
      await removerParceiro(id)
      setMensagem('Parceiro inativado.')
      await carregar()
    } catch (e: any) {
      setErro(e?.response?.data?.mensagem ?? 'Erro ao inativar.')
    }
  }

  async function handleAddAtividade(parceiroId: string, atividade: AtividadeParceiro) {
    try {
      await adicionarAtividade(parceiroId, atividade)
      await carregar()
    } catch (e: any) {
      setErro(e?.response?.data?.mensagem ?? 'Erro ao adicionar atividade.')
    }
  }

  async function handleRemoveAtividade(parceiroId: string, atividade: AtividadeParceiro) {
    try {
      await removerAtiv(parceiroId, atividade)
      await carregar()
    } catch (e: any) {
      setErro(e?.response?.data?.mensagem ?? 'Erro ao remover atividade.')
    }
  }

  if (!usuario) return null

  return (
    <div className="social-bg">
      <header className="social-header">
        <button className="social-voltar" onClick={() => navigate('/dashboard')}>Voltar</button>
        <span className="social-logo">Voke</span>
        <button className="social-voltar" onClick={() => navigate('/parceiros/novo')}>+ Novo</button>
      </header>

      <main className="social-main">
        <section className="social-title">
          <h1>Parceiros</h1>
          <p>Gerencie seus parceiros — adicione ou remova atividades e inative parceiros.</p>
        </section>

        {mensagem && <p className="social-msg-sucesso">{mensagem}</p>}
        {erro && <p className="social-msg-erro">{erro}</p>}

        {carregando ? (
          <p style={{ color: '#9ca3af', textAlign: 'center', padding: '2rem' }}>Carregando...</p>
        ) : parceiros.length === 0 ? (
          <div className="social-card">
            <p className="social-vazio">Nenhum parceiro cadastrado.</p>
          </div>
        ) : (
          <div className="social-card">
            <h2>Seus parceiros ({parceiros.length})</h2>
            <div className="social-lista social-lista-grande">
              {parceiros.map((p) => (
                <div key={p.id} className="parceiro-item">
                  <div className="parceiro-item-info">
                    <strong>{p.nomeParticipante || 'Participante'}</strong>
                    <span>ID: {p.participanteId.slice(0, 8)}...</span>
                    <div className="parceiro-atividades">
                      {p.atividades.map((a) => (
                        <span key={a} className="parceiro-atividade">{ATIVIDADES_LABELS[a]}</span>
                      ))}
                    </div>

                    {editandoId === p.id && (
                      <div className="parceiro-editar-ativ">
                        {TODAS_ATIVIDADES.filter((a) => !p.atividades.includes(a)).map((a) => (
                          <button key={a} onClick={() => handleAddAtividade(p.id, a)}>
                            + {ATIVIDADES_LABELS[a]}
                          </button>
                        ))}
                        {p.atividades.map((a) => (
                          <button key={a} className="social-btn-perigo" onClick={() => handleRemoveAtividade(p.id, a)}>
                            − {ATIVIDADES_LABELS[a]}
                          </button>
                        ))}
                      </div>
                    )}
                  </div>

                  <div className="social-acoes" style={{ flexDirection: 'column' }}>
                    <button onClick={() => setEditandoId(editandoId === p.id ? null : p.id)}>
                      {editandoId === p.id ? 'Fechar' : 'Editar'}
                    </button>
                    <button className="social-btn-perigo" onClick={() => handleInativar(p.id)}>
                      Inativar
                    </button>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}
      </main>
    </div>
  )
}
