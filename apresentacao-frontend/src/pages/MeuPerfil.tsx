import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { alterarSenha, editarPerfil, removerConta } from '../services/participanteService'
import './MeuPerfil.css'

type Modo = 'visualizar' | 'editar' | 'senha'

export default function MeuPerfil() {
  const navigate = useNavigate()
  const { usuario, salvarSessao, sair } = useAuth()

  const [modo, setModo] = useState<Modo>('visualizar')
  const [carregando, setCarregando] = useState(false)
  const [erro, setErro] = useState('')
  const [mensagem, setMensagem] = useState('')
  const [confirmarExclusao, setConfirmarExclusao] = useState(false)

  const [form, setForm] = useState({
    nome: usuario?.nome ?? '',
    email: usuario?.email ?? '',
  })

  const [senhaForm, setSenhaForm] = useState({
    senhaAtual: '', novaSenha: '', confirmarSenha: '',
  })

  if (!usuario) {
    navigate('/login')
    return null
  }

  function atualizar(campo: string, valor: string) {
    setErro('')
    setMensagem('')
    setForm((f) => ({ ...f, [campo]: valor }))
  }

  function cancelarEdicao() {
    setForm({ nome: usuario!.nome, email: usuario!.email })
    setSenhaForm({ senhaAtual: '', novaSenha: '', confirmarSenha: '' })
    setModo('visualizar')
    setErro('')
  }

  function atualizarSenhaCampo(campo: string, valor: string) {
    setErro(''); setMensagem('')
    setSenhaForm((f) => ({ ...f, [campo]: valor }))
  }

  async function handleAlterarSenha(e: React.FormEvent) {
    e.preventDefault()
    setErro(''); setMensagem('')
    if (senhaForm.novaSenha !== senhaForm.confirmarSenha) {
      setErro('As novas senhas não conferem.')
      return
    }
    setCarregando(true)
    try {
      await alterarSenha({
        id: usuario!.id,
        senhaAtual: senhaForm.senhaAtual,
        novaSenha: senhaForm.novaSenha,
      })
      setSenhaForm({ senhaAtual: '', novaSenha: '', confirmarSenha: '' })
      setMensagem('Senha alterada com sucesso.')
      setModo('visualizar')
    } catch (err: unknown) {
      const msg = (err as { response?: { data?: { mensagem?: string } } })?.response?.data?.mensagem
        ?? 'Não foi possível alterar a senha.'
      setErro(msg)
    } finally {
      setCarregando(false)
    }
  }

  async function handleSalvar(e: React.FormEvent) {
    e.preventDefault()
    setErro('')
    setMensagem('')
    setCarregando(true)
    try {
      await editarPerfil({ id: usuario!.id, nome: form.nome, email: form.email })
      const token = localStorage.getItem('voke_token') ?? ''
      salvarSessao({
        token,
        papel: usuario!.papel,
        id: usuario!.id,
        nome: form.nome,
        email: form.email,
      })
      setMensagem('Dados atualizados com sucesso.')
      setModo('visualizar')
    } catch (err: unknown) {
      const msg = (err as { response?: { data?: { mensagem?: string } } })?.response?.data?.mensagem
        ?? 'Não foi possível salvar as alterações.'
      setErro(msg)
    } finally {
      setCarregando(false)
    }
  }

  async function handleRemover() {
    setErro('')
    setCarregando(true)
    try {
      await removerConta(usuario!.id)
      sair()
      navigate('/login')
    } catch (err: unknown) {
      const msg = (err as { response?: { data?: { mensagem?: string } } })?.response?.data?.mensagem
        ?? 'Não foi possível remover a conta.'
      setErro(msg)
      setCarregando(false)
      setConfirmarExclusao(false)
    }
  }

  return (
    <div className="perfil-bg">
      <header className="perfil-header">
        <button className="perfil-voltar" onClick={() => navigate('/dashboard')}>← Voltar</button>
        <span className="perfil-logo">Voke</span>
        <div style={{ width: 90 }} />
      </header>

      <main className="perfil-main">
        <div className="perfil-card">
          <div className="perfil-avatar">{usuario.nome.charAt(0).toUpperCase()}</div>
          <h1 className="perfil-titulo">Minha Conta</h1>
          <p className="perfil-sub">Gerencie seus dados pessoais</p>

          {mensagem && <p className="perfil-msg-sucesso">{mensagem}</p>}
          {erro && <p className="perfil-msg-erro">{erro}</p>}

          {modo === 'visualizar' ? (
            <div className="perfil-info">
              <div className="perfil-campo">
                <label>Nome completo</label>
                <span>{usuario.nome}</span>
              </div>
              <div className="perfil-campo">
                <label>E-mail</label>
                <span>{usuario.email}</span>
              </div>
              <div className="perfil-campo">
                <label>Tipo de conta</label>
                <span>{usuario.papel}</span>
              </div>

              <div className="perfil-acoes">
                <button className="perfil-btn-primario" onClick={() => setModo('editar')}>
                  Editar dados
                </button>
                <button className="perfil-btn-secundario" onClick={() => setModo('senha')}>
                  Alterar senha
                </button>
              </div>
              <div className="perfil-acoes">
                <button
                  className="perfil-btn-secundario"
                  onClick={() => navigate('/onboarding', { state: { editando: true } })}
                >
                  Editar interesses
                </button>
                <button className="perfil-btn-perigo" onClick={() => setConfirmarExclusao(true)}>
                  Excluir conta
                </button>
              </div>
            </div>
          ) : modo === 'senha' ? (
            <form onSubmit={handleAlterarSenha} className="perfil-form">
              <div className="perfil-field">
                <label>Senha atual</label>
                <input type="password" value={senhaForm.senhaAtual}
                       onChange={(e) => atualizarSenhaCampo('senhaAtual', e.target.value)} required />
              </div>
              <div className="perfil-field">
                <label>Nova senha</label>
                <input type="password" placeholder="Mín. 8 caracteres, letra e número"
                       value={senhaForm.novaSenha}
                       onChange={(e) => atualizarSenhaCampo('novaSenha', e.target.value)} required />
              </div>
              <div className="perfil-field">
                <label>Confirmar nova senha</label>
                <input type="password" value={senhaForm.confirmarSenha}
                       onChange={(e) => atualizarSenhaCampo('confirmarSenha', e.target.value)} required />
              </div>
              <div className="perfil-acoes">
                <button type="button" className="perfil-btn-secundario" onClick={cancelarEdicao} disabled={carregando}>
                  Cancelar
                </button>
                <button type="submit" className="perfil-btn-primario" disabled={carregando}>
                  {carregando ? 'Salvando...' : 'Alterar senha'}
                </button>
              </div>
            </form>
          ) : (
            <form onSubmit={handleSalvar} className="perfil-form">
              <div className="perfil-field">
                <label>Nome completo</label>
                <input type="text" value={form.nome} onChange={(e) => atualizar('nome', e.target.value)} required />
              </div>
              <div className="perfil-field">
                <label>E-mail</label>
                <input type="email" value={form.email} onChange={(e) => atualizar('email', e.target.value)} required />
              </div>

              <div className="perfil-acoes">
                <button type="button" className="perfil-btn-secundario" onClick={cancelarEdicao} disabled={carregando}>
                  Cancelar
                </button>
                <button type="submit" className="perfil-btn-primario" disabled={carregando}>
                  {carregando ? 'Salvando...' : 'Salvar'}
                </button>
              </div>
            </form>
          )}
        </div>

        {confirmarExclusao && (
          <div className="perfil-modal-bg" onClick={() => !carregando && setConfirmarExclusao(false)}>
            <div className="perfil-modal" onClick={(e) => e.stopPropagation()}>
              <h2>Excluir conta</h2>
              <p>Esta ação é irreversível. Sua conta e seus dados serão removidos permanentemente.</p>
              <div className="perfil-acoes">
                <button className="perfil-btn-secundario" onClick={() => setConfirmarExclusao(false)} disabled={carregando}>
                  Cancelar
                </button>
                <button className="perfil-btn-perigo" onClick={handleRemover} disabled={carregando}>
                  {carregando ? 'Removendo...' : 'Sim, excluir'}
                </button>
              </div>
            </div>
          </div>
        )}
      </main>
    </div>
  )
}
