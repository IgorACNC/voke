import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { criarEvento } from '../services/eventoService'
import { listarCategorias, type Categoria } from '../services/categoriaService'
import './CriarEvento.css'

export default function CriarEvento() {
  const { usuario, sair } = useAuth()
  const navigate = useNavigate()

  const [etapa, setEtapa] = useState<1 | 2>(1)
  const [erro, setErro] = useState('')
  const [carregando, setCarregando] = useState(false)
  const [categorias, setCategorias] = useState<Categoria[]>([])

  // Etapa 1 - dados do evento
  const [nome, setNome] = useState('')
  const [descricao, setDescricao] = useState('')
  const [local, setLocal] = useState('')
  const [dataInicio, setDataInicio] = useState('')
  const [dataFim, setDataFim] = useState('')
  const [capacidade, setCapacidade] = useState('')
  const [idadeMinima, setIdadeMinima] = useState('0')
  const [categoriasSelected, setCategoriasSelected] = useState<string[]>([])

  // Etapa 2 - lote inicial
  const [preco, setPreco] = useState('')
  const [quantidade, setQuantidade] = useState('')

  useEffect(() => {
    listarCategorias()
      .then(setCategorias)
      .catch(() => setErro('Erro ao carregar categorias.'))
  }, [])

  function toggleCategoria(id: string) {
    setCategoriasSelected((prev) =>
      prev.includes(id) ? prev.filter((c) => c !== id) : [...prev, id]
    )
  }

  function handleProximaEtapa(e: React.FormEvent) {
    e.preventDefault()
    setErro('')
    if (!nome.trim() || !local.trim() || !dataInicio || !dataFim) {
      setErro('Preencha todos os campos obrigatórios.')
      return
    }
    if (new Date(dataFim) <= new Date(dataInicio)) {
      setErro('Data de fim deve ser posterior à data de início.')
      return
    }
    if (categoriasSelected.length === 0) {
      setErro('Selecione ao menos uma categoria.')
      return
    }
    setEtapa(2)
  }

  async function handleCriar(e: React.FormEvent) {
    e.preventDefault()
    setErro('')
    if (!preco || !quantidade) {
      setErro('Preencha os dados do lote.')
      return
    }
    setCarregando(true)
    try {
      await criarEvento({
        nome: nome.trim(),
        descricao: descricao.trim(),
        local: local.trim(),
        dataHoraInicio: dataInicio,
        dataHoraFim: dataFim,
        capacidadeMaxima: Number(capacidade),
        idadeMinima: Number(idadeMinima),
        categoriaIds: categoriasSelected,
        precoLote: Number(preco),
        quantidadeLote: Number(quantidade),
      })
      navigate('/meus-eventos')
    } catch (err: unknown) {
      const msg = (err as { response?: { data?: { mensagem?: string } } })?.response?.data?.mensagem
      setErro(msg || 'Erro ao criar evento.')
    } finally {
      setCarregando(false)
    }
  }

  function handleSair() { sair(); navigate('/login') }

  return (
    <div className="cev-bg">
      <header className="cev-header">
        <span className="cev-logo" onClick={() => navigate('/meus-eventos')}>Voke</span>
        <div className="cev-header-right">
          <span className="cev-papel">{usuario?.papel}</span>
          <span className="cev-nome">{usuario?.nome}</span>
          <button className="cev-sair" onClick={handleSair}>Sair</button>
        </div>
      </header>

      <main className="cev-main">
        <button className="cev-voltar" onClick={() => etapa === 1 ? navigate('/meus-eventos') : setEtapa(1)}>
          ← Voltar
        </button>

        <div className="cev-progresso">
          <div className={`cev-step ${etapa >= 1 ? 'cev-step--ativo' : ''}`}>1. Dados do Evento</div>
          <div className="cev-step-sep" />
          <div className={`cev-step ${etapa >= 2 ? 'cev-step--ativo' : ''}`}>2. Configurar Lote</div>
        </div>

        {erro && <div className="cev-erro">{erro}</div>}

        {etapa === 1 && (
          <form className="cev-form" onSubmit={handleProximaEtapa}>
            <h1 className="cev-titulo">Criar Evento</h1>

            <label className="cev-label">Nome do Evento *</label>
            <input className="cev-input" value={nome} onChange={(e) => setNome(e.target.value)} required />

            <label className="cev-label">Descrição</label>
            <textarea className="cev-textarea" rows={3} value={descricao}
              onChange={(e) => setDescricao(e.target.value)} />

            <label className="cev-label">Local Físico *</label>
            <input className="cev-input" value={local} onChange={(e) => setLocal(e.target.value)} required />

            <div className="cev-row">
              <div className="cev-col">
                <label className="cev-label">Data e Hora Início *</label>
                <input className="cev-input" type="datetime-local" value={dataInicio}
                  onChange={(e) => setDataInicio(e.target.value)} required />
              </div>
              <div className="cev-col">
                <label className="cev-label">Data e Hora Término *</label>
                <input className="cev-input" type="datetime-local" value={dataFim}
                  onChange={(e) => setDataFim(e.target.value)} required />
              </div>
            </div>

            <div className="cev-row">
              <div className="cev-col">
                <label className="cev-label">Capacidade Máxima *</label>
                <input className="cev-input" type="number" min="1" value={capacidade}
                  onChange={(e) => setCapacidade(e.target.value)} required />
              </div>
              <div className="cev-col">
                <label className="cev-label">Classificação Etária</label>
                <input className="cev-input" type="number" min="0" max="21" value={idadeMinima}
                  onChange={(e) => setIdadeMinima(e.target.value)} />
              </div>
            </div>

            <label className="cev-label">Categorias *</label>
            <div className="cev-categorias">
              {categorias.map((c) => (
                <button
                  key={c.id}
                  type="button"
                  className={`cev-tag ${categoriasSelected.includes(c.id) ? 'cev-tag--ativo' : ''}`}
                  onClick={() => toggleCategoria(c.id)}
                >
                  {c.nome}
                </button>
              ))}
            </div>

            <button className="cev-btn-primario" type="submit">
              Próximo: Configurar Lote →
            </button>
          </form>
        )}

        {etapa === 2 && (
          <form className="cev-form" onSubmit={handleCriar}>
            <h1 className="cev-titulo">Configurar Lote Inicial</h1>
            <p className="cev-info">
              <strong>Evento:</strong> {nome} &bull; <strong>Capacidade:</strong> {capacidade} pessoas
            </p>

            <label className="cev-label">Nome do Lote</label>
            <input className="cev-input" value="1º Lote" disabled />

            <label className="cev-label">Quantidade de Ingressos *</label>
            <input className="cev-input" type="number" min="1" max={capacidade}
              value={quantidade} onChange={(e) => setQuantidade(e.target.value)} required />

            <label className="cev-label">Preço (R$) *</label>
            <input className="cev-input" type="number" min="0" step="0.01"
              value={preco} onChange={(e) => setPreco(e.target.value)} required />

            <button className="cev-btn-primario" type="submit" disabled={carregando}>
              {carregando ? 'Criando...' : 'Criar Evento'}
            </button>
          </form>
        )}
      </main>
    </div>
  )
}
