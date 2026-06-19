import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import {
  listarFaq, criarPergunta, editarPergunta, excluirPergunta, reordenarFaq,
  type PerguntaFaq,
} from '../services/perguntaFaqService'
import FaqFormModal from '../components/FaqFormModal'
import './FaqEvento.css'

const LIMITE = 20

type ModoModal = { tipo: 'criar' } | { tipo: 'editar', pergunta: PerguntaFaq } | null

export default function FaqEvento() {
  const { eventoId } = useParams<{ eventoId: string }>()
  const navigate = useNavigate()
  const { usuario, sair } = useAuth()

  const [perguntas, setPerguntas] = useState<PerguntaFaq[]>([])
  const [expandido, setExpandido] = useState<Record<string, boolean>>({})
  const [modal, setModal] = useState<ModoModal>(null)
  const [confirmarExcluirId, setConfirmarExcluirId] = useState<string | null>(null)
  const [carregando, setCarregando] = useState(false)
  const [erro, setErro] = useState('')
  const [erroModal, setErroModal] = useState('')

  useEffect(() => { if (eventoId) carregar() }, [eventoId])

  async function carregar() {
    if (!eventoId) return
    try {
      const lista = await listarFaq(eventoId)
      setPerguntas(lista)
    } catch {
      setErro('Erro ao carregar perguntas.')
    }
  }

  function handleSair() { sair(); navigate('/login') }

  function alternar(id: string) {
    setExpandido((prev) => ({ ...prev, [id]: !prev[id] }))
  }

  async function handleSalvar(pergunta: string, resposta: string) {
    if (!eventoId || !modal) return
    setCarregando(true)
    setErroModal('')
    try {
      if (modal.tipo === 'criar') {
        await criarPergunta(eventoId, { pergunta, resposta })
      } else {
        await editarPergunta(eventoId, modal.pergunta.id, { pergunta, resposta })
      }
      setModal(null)
      await carregar()
    } catch (err: unknown) {
      const msg = (err as { response?: { data?: { mensagem?: string } } })?.response?.data?.mensagem
      setErroModal(msg || 'Erro ao salvar pergunta.')
    } finally {
      setCarregando(false)
    }
  }

  async function handleExcluir(id: string) {
    if (!eventoId) return
    try {
      await excluirPergunta(eventoId, id)
      setConfirmarExcluirId(null)
      await carregar()
    } catch (err: unknown) {
      const msg = (err as { response?: { data?: { mensagem?: string } } })?.response?.data?.mensagem
      setErro(msg || 'Erro ao excluir pergunta.')
    }
  }

  async function mover(indice: number, direcao: -1 | 1) {
    const destino = indice + direcao
    if (destino < 0 || destino >= perguntas.length || !eventoId) return
    const novaLista = [...perguntas]
    const tmp = novaLista[indice]
    novaLista[indice] = novaLista[destino]
    novaLista[destino] = tmp
    const idsOrdenados = novaLista.map((p) => p.id)
    setPerguntas(novaLista.map((p, i) => ({ ...p, posicao: i + 1 })))
    try {
      await reordenarFaq(eventoId, idsOrdenados)
    } catch (err: unknown) {
      const msg = (err as { response?: { data?: { mensagem?: string } } })?.response?.data?.mensagem
      setErro(msg || 'Erro ao reordenar.')
      await carregar()
    }
  }

  const podeCriar = perguntas.length < LIMITE

  return (
    <div className="faq-bg">
      <header className="faq-header">
        <span className="faq-logo" onClick={() => navigate('/dashboard')}>Voke</span>
        <div className="faq-header-right">
          <span>{usuario?.papel}</span>
          <span>{usuario?.nome}</span>
          <button className="faq-sair" onClick={handleSair}>Sair</button>
        </div>
      </header>

      <main className="faq-main">
        <div className="faq-topo">
          <div>
            <h1 className="faq-titulo">Perguntas Frequentes</h1>
            <p className="faq-sub">Gerencie as perguntas exibidas na página do evento</p>
          </div>
          <div style={{ display: 'flex', gap: '0.75rem', alignItems: 'center' }}>
            <span className="faq-contador">{perguntas.length}/{LIMITE} perguntas cadastradas</span>
            <button
              className="faq-btn-novo"
              onClick={() => { setErroModal(''); setModal({ tipo: 'criar' }) }}
              disabled={!podeCriar}
              title={!podeCriar ? `Limite de ${LIMITE} perguntas atingido` : undefined}
            >
              + NOVA PERGUNTA
            </button>
          </div>
        </div>

        {erro && <div className="faq-erro">{erro}</div>}

        {perguntas.length === 0 && (
          <p className="faq-vazio">Nenhuma pergunta cadastrada ainda.</p>
        )}

        <div className="faq-lista">
          {perguntas.map((p, idx) => (
            <div key={p.id} className="faq-card">
              <div className="faq-card-topo" onClick={() => alternar(p.id)}>
                <span className="faq-pos">{p.posicao}</span>
                <span className="faq-pergunta-texto">{p.pergunta}</span>
                <span className={`faq-chevron ${expandido[p.id] ? 'aberto' : ''}`}>▼</span>
              </div>
              {expandido[p.id] && <div className="faq-resposta">{p.resposta}</div>}
              <div className="faq-acoes">
                <button
                  className="faq-btn-acao"
                  onClick={() => mover(idx, -1)}
                  disabled={idx === 0}
                  title="Mover para cima"
                >↑</button>
                <button
                  className="faq-btn-acao"
                  onClick={() => mover(idx, 1)}
                  disabled={idx === perguntas.length - 1}
                  title="Mover para baixo"
                >↓</button>
                <button
                  className="faq-btn-acao"
                  onClick={() => { setErroModal(''); setModal({ tipo: 'editar', pergunta: p }) }}
                >EDITAR</button>
                <button
                  className="faq-btn-acao faq-btn-excluir"
                  onClick={() => setConfirmarExcluirId(p.id)}
                >EXCLUIR</button>
              </div>
            </div>
          ))}
        </div>
      </main>

      {modal && (
        <FaqFormModal
          titulo={modal.tipo === 'criar' ? 'Nova Pergunta' : 'Editar Pergunta'}
          perguntaInicial={modal.tipo === 'editar' ? modal.pergunta.pergunta : ''}
          respostaInicial={modal.tipo === 'editar' ? modal.pergunta.resposta : ''}
          carregando={carregando}
          erro={erroModal}
          onCancelar={() => setModal(null)}
          onSubmit={handleSalvar}
        />
      )}

      {confirmarExcluirId && (
        <div className="faq-modal-bg" onClick={() => setConfirmarExcluirId(null)}>
          <div className="faq-modal" onClick={(e) => e.stopPropagation()}>
            <h2>Excluir pergunta?</h2>
            <p>As perguntas posteriores serão reordenadas automaticamente.</p>
            <div className="faq-modal-acoes">
              <button className="faq-btn-acao" onClick={() => setConfirmarExcluirId(null)}>Voltar</button>
              <button
                className="faq-btn-acao faq-btn-excluir"
                onClick={() => handleExcluir(confirmarExcluirId)}
              >Confirmar exclusão</button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
