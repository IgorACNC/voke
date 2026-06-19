import { useEffect, useState } from 'react'

interface Props {
  titulo: string
  perguntaInicial?: string
  respostaInicial?: string
  carregando?: boolean
  erro?: string
  onCancelar: () => void
  onSubmit: (pergunta: string, resposta: string) => void
}

export default function FaqFormModal({
  titulo, perguntaInicial = '', respostaInicial = '', carregando, erro, onCancelar, onSubmit,
}: Props) {
  const [pergunta, setPergunta] = useState(perguntaInicial)
  const [resposta, setResposta] = useState(respostaInicial)

  useEffect(() => {
    setPergunta(perguntaInicial)
    setResposta(respostaInicial)
  }, [perguntaInicial, respostaInicial])

  function handleSubmit() {
    if (!pergunta.trim() || !resposta.trim()) return
    onSubmit(pergunta.trim(), resposta.trim())
  }

  return (
    <div className="faq-modal-bg" onClick={onCancelar}>
      <div className="faq-modal" onClick={(e) => e.stopPropagation()}>
        <h2>{titulo}</h2>
        {erro && <div className="faq-erro">{erro}</div>}
        <div className="faq-campo">
          <label>Pergunta</label>
          <input
            type="text"
            value={pergunta}
            onChange={(e) => setPergunta(e.target.value)}
            placeholder="Ex.: Posso levar acompanhante?"
            maxLength={500}
          />
        </div>
        <div className="faq-campo">
          <label>Resposta</label>
          <textarea
            value={resposta}
            onChange={(e) => setResposta(e.target.value)}
            placeholder="Descreva a resposta completa..."
            maxLength={5000}
          />
        </div>
        <div className="faq-modal-acoes">
          <button className="faq-btn-acao" onClick={onCancelar} disabled={carregando}>Cancelar</button>
          <button
            className="faq-btn-novo"
            onClick={handleSubmit}
            disabled={carregando || !pergunta.trim() || !resposta.trim()}
          >
            {carregando ? 'Salvando...' : 'Salvar'}
          </button>
        </div>
      </div>
    </div>
  )
}
