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
        ? await login({ email: form.email, senha: form.senha, papel })
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
        ?? 'Ocorreu um erro. Tente novamente.'
      setErro(msg)
    } finally {
      setCarregando(false)
    }
  }

  return (
    <div className="auth-bg">
      <div className="auth-card">
        <div className="auth-logo">
          <span className="auth-logo-text">Voke</span>
          <span className="auth-logo-sub">Eventos &amp; Experiências</span>
        </div>

        <div className="auth-tabs">
          <button className={modo === 'login' ? 'active' : ''} onClick={() => trocarModo('login')} type="button">Entrar</button>
          <button className={modo === 'cadastro' ? 'active' : ''} onClick={() => trocarModo('cadastro')} type="button">Criar conta</button>
        </div>

        <div className="auth-papel-toggle">
          <button type="button" className={papel === 'PARTICIPANTE' ? 'active' : ''} onClick={() => setPapel('PARTICIPANTE')}>Participante</button>
          <button type="button" className={papel === 'ORGANIZADOR' ? 'active' : ''} onClick={() => setPapel('ORGANIZADOR')}>Organizador</button>
        </div>

        <form onSubmit={handleSubmit} className="auth-form">
          {modo === 'cadastro' && (
            <>
              <div className="auth-field">
                <label>Nome completo</label>
                <input type="text" placeholder="Seu nome completo" value={form.nome} onChange={(e) => atualizar('nome', e.target.value)} required />
              </div>
              <div className="auth-field">
                <label>CPF</label>
                <input type="text" placeholder="000.000.000-00" value={form.cpf} onChange={(e) => atualizar('cpf', e.target.value)} required />
              </div>
            </>
          )}

          <div className="auth-field">
            <label>E-mail</label>
            <input type="email" placeholder="seu@email.com" value={form.email} onChange={(e) => atualizar('email', e.target.value)} required />
          </div>

          <div className="auth-field">
            <label>Senha</label>
            <input type="password" placeholder={modo === 'cadastro' ? 'Mín. 8 caracteres, letra e número' : '••••••••'} value={form.senha} onChange={(e) => atualizar('senha', e.target.value)} required />
          </div>

          {modo === 'cadastro' && (
            <>
              <div className="auth-field">
                <label>Confirmar senha</label>
                <input type="password" placeholder="Repita a senha" value={form.confirmarSenha} onChange={(e) => atualizar('confirmarSenha', e.target.value)} required />
              </div>
              <div className="auth-field">
                <label>Data de nascimento</label>
                <input type="date" value={form.dataNascimento} onChange={(e) => atualizar('dataNascimento', e.target.value)} required />
              </div>
            </>
          )}

          {erro && <p className="auth-erro">{erro}</p>}

          <button type="submit" className="auth-btn-submit" disabled={carregando}>
            {carregando ? 'Aguarde...' : modo === 'login' ? 'Entrar' : papel === 'PARTICIPANTE' ? 'Criar conta de Participante' : 'Criar conta de Organizador'}
          </button>

          {modo === 'login' && (
            <p className="auth-hint" style={{ textAlign: 'center', marginTop: '0.5rem' }}>
              <Link to="/esqueci-senha" style={{ color: '#9333ea', textDecoration: 'none', fontWeight: 500 }}>
                Esqueci minha senha
              </Link>
            </p>
          )}
        </form>

        <p className="auth-hint">
          {papel === 'ORGANIZADOR' ? 'Organizadores criam e gerenciam eventos.' : 'Participantes se inscrevem e curtem eventos.'}
        </p>

        <div style={{ textAlign: 'center', marginTop: '1rem', paddingTop: '1rem', borderTop: '1px solid #eee' }}>
          <Link
            to="/catalogo"
            style={{ color: '#7c6af7', textDecoration: 'none', fontSize: '0.9rem', fontWeight: 500 }}
          >
            Ver eventos sem fazer login →
          </Link>
        </div>
      </div>
    </div>
  )
}
