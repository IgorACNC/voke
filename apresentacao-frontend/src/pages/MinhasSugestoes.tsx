import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import {
  listarMinhasSugestoes,
  avaliarSugestao,
  descartarSugestao,
  gerarSugestoes,
  type Sugestao,
} from '../services/sugestaoService'
import { buscarEvento, type Evento } from '../services/eventoService'
import './MinhasSugestoes.css'

export default function MinhasSugestoes() {
  const navigate = useNavigate()
  const { usuario, sair } = useAuth()

  const [sugestoes, setSugestoes] = useState<Sugestao[]>([])
  const [eventos, setEventos] = useState<Map<string, Evento>>(new Map())
  const [carregando, setCarregando] = useState(true)
  const [gerando, setGerando] = useState(false)
  const [erro, setErro] = useState('')
  const [mensagem, setMensagem] = useState('')

  useEffect(() => { carregar() }, [])

  async function carregar() {
    if (!usuario) return
    setCarregando(true)
    try {
      const lista = await listarMinhasSugestoes(usuario.id)
      setSugestoes(lista)
      await carregarEventos(lista)
    } catch {
      setErro('Erro ao carregar sugestões.')
    } finally {
      setCarregando(false)
    }
  }

  async function carregarEventos(lista: Sugestao[]) {
    const ids = Array.from(new Set(lista.map((s) => s.eventoId)))
    const map = new Map<string, Evento>()
    await Promise.all(ids.map(async (id) => {
      try {
        const ev = await buscarEvento(id)
        map.set(id, ev)
      } catch { /* evento removido — sugestão ainda existe mas evento não */ }
    }))
    setEventos(map)
  }

  function limpar() { setErro(''); setMensagem('') }

  async function handleGerar() {
    if (!usuario) return
    limpar(); setGerando(true)
    try {
      const novas = await gerarSugestoes(usuario.id)
      if (novas.length === 0) {
        setMensagem('Você já atingiu o limite semanal de 4 sugestões, ou não temos eventos novos que combinem com seus interesses.')
      } else {
        setMensagem(`${novas.length} nova(s) sugestão(ões) gerada(s)!`)
      }
      await carregar()
    } catch (err: unknown) {
      const msg = (err as { response?: { data?: { mensagem?: string } } })?.response?.data?.mensagem
        ?? 'Erro ao gerar sugestões.'
      setErro(msg)
    } finally { setGerando(false) }
  }

  async function handleAvaliar(s: Sugestao, gostou: boolean) {
    limpar()
    try {
      await avaliarSugestao(s.id, gostou)
      setMensagem(gostou
        ? 'Obrigado pelo feedback! Vamos sugerir mais coisas assim.'
        : 'Anotado. Vamos evitar sugerir coisas parecidas.')
      await carregar()
    } catch {
      setErro('Erro ao registrar avaliação.')
    }
  }

  async function handleDescartar(s: Sugestao) {
    if (!confirm('Remover esta sugestão da sua lista?')) return
    limpar()
    try {
      await descartarSugestao(s.id)
      setMensagem('Sugestão removida.')
      await carregar()
    } catch {
      setErro('Erro ao remover sugestão.')
    }
  }

  const pendentes = sugestoes.filter((s) => s.status === 'PENDENTE')
  const avaliadas = sugestoes.filter((s) => s.status !== 'PENDENTE')

  return (
    <div className="sug-bg">
      <header className="sug-header">
        <button className="sug-voltar" onClick={() => navigate('/dashboard')}>← Voltar</button>
        <span className="sug-logo">Voke</span>
        <button className="sug-sair" onClick={() => { sair(); navigate('/login') }}>Sair</button>
      </header>

      <main className="sug-main">
        <div className="sug-titulo-secao">
          <div>
            <h1>Sugestões pra você</h1>
            <p>Eventos selecionados com base nos seus interesses</p>
          </div>
          <button className="sug-btn-gerar" onClick={handleGerar} disabled={gerando}>
            {gerando ? 'Gerando...' : '✨ Gerar novas'}
          </button>
        </div>

        {mensagem && <p className="sug-msg-sucesso">{mensagem}</p>}
        {erro && <p className="sug-msg-erro">{erro}</p>}

        <section className="sug-secao">
          <h2>Pendentes ({pendentes.length})</h2>
          {carregando ? (
            <p className="sug-info">Carregando...</p>
          ) : pendentes.length === 0 ? (
            <div className="sug-vazio">
              <span>💡</span>
              <p>Nada por aqui ainda.</p>
              <small>Clique em "Gerar novas" para receber sugestões baseadas nos seus interesses.</small>
              <button className="sug-btn-editar-interesses" onClick={() => navigate('/onboarding', { state: { editando: true } })}>
                Editar interesses
              </button>
            </div>
          ) : (
            <ul className="sug-lista">
              {pendentes.map((s) => {
                const ev = eventos.get(s.eventoId)
                return (
                  <li key={s.id} className="sug-card">
                    {ev ? (
                      <>
                        <div className="sug-card-info" onClick={() => navigate(`/eventos/${s.eventoId}`)}>
                          <h3>{ev.nome}</h3>
                          <p className="sug-local">📍 {ev.local}</p>
                          <p className="sug-data">
                            📅 {new Date(ev.dataHoraInicio).toLocaleDateString('pt-BR', {
                              day: '2-digit', month: 'short', year: 'numeric',
                            })}
                          </p>
                          {ev.loteAtual && (
                            <p className="sug-preco">R$ {ev.loteAtual.preco.toFixed(2)}</p>
                          )}
                        </div>
                        <div className="sug-acoes">
                          <button
                            className="sug-btn-gostei"
                            onClick={() => handleAvaliar(s, true)}
                            title="Gostei"
                          >
                            👍 Gostei
                          </button>
                          <button
                            className="sug-btn-nao-gostei"
                            onClick={() => handleAvaliar(s, false)}
                            title="Não gostei"
                          >
                            👎 Não gostei
                          </button>
                          <button
                            className="sug-btn-descartar"
                            onClick={() => handleDescartar(s)}
                            title="Descartar"
                          >
                            🗑️
                          </button>
                        </div>
                      </>
                    ) : (
                      <div className="sug-card-info sug-card-removido">
                        <p>Este evento não está mais disponível.</p>
                        <button className="sug-btn-descartar" onClick={() => handleDescartar(s)}>
                          Remover sugestão
                        </button>
                      </div>
                    )}
                  </li>
                )
              })}
            </ul>
          )}
        </section>

        {avaliadas.length > 0 && (
          <section className="sug-secao">
            <h2>Histórico ({avaliadas.length})</h2>
            <ul className="sug-lista sug-lista-historico">
              {avaliadas.map((s) => {
                const ev = eventos.get(s.eventoId)
                return (
                  <li key={s.id} className={`sug-card sug-card-historico status-${s.status.toLowerCase()}`}>
                    <div
                      className={`sug-card-info ${ev ? 'sug-card-clicavel' : ''}`}
                      onClick={() => ev && navigate(`/eventos/${s.eventoId}`)}
                    >
                      <h3>{ev?.nome ?? 'Evento removido'}</h3>
                      {ev && (
                        <>
                          <p className="sug-local">📍 {ev.local}</p>
                          <p className="sug-data">📅 {new Date(ev.dataHoraInicio).toLocaleDateString('pt-BR')}</p>
                        </>
                      )}
                    </div>
                    <div className="sug-historico-direita">
                      <span className={`sug-badge status-${s.status.toLowerCase()}`}>
                        {s.status === 'APROVADA' && '👍 Gostei'}
                        {s.status === 'REJEITADA' && '👎 Não gostei'}
                        {s.status === 'EXPIRADA' && '⏱ Expirada'}
                      </span>
                      <button
                        className="sug-btn-descartar-historico"
                        onClick={() => handleDescartar(s)}
                        title="Remover do histórico"
                      >
                        🗑️
                      </button>
                    </div>
                  </li>
                )
              })}
            </ul>
          </section>
        )}
      </main>
    </div>
  )
}
