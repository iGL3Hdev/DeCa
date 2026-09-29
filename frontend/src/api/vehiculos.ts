import { api } from './client'
import type { Vehiculo, VehiculoRequest } from '../types'

export const vehiculosApi = {
  listar: (activo?: boolean) =>
    api.get<Vehiculo[]>(activo === undefined ? '/vehiculos' : `/vehiculos?activo=${activo}`),

  obtener: (id: number) => api.get<Vehiculo>(`/vehiculos/${id}`),

  crear: (datos: VehiculoRequest) => api.post<Vehiculo>('/vehiculos', datos),

  actualizar: (id: number, datos: VehiculoRequest) =>
    api.put<Vehiculo>(`/vehiculos/${id}`, datos),

  darDeBaja: (id: number) => api.patch<Vehiculo>(`/vehiculos/${id}/baja`),
}
