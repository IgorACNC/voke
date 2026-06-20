import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import './Header.css'

interface Props {
  /** Texto sobre o eyebrow central (entre logo e perfil). Geralmente um EventClock ou string utilitária. */
  eyebrow?: React.ReactNode
  /** Quando true, header fica transparente sobre fundo escuro (para hero). */
  transparent?: boolean
}

export default function Header({ eyebrow, transparent }: Props) {
  const navigate = useNavigate()
  const { usuario, sair, estaAutenticado } = useAuth()

  function handleSair() {
    sair()
    navigate('/login')
  }

  function handleHome() {
    navigate(estaAutenticado ? '/dashboard' : '/')
  }

  return (
    <header className={`v-header ${transparent ? 'v-header--transparent' : ''}`}>
      <div className="v-header__inner">
        <button type="button" className="v-header__logo" onClick={handleHome} aria-label="Página inicial Voke">
          <span className="v-header__logo-mark">VOKE</span>
        </button>

        <div className="v-header__eyebrow">
          {eyebrow}
        </div>

        <div className="v-header__profile">
          {estaAutenticado ? (
            <>
              <span className="v-header__role t-eyebrow">{usuario?.papel}</span>
              <span className="v-header__name t-meta">{usuario?.nome}</span>
              <button type="button" className="btn btn--ghost btn--sm v-header__action" onClick={handleSair}>
                Sair
              </button>
            </>
          ) : (
            <>
              <button type="button" className="btn btn--ghost btn--sm v-header__action" onClick={() => navigate('/login')}>
                Entrar
              </button>
              <button type="button" className="btn btn--primary btn--sm v-header__action" onClick={() => navigate('/login')}>
                Criar conta
              </button>
            </>
          )}
        </div>
      </div>
    </header>
  )
}
