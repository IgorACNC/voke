import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import {
  listarCategoriasAdmin,
  criarCategoria,
  editarCategoria,
  removerCategoria,
} from '../services/adminService'
import type { Categoria } from '../services/categoriaService'
import './Admin.css'

export default function Admin() {
  const navigate = useNavigate()
  const { usuario, sair } = useAuth()

  const [categorias, setCategorias] = useState<Categoria[]>([])
  const [carregando, setCarregando] = useState(false)
  const [erro, setErro] = useState('')
  const [mensagem, setMensagem] = useState('')

  const [novoNome, setNovoNome] = useState('')
  const [editandoId, setEditandoId] = useState<string | null>(null)
  const [editandoNome, setEditandoNome] = useState('')
  const [confirmarRemocaoId, setConfirmarRemocaoId] = useState<string | null>(null)

  useEffect(() => { carregar() }, [])

  async function carregar() {
    setCarregando(true)
    try {
      setCategorias(await listarCategoriasAdmin())
    } catch {
      setErro('Erro ao carregar categorias.')
    } finally {
      setCarregando(false)
    }
  }

  function limparMensagens() { setErro(''); setMensagem('') }

  async function handleCriar(e: React.FormEvent) {
    e.preventDefault()
    limparMensagens()
    if (!novoNome.trim()) return
    setCarregando(true)
    try {
      await criarCategoria(novoNome.trim())
      setNovoNome('')
      setMensagem('Categoria criada com sucesso.')
      await carregar()
    } catch (err: unknown) {
      const msg = (err as { response?: { data?: { mensagem?: string } } })?.response?.data?.mensagem
        ?? 'Não foi possível criar a categoria.'
      setErro(msg)
    } finally {
      setCarregando(false)
    }
  }

  function iniciarEdicao(c: Categoria) {
    limparMensagens()
    setEditandoId(c.id)
    setEditandoNome(c.nome)
  }

  function cancelarEdicao() {
    setEditandoId(null)
    setEditandoNome('')
  }

  async function salvarEdicao() {
    if (!editandoId || !editandoNome.trim()) return
    limparMensagens()
    setCarregando(true)
    try {
      await editarCategoria(editandoId, editandoNome.trim())
      setMensagem('Categoria atualizada.')
      cancelarEdicao()
      await carregar()
    } catch (err: unknown) {
      const msg = (err as { response?: { data?: { mensagem?: string } } })?.response?.data?.mensagem
        ?? 'Não foi possível salvar.'
      setErro(msg)
    } finally {
      setCarregando(false)
    }
  }

  async function handleRemover(id: string) {
    limparMensagens()
    setCarregando(true)
    try {
      await removerCategoria(id)
      setMensagem('Categoria removida.')
      setConfirmarRemocaoId(null)
      await carregar()
    } catch (err: unknown) {
      const msg = (err as { response?: { data?: { mensagem?: string } } })?.response?.data?.mensagem
        ?? 'Não foi possível remover.'
      setErro(msg)
    } finally {
      setCarregando(false)
    }
  }

  function handleSair() { sair(); navigate('/login') }

  return (
    <div className="admin-bg">
      <header className="admin-header">
        <span className="admin-logo">Voke</span>
        <div className="admin-header-right">
          <span className="admin-papel">ADMIN</span>
          <span className="admin-nome">{usuario?.nome}</span>
          <button className="admin-sair" onClick={handleSair}>Sair</button>
        </div>
      </header>

      <main className="admin-main">
        <div className="admin-boas-vindas">
          <h1>Painel do Administrador</h1>
          <p>Gerencie as categorias de eventos disponíveis na plataforma.</p>
        </div>

        {mensagem && <p className="admin-msg-sucesso">{mensagem}</p>}
        {erro && <p className="admin-msg-erro">{erro}</p>}

        <section className="admin-secao">
          <h2>Nova categoria</h2>
          <form onSubmit={handleCriar} className="admin-form-novo">
            <input
              type="text"
              placeholder="Ex: Tecnologia"
              value={novoNome}
              onChange={(e) => setNovoNome(e.target.value)}
              disabled={carregando}
              required
            />
            <button type="submit" className="admin-btn-primario" disabled={carregando || !novoNome.trim()}>
              Adicionar
            </button>
          </form>
        </section>

        <section className="admin-secao">
          <h2>Categorias cadastradas ({categorias.length})</h2>
          {categorias.length === 0 ? (
            <p className="admin-vazio">Nenhuma categoria cadastrada ainda.</p>
          ) : (
            <ul className="admin-lista">
              {categorias.map((c) => (
                <li key={c.id} className="admin-item">
                  {editandoId === c.id ? (
                    <>
                      <input
                        type="text"
                        value={editandoNome}
                        onChange={(e) => setEditandoNome(e.target.value)}
                        disabled={carregando}
                      />
                      <div className="admin-item-acoes">
                        <button className="admin-btn-primario" onClick={salvarEdicao} disabled={carregando}>
                          Salvar
                        </button>
                        <button className="admin-btn-secundario" onClick={cancelarEdicao} disabled={carregando}>
                          Cancelar
                        </button>
                      </div>
                    </>
                  ) : (
                    <>
                      <span className="admin-item-nome">{c.nome}</span>
                      <div className="admin-item-acoes">
                        <button className="admin-btn-secundario" onClick={() => iniciarEdicao(c)}>
                          Editar
                        </button>
                        <button className="admin-btn-perigo" onClick={() => setConfirmarRemocaoId(c.id)}>
                          Remover
                        </button>
                      </div>
                    </>
                  )}
                </li>
              ))}
            </ul>
          )}
        </section>

        {confirmarRemocaoId && (
          <div className="admin-modal-bg" onClick={() => !carregando && setConfirmarRemocaoId(null)}>
            <div className="admin-modal" onClick={(e) => e.stopPropagation()}>
              <h2>Remover categoria</h2>
              <p>Essa ação não pode ser desfeita. Tem certeza?</p>
              <div className="admin-item-acoes">
                <button className="admin-btn-secundario" onClick={() => setConfirmarRemocaoId(null)} disabled={carregando}>
                  Cancelar
                </button>
                <button className="admin-btn-perigo" onClick={() => handleRemover(confirmarRemocaoId)} disabled={carregando}>
                  Sim, remover
                </button>
              </div>
            </div>
          </div>
        )}
      </main>
    </div>
  )
}
