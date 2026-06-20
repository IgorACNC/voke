import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { esqueciSenha } from '../services/authService'
import './Auth.css'

export default function EsqueciSenha() {
  const navigate = useNavigate()
  const [email, setEmail] = useState('')
  const [carregando, setCarregando] = useState(false)
  const [erro, setErro] = useState('')
  const [enviado, setEnviado] = useState(false)

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    setErro('')
    setCarregando(true)
    try {
      await esqueciSenha(email)
      setEnviado(true)
    } catch (err: unknown) {
      const msg = (err as { response?: { data?: { mensagem?: string } } })?.response?.data?.mensagem
        ?? 'Não foi possível enviar o link de recuperação.'
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
          <span className="auth-logo-sub">Recuperar Senha</span>
        </div>

        {enviado ? (
          <div className="auth-form">
            <p style={{ color: '#065f46', background: '#ecfdf5', border: '1px solid #a7f3d0',
                        padding: '1rem', borderRadius: 10, textAlign: 'center' }}>
              Se este e-mail estiver cadastrado, enviaremos um link para redefinir sua senha.
            </p>
            <button className="auth-btn-submit" type="button" onClick={() => navigate('/login')}>
              Voltar para o login
            </button>
          </div>
        ) : (
          <form onSubmit={handleSubmit} className="auth-form">
            <p style={{ color: '#6b7280', fontSize: '0.9rem', textAlign: 'center', margin: 0 }}>
              Informe seu e-mail cadastrado para receber um link de redefinição de senha.
            </p>
            <div className="auth-field">
              <label>E-mail</label>
              <input type="email" placeholder="seu@email.com" value={email}
                     onChange={(e) => { setErro(''); setEmail(e.target.value) }} required />
            </div>

            {erro && <p className="auth-erro">{erro}</p>}

            <button type="submit" className="auth-btn-submit" disabled={carregando}>
              {carregando ? 'Enviando...' : 'Enviar link'}
            </button>
            <p className="auth-hint" style={{ textAlign: 'center' }}>
              <Link to="/login" style={{ color: '#9333ea', textDecoration: 'none' }}>Voltar para o login</Link>
            </p>
          </form>
        )}
      </div>
    </div>
  )
}
