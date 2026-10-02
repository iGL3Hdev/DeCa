import { api } from './client'
import type { Transporte, TransporteRequest, EstadoTransporte } from '../types'

interface FiltrosTransporte {
  estado?: EstadoTransporte
  fechaDesde?: string
  fechaHasta?: string
}

export const transportesApi = {
  listar: (filtros: FiltrosTransporte = {}) => {
    const params = new URLSearchParams()
    if (filtros.estado) params.set('estado', filtros.estado)
    if (filtros.fechaDesde) params.set('fechaDesde', filtros.fechaDesde)
    if (filtros.fechaHasta) params.set('fechaHasta', filtros.fechaHasta)
    const query = params.toString()
    return api.get<Transporte[]>(`/transportes${query ? `?${query}` : ''}`)
  },

  obtener: (id: number) => api.get<Transporte>(`/transportes/${id}`),

  crear: (datos: TransporteRequest) => api.post<Transporte>('/transportes', datos),

  actualizar: (id: number, datos: TransporteRequest) =>
    api.put<Transporte>(`/transportes/${id}`, datos),
}
