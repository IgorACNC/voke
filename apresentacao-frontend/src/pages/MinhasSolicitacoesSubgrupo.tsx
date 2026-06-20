import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import {
  listarMinhasSolicitacoes, type SolicitacaoSubgrupo,
} from '../services/solicitacaoSubgrupoService'
import './Subgrupo.css'

export default function MinhasSolicitacoesSubgrupo() {
  const { usuario, sair } = useAuth()
  const navigate = useNavigate()
  const [solicitacoes, setSolicitacoes] = useState<SolicitacaoSubgrupo[]>([])
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState('')

  useEffect(() => { carregar() }, [])

  async function carregar() {
    setCarregando(true)
    try {
      setSolicitacoes(await listarMinhasSolicitacoes())
    } catch {
      setErro('Erro ao carregar suas solicitações.')
    } finally {
      setCarregando(false)
    }
  }

  function handleSair() { sair(); navigate('/login') }

  function tagClasse(status: string) {
    if (status === 'APROVADA') return 'sub-tag--tipo-aberto'
    if (status === 'REJEITADA') return 'sub-tag--tipo-fechado'
    return 'sub-tag--pendente'
  }

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

      <main className="sub-main" style={{ maxWidth: 720 }}>
        <button className="sub-voltar" onClick={() => navigate('/dashboard')}>← Voltar</button>

        <div className="sub-topo">
          <div>
            <h1 className="sub-titulo">Minhas Solicitações</h1>
            <p className="sub-sub">Histórico das solicitações de entrada em subgrupos fechados</p>
          </div>
        </div>

        {erro && <div className="sub-erro">{erro}</div>}
        {carregando && <p className="sub-info">Carregando...</p>}
        {!carregando && solicitacoes.length === 0 && (
          <p className="sub-info">Você ainda não enviou nenhuma solicitação.</p>
        )}

        {solicitacoes.map((s) => (
          <div key={s.id} className="sub-solicitacao">
            <div className="sub-solicitacao-topo">
              <span className="sub-solicitacao-id">🔒 Subgrupo {s.subgrupoId.substring(0, 8)}…</span>
              <span className={`sub-tag ${tagClasse(s.status)}`}>{s.status}</span>
            </div>
            <div className="sub-solicitacao-data">
              Enviada em {new Date(s.dataSolicitacao).toLocaleString('pt-BR')}
              {s.dataDecisao && ` · Decidida em ${new Date(s.dataDecisao).toLocaleString('pt-BR')}`}
            </div>
            {s.mensagem && <p className="sub-solicitacao-msg">"{s.mensagem}"</p>}
            <div className="sub-solicitacao-acoes">
              <button className="sub-btn-secundario"
                onClick={() => navigate(`/subgrupos/${s.subgrupoId}`)}>
                Ver subgrupo
              </button>
            </div>
          </div>
        ))}
      </main>
    </div>
  )
}
