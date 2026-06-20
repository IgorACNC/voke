import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { login, cadastrar } from '../services/authService'
import { temPreferencias } from '../services/sugestaoService'
import type { Papel } from '../types/auth'
import './Auth.css'

type Modo = 'login' | 'cadastro'

function formatarCpf(valor: string): string {
  const nums = valor.replace(/\D/g, '').slice(0, 11)
  return nums
    .replace(/(\d{3})(\d)/, '$1.$2')
    .replace(/(\d{3})(\d)/, '$1.$2')
    .replace(/(\d{3})(\d{1,2})$/, '$1-$2')
}

export default function Auth() {
  const navigate = useNavigate()
  const { salvarSessao } = useAuth()

  const [modo, setModo] = useState<Modo>('login')
  const [papel, setPapel] = useState<Papel>('PARTICIPANTE')
  const [carregando, setCarregando] = useState(false)
  const [erro, setErro] = useState('')

  const [form, setForm] = useState({
    nome: '', cpf: '', email: '', senha: '', confirmarSenha: '', dataNascimento: '',
  })

  function atualizar(campo: string, valor: string) {
    setErro('')
    setForm((f) => ({ ...f, [campo]: campo === 'cpf' ? formatarCpf(valor) : valor }))
  }

  function trocarModo(novoModo: Modo) {
    setModo(novoModo)
    setErro('')
    setForm({ nome: '', cpf: '', email: '', senha: '', confirmarSenha: '', dataNascimento: '' })
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    setErro('')
    if (modo === 'cadastro' && form.senha !== form.confirmarSenha) {
      setErro('As senhas não conferem.')
      return
    }
    setCarregando(true)
    try {
      const resposta = modo === 'login'
        ? await login({ email: form.email, senha: form.senha })
        : await cadastrar({ nome: form.nome, cpf: form.cpf, email: form.email, senha: form.senha, dataNascimento: form.dataNascimento }, papel)
      salvarSessao(resposta)
      if (resposta.papel === 'ADMIN') {
        navigate('/admin')
      } else if (resposta.papel === 'PARTICIPANTE') {
        const configurado = await temPreferencias(resposta.id).catch(() => true)
        navigate(configurado ? '/dashboard' : '/onboarding')
      } else {
        navigate('/dashboard')
      }
    } catch (err: unknown) {
      const msg = (err as { response?: { data?: { mensagem?: string } } })?.response?.data?.mensagem
        ?? 'Não foi possível entrar. Confira o e-mail e a senha.'
      setErro(msg)
    } finally {
      setCarregando(false)
    }
  }

  const ctaLabel = carregando
    ? 'Aguarde…'
    : modo === 'login'
      ? 'Entrar'
      : papel === 'PARTICIPANTE' ? 'Criar conta de participante' : 'Criar conta de organizador'

  return (
    <div className="auth">
      <aside className="auth-stage" aria-hidden="true">
        <Link to="/" className="auth-stage__brand t-mega tone-on-ink">VOKE</Link>
        <p className="auth-stage__line t-h2 tone-on-ink-soft">
          Onde o tempo do evento começa a contar.
        </p>
        <span className="auth-stage__time t-time tone-on-ink-soft">
          ABRINDO PORTAS
        </span>
      </aside>

      <section className="auth-form-wrap">
        <header className="auth-formhead">
          <p className="t-eyebrow tone-hush">{modo === 'login' ? 'Bem-vindo de volta' : 'Comece agora'}</p>
          <h1 className="t-display">{modo === 'login' ? 'Entrar' : 'Criar conta'}</h1>
        </header>

        <div className="auth-tabs" role="tablist" aria-label="Modo de acesso">
          <button
            type="button"
            role="tab"
            aria-selected={modo === 'login'}
            className={`auth-tabs__tab ${modo === 'login' ? 'is-active' : ''}`}
            onClick={() => trocarModo('login')}
          >
            Entrar
          </button>
          <button
            type="button"
            role="tab"
            aria-selected={modo === 'cadastro'}
            className={`auth-tabs__tab ${modo === 'cadastro' ? 'is-active' : ''}`}
            onClick={() => trocarModo('cadastro')}
          >
            Criar conta
          </button>
        </div>

        {modo === 'cadastro' && (
          <fieldset className="auth-papel" aria-label="Tipo de conta">
            <legend className="sr-only">Tipo de conta</legend>
            <label className={`auth-papel__opt ${papel === 'PARTICIPANTE' ? 'is-active' : ''}`}>
              <input
                type="radio"
                name="papel"
                value="PARTICIPANTE"
                checked={papel === 'PARTICIPANTE'}
                onChange={() => setPapel('PARTICIPANTE')}
              />
              <span className="t-h3">Participante</span>
              <span className="t-meta tone-hush">Inscrever-se em eventos.</span>
            </label>
            <label className={`auth-papel__opt ${papel === 'ORGANIZADOR' ? 'is-active' : ''}`}>
              <input
                type="radio"
                name="papel"
                value="ORGANIZADOR"
                checked={papel === 'ORGANIZADOR'}
                onChange={() => setPapel('ORGANIZADOR')}
              />
              <span className="t-h3">Organizador</span>
              <span className="t-meta tone-hush">Publicar e gerir eventos.</span>
            </label>
          </fieldset>
        )}

        <form onSubmit={handleSubmit} className="auth-form" noValidate>
          {modo === 'cadastro' && (
            <>
              <div className="auth-field">
                <label htmlFor="f-nome" className="t-meta">Nome completo</label>
                <input
                  id="f-nome" type="text" placeholder="Como aparece no seu documento"
                  value={form.nome} onChange={(e) => atualizar('nome', e.target.value)} required
                />
              </div>
              <div className="auth-field">
                <label htmlFor="f-cpf" className="t-meta">CPF</label>
                <input
                  id="f-cpf" type="text" placeholder="000.000.000-00"
                  value={form.cpf} onChange={(e) => atualizar('cpf', e.target.value)} required
                  inputMode="numeric"
                />
              </div>
            </>
          )}

          <div className="auth-field">
            <label htmlFor="f-email" className="t-meta">E-mail</label>
            <input
              id="f-email" type="email" placeholder="seu@email.com"
              value={form.email} onChange={(e) => atualizar('email', e.target.value)} required
              autoComplete="email"
            />
          </div>

          <div className="auth-field">
            <label htmlFor="f-senha" className="t-meta">Senha</label>
            <input
              id="f-senha" type="password"
              placeholder={modo === 'cadastro' ? 'Mín. 8 caracteres, letra e número' : '••••••••'}
              value={form.senha} onChange={(e) => atualizar('senha', e.target.value)} required
              autoComplete={modo === 'login' ? 'current-password' : 'new-password'}
            />
          </div>

          {modo === 'cadastro' && (
            <>
              <div className="auth-field">
                <label htmlFor="f-confirma" className="t-meta">Confirmar senha</label>
                <input
                  id="f-confirma" type="password" placeholder="Repita a senha"
                  value={form.confirmarSenha} onChange={(e) => atualizar('confirmarSenha', e.target.value)} required
                  autoComplete="new-password"
                />
              </div>
              <div className="auth-field">
                <label htmlFor="f-nasc" className="t-meta">Data de nascimento</label>
                <input
                  id="f-nasc" type="date"
                  value={form.dataNascimento} onChange={(e) => atualizar('dataNascimento', e.target.value)} required
                />
              </div>
            </>
          )}

          {erro && (
            <p className="auth-erro t-meta" role="alert">{erro}</p>
          )}

          <button type="submit" className="btn btn--primary btn--lg auth-submit" disabled={carregando}>
            {ctaLabel}
          </button>

          {modo === 'login' && (
            <p className="t-meta auth-hint">
              <Link to="/esqueci-senha" className="auth-link">Esqueci minha senha</Link>
            </p>
          )}
        </form>

        <p className="auth-foot t-meta tone-hush">
          <Link to="/catalogo" className="auth-link">Explorar eventos sem fazer login →</Link>
        </p>
      </section>
    </div>
  )
}
