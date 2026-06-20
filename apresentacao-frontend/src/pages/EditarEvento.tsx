import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import {
  buscarEvento, editarEvento, criarLote, type Evento,
} from '../services/eventoService'
import './EditarEvento.css'

export default function EditarEvento() {
  const { id } = useParams<{ id: string }>()
  const { usuario, sair } = useAuth()
  const navigate = useNavigate()

  const [evento, setEvento] = useState<Evento | null>(null)
  const [carregando, setCarregando] = useState(true)
  const [salvando, setSalvando] = useState(false)
  const [erro, setErro] = useState('')
  const [sucesso, setSucesso] = useState('')

  // Campos de edição
  const [nome, setNome] = useState('')
  const [local, setLocal] = useState('')
  const [dataInicio, setDataInicio] = useState('')
  const [dataFim, setDataFim] = useState('')
  const [capacidade, setCapacidade] = useState('')

  // Novo lote
  const [mostrarFormLote, setMostrarFormLote] = useState(false)
  const [novoPreco, setNovoPreco] = useState('')
  const [novaQuantidade, setNovaQuantidade] = useState('')
  const [criandoLote, setCriandoLote] = useState(false)

  useEffect(() => {
    if (!id) return
    buscarEvento(id)
      .then((ev) => {
        setEvento(ev)
        setNome(ev.nome)
        setLocal(ev.local)
        setDataInicio(ev.dataHoraInicio.slice(0, 16))
        setDataFim(ev.dataHoraFim.slice(0, 16))
        setCapacidade(String(ev.capacidadeMaxima))
      })
      .catch(() => setErro('Erro ao carregar evento.'))
      .finally(() => setCarregando(false))
  }, [id])

  async function handleSalvar(e: React.FormEvent) {
    e.preventDefault()
    setErro(''); setSucesso('')
    if (new Date(dataFim) <= new Date(dataInicio)) {
      setErro('Data de fim deve ser posterior à data de início.')
      return
    }
    setSalvando(true)
    try {
      const atualizado = await editarEvento(id!, {
        nome: nome.trim(), local: local.trim(),
        dataHoraInicio: dataInicio, dataHoraFim: dataFim,
        capacidadeMaxima: Number(capacidade),
      })
      setEvento(atualizado)
      setSucesso('Evento atualizado com sucesso!')
    } catch (err: unknown) {
      const msg = (err as { response?: { data?: { mensagem?: string } } })?.response?.data?.mensagem
      setErro(msg || 'Erro ao salvar.')
    } finally {
      setSalvando(false)
    }
  }

  async function handleCriarLote(e: React.FormEvent) {
    e.preventDefault()
    setErro(''); setSucesso('')
    setCriandoLote(true)
    try {
      const atualizado = await criarLote(id!, Number(novoPreco), Number(novaQuantidade))
      setEvento(atualizado)
      setMostrarFormLote(false)
      setNovoPreco(''); setNovaQuantidade('')
      setSucesso('Novo lote criado com sucesso!')
    } catch (err: unknown) {
      const msg = (err as { response?: { data?: { mensagem?: string } } })?.response?.data?.mensagem
      setErro(msg || 'Erro ao criar lote.')
    } finally {
      setCriandoLote(false)
    }
  }

  function handleSair() { sair(); navigate('/login') }

  if (carregando) return <div className="eev-loading">Carregando...</div>

  return (
    <div className="eev-bg">
      <header className="eev-header">
        <span className="eev-logo" onClick={() => navigate('/meus-eventos')}>Voke</span>
        <div className="eev-header-right">
          <span className="eev-papel">{usuario?.papel}</span>
          <span className="eev-nome">{usuario?.nome}</span>
          <button className="eev-sair" onClick={handleSair}>Sair</button>
        </div>
      </header>

      <main className="eev-main">
        <button className="eev-voltar" onClick={() => navigate('/meus-eventos')}>← Meus Eventos</button>

        {erro && <div className="eev-erro">{erro}</div>}
        {sucesso && <div className="eev-sucesso">{sucesso}</div>}

        <form className="eev-card" onSubmit={handleSalvar}>
          <h1 className="eev-titulo">Editar Evento</h1>

          <label className="eev-label">Nome do Evento</label>
          <input className="eev-input" value={nome} onChange={(e) => setNome(e.target.value)}
            disabled={evento?.status !== 'ATIVO'} required />

          <label className="eev-label">Local Físico</label>
          <input className="eev-input" value={local} onChange={(e) => setLocal(e.target.value)}
            disabled={evento?.status !== 'ATIVO'} required />

          <div className="eev-row">
            <div className="eev-col">
              <label className="eev-label">Data e Hora Início</label>
              <input className="eev-input" type="datetime-local" value={dataInicio}
                onChange={(e) => setDataInicio(e.target.value)}
                disabled={evento?.status !== 'ATIVO'} required />
            </div>
            <div className="eev-col">
              <label className="eev-label">Data e Hora Término</label>
              <input className="eev-input" type="datetime-local" value={dataFim}
                onChange={(e) => setDataFim(e.target.value)}
                disabled={evento?.status !== 'ATIVO'} required />
            </div>
          </div>

          <label className="eev-label">Capacidade Máxima</label>
          <input className="eev-input" type="number" min="1" value={capacidade}
            onChange={(e) => setCapacidade(e.target.value)}
            disabled={evento?.status !== 'ATIVO'} required />

          {evento?.status === 'ATIVO' && (
            <button className="eev-btn-primario" type="submit" disabled={salvando}>
              {salvando ? 'Salvando...' : 'Salvar Alterações'}
            </button>
          )}
        </form>

        {/* Gerenciamento de lotes */}
        <div className="eev-card eev-card--lotes">
          <div className="eev-lotes-header">
            <h2 className="eev-lotes-titulo">Lotes do Evento</h2>
            <span className="eev-cap-info">Capacidade total: {evento?.capacidadeMaxima} pessoas</span>
          </div>

          {evento?.loteAtual && (() => {
            const lote = evento.loteAtual;
            const esgotado = lote.quantidadeVendida >= lote.quantidadeTotal;
            return (
            <div className={`eev-lote ${lote.ativo ? 'eev-lote--ativo' : 'eev-lote--encerrado'}`}>
              <div className="eev-lote-linha">
                <span className="eev-lote-nome">{lote.numero}º Lote — {lote.ativo ? 'ATIVO' : esgotado ? 'ESGOTADO' : 'AGUARDANDO'}</span>
                <span className={`eev-lote-badge ${lote.ativo ? '' : esgotado ? 'eev-lote-badge--esgotado' : 'eev-lote-badge--bloq'}`}>
                  {lote.ativo ? 'EM VENDA' : esgotado ? 'ESGOTADO' : 'BLOQUEADO'}
                </span>
              </div>
              <div className="eev-lote-detalhe">
                <span>Preço: R$ {lote.preco.toFixed(2)}</span>
                <span>Vendidos: {lote.quantidadeVendida}/{lote.quantidadeTotal}</span>
              </div>
              {!lote.ativo && !esgotado && (
                <p className="eev-lote-aviso">Será liberado após esgotamento do lote anterior</p>
              )}
            </div>
            );
          })()}

          {evento?.status === 'ATIVO' && evento?.loteAtual && (!evento.loteAtual.ativo || evento.loteAtual.quantidadeVendida >= evento.loteAtual.quantidadeTotal) && evento.totalIngressosVendidos < evento.capacidadeMaxima && (
            <>
              {!mostrarFormLote ? (
                <button className="eev-btn-lote" onClick={() => setMostrarFormLote(true)}>
                  + Criar Próximo Lote
                </button>
              ) : (
                <form className="eev-form-lote" onSubmit={handleCriarLote}>
                  <h3 className="eev-form-lote-titulo">Novo Lote</h3>
                  <label className="eev-label">Quantidade de Ingressos</label>
                  <input className="eev-input" type="number" min="1" max={evento.capacidadeMaxima - evento.totalIngressosVendidos}
                    value={novaQuantidade} onChange={(e) => setNovaQuantidade(e.target.value)} required />
                  <span className="eev-vagas-restantes">Vagas restantes: {evento.capacidadeMaxima - evento.totalIngressosVendidos}</span>
                  <label className="eev-label">Preço (R$)</label>
                  <input className="eev-input" type="number" min="0" step="0.01"
                    value={novoPreco} onChange={(e) => setNovoPreco(e.target.value)} required />
                  <div className="eev-form-lote-acoes">
                    <button type="button" className="eev-btn-sec" onClick={() => setMostrarFormLote(false)}>
                      Cancelar
                    </button>
                    <button type="submit" className="eev-btn-primario" disabled={criandoLote}>
                      {criandoLote ? 'Criando...' : 'Criar Lote'}
                    </button>
                  </div>
                </form>
              )}
            </>
          )}

          {evento?.status === 'ATIVO' && evento?.loteAtual && evento.totalIngressosVendidos >= evento.capacidadeMaxima && (
            <p className="eev-lote-info-aviso">
              Capacidade máxima do evento atingida. Não é possível criar novos lotes.
            </p>
          )}

          {evento?.status === 'ATIVO' && evento?.loteAtual?.ativo && evento.loteAtual.quantidadeVendida < evento.loteAtual.quantidadeTotal && (
            <p className="eev-lote-info-aviso">
              O próximo lote só pode ser criado após o lote atual ser esgotado.
            </p>
          )}
        </div>

        {evento?.status === 'ATIVO' && (
          <div className="eev-card eev-card--grupo">
            <h2 className="eev-lotes-titulo">Grupo do Evento</h2>
            <p className="eev-grupo-desc">Crie ou gerencie o grupo de comunicação deste evento.</p>
            <button className="eev-btn-grupo" onClick={() => navigate(`/eventos/${id}/grupo`)}>
              Acessar Grupo
            </button>
          </div>
        )}
      </main>
    </div>
  )
}
