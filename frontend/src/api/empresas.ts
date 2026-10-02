import { api } from './client'
import type { Empresa, EmpresaRequest } from '../types'

export const empresasApi = {
  listar: () => api.get<Empresa[]>('/empresas'),
  obtener: (id: number) => api.get<Empresa>(`/empresas/${id}`),
  crear: (datos: EmpresaRequest) => api.post<Empresa>('/empresas', datos),
  actualizar: (id: number, datos: EmpresaRequest) => api.put<Empresa>(`/empresas/${id}`, datos),
  eliminar: (id: number) => api.delete<void>(`/empresas/${id}`),
}
