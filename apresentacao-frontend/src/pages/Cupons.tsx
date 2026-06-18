import { useEffect, useMemo, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import {
  listarMeusCupons, listarTodosCupons,
  criarCupom, criarCupomGlobal,
  alterarAtivoCupom, excluirCupom,
  type Cupom, type TipoDescontoCupom,
} from '../services/cupomService'
import { listarMeusEventos, type Evento } from '../services/eventoService'
import './Social.css'

export default function Cupons() {
  const navigate = useNavigate()
  const { usuario } = useAuth()

  const isAdmin = usuario?.papel === 'ADMIN'

  const [cupons, setCupons] = useState<Cupom[]>([])
  const [eventos, setEventos] = useState<Evento[]>([])
  const [carregando, setCarregando] = useState(false)
  const [erro, setErro] = useState('')
  const [mensagem, setMensagem] = useState('')

  const [codigo, setCodigo] = useState('')
  const [desconto, setDesconto] = useState('')
  const [tipoDesconto, setTipoDesconto] = useState<TipoDescontoCupom>('FIXO')
  const [quantidade, setQuantidade] = useState('')
  const [eventoId, setEventoId] = useState<string>('')
  const [escopo, setEscopo] = useState<'evento' | 'organizador'>('evento')
  const [parceiroId, setParceiroId] = useState<string>('')

  useEffect(() => {
    if (!usuario) return
    carregar()
  }, [usuario?.id])

  async function carregar() {
    setErro('')
    setCarregando(true)
    try {
      if (isAdmin) {
        setCupons(await listarTodosCupons())
      } else {
        const [cs, evs] = await Promise.all([
          listarMeusCupons(usuario!.id),
          listarMeusEventos().catch(() => []),
        ])
        setCupons(cs)
        setEventos(evs)
      }
    } catch {
      setErro('Não foi possível carregar os cupons.')
    } finally {
      setCarregando(false)
    }
  }

  async function handleCriar(e: React.FormEvent) {
    e.preventDefault()
    setErro(''); setMensagem('')
    const valorDesconto = parseFloat(desconto || '0')
    const qtde = parseInt(quantidade || '0', 10)
    if (!codigo.trim() || valorDesconto <= 0 || qtde <= 0) {
      setErro('Preencha código, desconto (> 0) e quantidade (> 0).')
      return
    }
    if (tipoDesconto === 'PERCENTUAL' && valorDesconto > 100) {
      setErro('Desconto percentual não pode ultrapassar 100%.')
      return
    }
    try {
      if (isAdmin) {
        await criarCupomGlobal({ codigo, desconto: valorDesconto, tipoDesconto, quantidadeMaxima: qtde })
      } else {
        await criarCupom({
          codigo,
          desconto: valorDesconto,
          tipoDesconto,
          organizadorId: usuario!.id,
          eventoId: escopo === 'evento' ? (eventoId || null) : null,
          parceiroId: parceiroId.trim() || undefined,
          quantidadeMaxima: qtde,
        })
      }
      setMensagem('Cupom criado com sucesso.')
      setCodigo(''); setDesconto(''); setQuantidade(''); setEventoId(''); setParceiroId('')
      setTipoDesconto('FIXO')
      await carregar()
    } catch (e: unknown) {
      setErro((e as any)?.response?.data?.mensagem ?? 'Erro ao criar cupom.')
    }
  }

  async function handleToggleAtivo(c: Cupom) {
    setErro(''); setMensagem('')
    try {
      await alterarAtivoCupom(c.id, !c.ativo)
      await carregar()
    } catch {
      setErro('Não foi possível alterar o status.')
    }
  }

  async function handleExcluir(id: string) {
    if (!confirm('Excluir este cupom?')) return
    try {
      await excluirCupom(id)
      setMensagem('Cupom excluído.')
      await carregar()
    } catch {
      setErro('Não foi possível excluir o cupom.')
    }
  }

  const nomeEvento = useMemo(() => {
    const map = new Map(eventos.map((e) => [e.id, e.nome]))
    return (id: string | null) => (id ? map.get(id) ?? '—' : 'Todos eventos')
  }, [eventos])

  if (!usuario) return null
  if (usuario.papel === 'PARTICIPANTE') {
    navigate('/dashboard')
    return null
  }

  return (
    <div className="social-bg">
      <header className="social-header">
        <button className="social-voltar" onClick={() => navigate(isAdmin ? '/admin' : '/dashboard')}>
          Voltar
        </button>
        <span className="social-logo">Voke</span>
        <div style={{ width: 90 }} />
      </header>

      <main className="social-main">
        <section className="social-title">
          <h1>Cupons promocionais</h1>
          <p>
            {isAdmin
              ? 'Crie cupons globais aplicáveis a qualquer evento.'
              : 'Crie cupons para seus eventos. Defina código, desconto fixo e limite de uso.'}
          </p>
        </section>

        {mensagem && <p className="social-msg-sucesso">{mensagem}</p>}
        {erro && <p className="social-msg-erro">{erro}</p>}

        <div className="social-card">
          <h2>{isAdmin ? 'Novo cupom global' : 'Novo cupom'}</h2>
          <form className="social-form-col" onSubmit={handleCriar}>
            <label>
              Código
              <input value={codigo} onChange={(e) => setCodigo(e.target.value.toUpperCase())}
                placeholder="VERAO20" required />
            </label>

            <label>
              Tipo de desconto
              <select value={tipoDesconto} onChange={(e) => setTipoDesconto(e.target.value as TipoDescontoCupom)}>
                <option value="FIXO">Valor fixo (R$)</option>
                <option value="PERCENTUAL">Percentual (%)</option>
              </select>
            </label>

            <label>
              {tipoDesconto === 'PERCENTUAL' ? 'Desconto (%)' : 'Desconto (R$)'}
              <input type="number" step="0.01" min="0.01"
                max={tipoDesconto === 'PERCENTUAL' ? 100 : undefined}
                value={desconto} onChange={(e) => setDesconto(e.target.value)} required />
            </label>

            <label>
              Quantidade máxima de usos
              <input type="number" min="1"
                value={quantidade} onChange={(e) => setQuantidade(e.target.value)} required />
            </label>

            {!isAdmin && (
              <>
                <label>
                  Escopo
                  <select value={escopo} onChange={(e) => setEscopo(e.target.value as 'evento' | 'organizador')}>
                    <option value="evento">Para um evento específico</option>
                    <option value="organizador">Para todos os meus eventos</option>
                  </select>
                </label>
                {escopo === 'evento' && (
                  <label>
                    Evento
                    <select value={eventoId} onChange={(e) => setEventoId(e.target.value)} required>
                      <option value="">Selecione um evento</option>
                      {eventos.map((ev) => (
                        <option key={ev.id} value={ev.id}>{ev.nome}</option>
                      ))}
                    </select>
                  </label>
                )}
                <label>
                  ID do Parceiro <span style={{ color: '#9ca3af', fontWeight: 400 }}>(opcional)</span>
                  <input
                    value={parceiroId}
                    onChange={(e) => setParceiroId(e.target.value)}
                    placeholder="UUID do parceiro"
                  />
                </label>
              </>
            )}

            <button>Criar cupom</button>
          </form>
        </div>

        {carregando ? (
          <p>Carregando...</p>
        ) : (
          <div className="social-card">
            <h2>{isAdmin ? 'Todos os cupons' : 'Meus cupons'}</h2>
            {cupons.length === 0 && <p className="social-vazio">Nenhum cupom cadastrado.</p>}
            {cupons.map((c) => (
              <div key={c.id} className="cupom-item" style={{ opacity: c.ativo ? 1 : 0.55 }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <strong>{c.codigo}</strong>
                  <span style={{
                    fontSize: '0.75rem',
                    padding: '2px 10px',
                    borderRadius: 12,
                    background: c.ativo ? '#dcfce7' : '#fee2e2',
                    color: c.ativo ? '#15803d' : '#b91c1c',
                  }}>
                    {c.ativo ? 'ATIVO' : 'INATIVO'}
                  </span>
                </div>
                <p style={{ margin: '0.4rem 0' }}>
                  Desconto: <strong>
                    {c.tipoDesconto === 'PERCENTUAL'
                      ? `${c.desconto.toFixed(c.desconto % 1 === 0 ? 0 : 2)}%`
                      : `R$ ${c.desconto.toFixed(2)}`}
                  </strong> •
                  Usos: {c.quantidadeUtilizada}/{c.quantidadeMaxima}
                </p>
                <p style={{ margin: '0.2rem 0', color: '#6b7280', fontSize: '0.85rem' }}>
                  {c.global ? '🌐 Global (todos eventos)' : `🎫 ${nomeEvento(c.eventoId)}`}
                </p>
                <div style={{ marginTop: 8, display: 'flex', gap: 8 }}>
                  <button onClick={() => handleToggleAtivo(c)}>
                    {c.ativo ? 'Desativar' : 'Ativar'}
                  </button>
                  <button className="social-btn-sec"
                    onClick={() => handleExcluir(c.id)}
                    disabled={c.quantidadeUtilizada > 0}
                    title={c.quantidadeUtilizada > 0 ? 'Cupom já usado, não pode ser excluído' : ''}>
                    Excluir
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}
      </main>
    </div>
  )
}