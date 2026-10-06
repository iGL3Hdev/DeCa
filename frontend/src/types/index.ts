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

export type EstadoTransporte = 'BORRADOR' | 'GENERADO'

export interface Empresa {
  id: number
  nombre: string
  nif: string
  domicilio: string | null
  telefono: string | null
}

export interface EmpresaRequest {
  nombre: string
  nif: string
  domicilio?: string | null
  telefono?: string | null}

export interface EmpresaResumen {
  id: number
  nombre: string
  nif: string
}

export interface VehiculoResumen {
  id: number
  matricula: string
  tipo: string
}

export interface Transporte {
  id: number
  cargador: EmpresaResumen
  transportista: EmpresaResumen
  vehiculo: VehiculoResumen
  fechaOperacion: string
  lugarCarga: string
  fechaCarga: string
  lugarDescarga: string
  fechaDescarga: string
  mercanciaNaturaleza: string
  mercanciaPeso: number
  mercanciaUnidad: string
  notas: string | null
  estado: EstadoTransporte
}

export interface TransporteRequest {
  cargadorId: number
  transportistaId: number
  vehiculoId: number
  fechaOperacion: string
  lugarCarga: string
  fechaCarga: string
  lugarDescarga: string
  fechaDescarga: string
  mercanciaNaturaleza: string
  mercanciaPeso: number
  mercanciaUnidad: string
  notas?: string | null
}

export interface DocumentoDeca {
  id: number
  transporteId: number
  urlPublica: string
  fechaCreacion: string | null
  fechaModificacion: string | null
  version: number
}
