import { useEffect, useState } from "react";
import { useParams, Navigate } from "react-router-dom";
import { api } from '../api/client';
import type { DocumentoDeca } from '../types';

export function DocumentoPorTransporte() {
    const { transporteId } = useParams<{ transporteId: string }>()
    const [documento, setDocumento] = useState<DocumentoDeca | 'no-encontrado' | null>(null)

    useEffect(() => {
        if(!transporteId) return
        api
            .get<DocumentoDeca[]>('/documentos')
            .then((documentos) => {
                const encontrado = documentos.find((d) => d.transporteId === Number(transporteId))
                setDocumento(encontrado ?? 'no-encontrado')
            })
    }, [transporteId])

    if (documento === null) return <p>Buscando documento...</p>
    if (documento === 'no-encontrado') return <p>No se encontró el documento de este transporte.</p>
    
    return <Navigate to={`/documentos/${documento.id}`} replace />
}

