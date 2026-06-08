import { useState } from 'react'
import { Link, useNavigate, useSearchParams } from 'react-router-dom'
import { redefinirSenha } from '../services/authService'
import './Auth.css'

export default function RedefinirSenha() {
  const navigate = useNavigate()
  const [params] = useSearchParams()
  const token = params.get('token') ?? ''

  const [form, setForm] = useState({ novaSenha: '', confirmarSenha: '' })
  const [carregando, setCarregando] = useState(false)
  const [erro, setErro] = useState('')

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    setErro('')
    if (!token) { setErro('Token ausente na URL.'); return }
    if (form.novaSenha !== form.confirmarSenha) {
      setErro('As senhas não conferem.'); return
    }
    setCarregando(true)
    try {
      await redefinirSenha(token, form.novaSenha)
      navigate('/login')
    } catch (err: unknown) {
      const msg = (err as { response?: { data?: { mensagem?: string } } })?.response?.data?.mensagem
        ?? 'Não foi possível redefinir a senha.'
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
          <span className="auth-logo-sub">Redefinir Senha</span>
        </div>

        <form onSubmit={handleSubmit} className="auth-form">
          <div className="auth-field">
            <label>Nova senha</label>
            <input type="password" placeholder="Mín. 8 caracteres, letra e número"
                   value={form.novaSenha}
                   onChange={(e) => { setErro(''); setForm({ ...form, novaSenha: e.target.value }) }} required />
          </div>
          <div className="auth-field">
            <label>Confirmar nova senha</label>
            <input type="password" value={form.confirmarSenha}
                   onChange={(e) => { setErro(''); setForm({ ...form, confirmarSenha: e.target.value }) }} required />
          </div>

          {erro && <p className="auth-erro">{erro}</p>}

          <button type="submit" className="auth-btn-submit" disabled={carregando}>
            {carregando ? 'Redefinindo...' : 'Redefinir senha'}
          </button>
          <p className="auth-hint" style={{ textAlign: 'center' }}>
            <Link to="/login" style={{ color: '#9333ea', textDecoration: 'none' }}>Voltar para o login</Link>
          </p>
        </form>
      </div>
    </div>
  )
}
