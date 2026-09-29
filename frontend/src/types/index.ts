export interface Vehiculo {
  id: number
  matricula: string
  tipo: string
  matriculaRemolque: string | null
  activo: boolean
}

export interface VehiculoRequest {
  matricula: string
  tipo: string
  matriculaRemolque?: string | null
}
