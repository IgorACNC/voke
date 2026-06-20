import axios from 'axios'

const api = axios.create({ baseURL: '/api' })

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('voke_token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

let redirecionandoParaLogin = false

api.interceptors.response.use(
  (r) => r,
  (error) => {
    if (error.response?.status === 401 && !redirecionandoParaLogin) {
      redirecionandoParaLogin = true
      localStorage.removeItem('voke_token')
      localStorage.removeItem('voke_usuario')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default api
