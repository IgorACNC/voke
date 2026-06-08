import api from './api'
import type { Categoria } from './categoriaService'

export async function listarCategoriasAdmin(): Promise<Categoria[]> {
  const { data } = await api.get<Categoria[]>('/admin/categorias')
  return data
}

export async function criarCategoria(nome: string): Promise<Categoria> {
  const { data } = await api.post<Categoria>('/admin/categorias', { nome })
  return data
}

export async function editarCategoria(id: string, nome: string): Promise<void> {
  await api.put(`/admin/categorias/${id}`, { nome })
}

export async function removerCategoria(id: string): Promise<void> {
  await api.delete(`/admin/categorias/${id}`)
}
