import { useEffect, useState } from 'react'
import { useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { listarCategorias } from '../services/categoriaService'
import type { Categoria } from '../services/categoriaService'
import { buscarPreferencias, salvarPreferencias } from '../services/sugestaoService'
import './Onboarding.css'

export default function Onboarding() {
  const navigate = useNavigate()
  const location = useLocation()
  const { usuario } = useAuth()

  const editando = (location.state as { editando?: boolean } | null)?.editando === true

  const [categorias, setCategorias] = useState<Categoria[]>([])
  const [selecionadas, setSelecionadas] = useState<Set<string>>(new Set())
  const [carregando, setCarregando] = useState(false)
  const [erro, setErro] = useState('')

  useEffect(() => { carregar() }, [])

  async function carregar() {
    if (!usuario) return
    setCarregando(true)
    try {
      const [listaCategorias, atuais] = await Promise.all([
        listarCategorias(),
        editando ? buscarPreferencias(usuario.id) : Promise.resolve([] as string[]),
      ])
      setCategorias(listaCategorias)
      if (atuais.length > 0) setSelecionadas(new Set(atuais))
    } catch {
      setErro('Erro ao carregar categorias. Tente novamente.')
    } finally {
      setCarregando(false)
    }
  }

  function toggle(id: string) {
    setErro('')
    setSelecionadas((prev) => {
      const novo = new Set(prev)
      if (novo.has(id)) novo.delete(id)
      else novo.add(id)
      return novo
    })
  }

  async function continuar() {
    if (!usuario) return
    if (selecionadas.size === 0) {
      setErro('Selecione pelo menos uma categoria para continuar.')
      return
    }
    setCarregando(true)
    try {
      await salvarPreferencias(usuario.id, Array.from(selecionadas))
      navigate(editando ? '/meu-perfil' : '/dashboard')
    } catch (err: unknown) {
      const msg = (err as { response?: { data?: { mensagem?: string } } })?.response?.data?.mensagem
        ?? 'Não foi possível salvar suas preferências.'
      setErro(msg)
      setCarregando(false)
    }
  }

  const titulo = editando
    ? 'Editar interesses'
    : `Bem-vindo(a), ${usuario?.nome?.split(' ')[0] ?? ''}!`
  const subtitulo = editando
    ? <>Atualize as <strong>categorias que você curte</strong> para receber sugestões mais relevantes.</>
    : <>Conta pra gente: <strong>quais tipos de eventos você curte?</strong><br />Vamos usar suas escolhas para sugerir experiências relevantes.</>
  const textoBotao = editando ? 'Salvar' : 'Continuar →'

  return (
    <div className="onb-bg">
      <div className="onb-card">
        <div className="onb-logo">
          <span className="onb-logo-text">Voke</span>
        </div>
        <h1 className="onb-titulo">{titulo}</h1>
        <p className="onb-sub">{subtitulo}</p>

        {erro && <p className="onb-erro">{erro}</p>}

        {categorias.length === 0 && !erro ? (
          <p className="onb-vazio">{carregando ? 'Carregando categorias...' : 'Nenhuma categoria disponível ainda.'}</p>
        ) : (
          <div className="onb-grid">
            {categorias.map((c) => (
              <button
                key={c.id}
                type="button"
                className={`onb-chip ${selecionadas.has(c.id) ? 'selecionada' : ''}`}
                onClick={() => toggle(c.id)}
                disabled={carregando}
              >
                {c.nome}
              </button>
            ))}
          </div>
        )}

        <div className="onb-rodape">
          <span className="onb-contagem">{selecionadas.size} selecionada(s)</span>
          <div className="onb-rodape-acoes">
            {editando && (
              <button
                type="button"
                className="onb-btn-secundario"
                onClick={() => navigate('/meu-perfil')}
                disabled={carregando}
              >
                Cancelar
              </button>
            )}
            <button className="onb-btn" onClick={continuar} disabled={carregando || selecionadas.size === 0}>
              {carregando ? 'Salvando...' : textoBotao}
            </button>
          </div>
        </div>
      </div>
    </div>
  )
}
