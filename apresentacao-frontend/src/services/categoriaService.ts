import api from './api'

export interface Categoria {
  id: string
  nome: string
}

export async function listarCategorias(): Promise<Categoria[]> {
  const { data } = await api.get<Categoria[]>('/categorias')
  return data
}
