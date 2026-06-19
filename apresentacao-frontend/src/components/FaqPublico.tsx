import { useEffect, useState } from 'react'
import { listarFaq, type PerguntaFaq } from '../services/perguntaFaqService'
import '../pages/FaqEvento.css'

interface Props {
  eventoId: string
}

export default function FaqPublico({ eventoId }: Props) {
  const [perguntas, setPerguntas] = useState<PerguntaFaq[]>([])
  const [expandido, setExpandido] = useState<Record<string, boolean>>({})

  useEffect(() => {
    listarFaq(eventoId).then(setPerguntas).catch(() => setPerguntas([]))
  }, [eventoId])

  if (perguntas.length === 0) return null

  function alternar(id: string) {
    setExpandido((prev) => ({ ...prev, [id]: !prev[id] }))
  }

  return (
    <div className="faq-publico">
      <h3>Perguntas Frequentes</h3>
      <div className="faq-lista">
        {perguntas.map((p) => (
          <div key={p.id} className="faq-card">
            <div className="faq-card-topo" onClick={() => alternar(p.id)}>
              <span className="faq-pos">{p.posicao}</span>
              <span className="faq-pergunta-texto">{p.pergunta}</span>
              <span className={`faq-chevron ${expandido[p.id] ? 'aberto' : ''}`}>▼</span>
            </div>
            {expandido[p.id] && <div className="faq-resposta">{p.resposta}</div>}
          </div>
        ))}
      </div>
    </div>
  )
}
