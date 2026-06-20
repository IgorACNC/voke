import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
// Design system — ordem importa: tokens precisam vir antes dos demais.
import './styles/tokens.css'
import './styles/typography.css'
import './styles/elements.css'
import './index.css'
import App from './App'

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <App />
  </StrictMode>
)
