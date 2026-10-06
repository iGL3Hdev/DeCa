import { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import { documentosApi } from '../api/documentos'
import type { DocumentoDeca } from '../types'

export function DocumentoDetalle() {
  const { id } = useParams<{ id: string }>()
  const [documento, setDocumento] = useState<DocumentoDeca | null>(null)
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [copiado, setCopiado] = useState(false)

  useEffect(() => {
    if (!id) return
    documentosApi
      .obtener(Number(id))
      .then(setDocumento)
      .catch(() => setError('No se pudo cargar el documento'))
      .finally(() => setCargando(false))
  }, [id])


  if (cargando) return <p>Cargando...</p>
  if ( error || !documento) return <p style={{ color: 'red'}}>{error ?? 'Documento no encontrado'}</p>

  const urlPdf = `/api/deca/${documento.urlPublica}`
  const urlCompleta = `${window.location.origin}${urlPdf}`

  async function copiaUrl() {
    await navigator.clipboard.writeText(urlCompleta)
    setCopiado(true)
    setTimeout(() => setCopiado(false), 2000)
  }

  return (
    <div>
        <p>
            <Link to="/">← Volver al Dashboard</Link>
        </p>

        <h1>Documento DeCa</h1>

        <p>
            <strong>Transporte:</strong> nº {documento.transporteId}
        </p>
        <p>
            <strong>Fecha de creación:</strong>{' '}
            {documento.fechaCreacion ? new Date(documento.fechaCreacion).toLocaleString() : '-'}
        </p>
        <p>
            <strong>Versión:</strong> {documento.version}
        </p>
        
        <div>
            <a href={urlPdf} target="_blank" rel='noreferrer'>
                <button type='button'>Descargar PDF</button>
            </a>
            <button type='button' onClick={copiaUrl} style={{ marginLeft: 8}}>
                {copiado ? 'URL copiada ✓' : 'Copiar URL pública'}
            </button>
        </div>

        <p style={{ marginTop: 8, fontSize: 13, color: '#555' }}>{urlCompleta}</p>

        <div style={{ marginTop: 16, border: '1px solid #ccc', height: 600}}>
            <iframe src={urlPdf} title="Vista previa del DeCA" width="100%" height="100%" /> 
        </div>
    </div>
  )
}