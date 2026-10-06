import { api } from './client'
import type { DocumentoDeca } from '../types'

export const documentosApi = {
    listar: () => api.get<DocumentoDeca[]>('/documentos'),
    obtener: (id: number) => api.get<DocumentoDeca>(`/documentos/${id}`),
    generar: (transporteId: number) => 
        api.post<DocumentoDeca>(`/transportes/${transporteId}/generar-deca`, {}),
}