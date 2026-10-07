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


  if (cargando) return <p className="texto-muted">Cargando...</p>
  if (error || !documento) return <p className="mensaje-error">{error ?? 'Documento no encontrado'}</p>

  const urlPdf = `/api/deca/${documento.urlPublica}`
  const urlCompleta = `${window.location.origin}${urlPdf}`

  async function copiaUrl() {
    await navigator.clipboard.writeText(urlCompleta)
    setCopiado(true)
    setTimeout(() => setCopiado(false), 2000)
  }

  return (
    <div>
      <div style={{ marginBottom: 20 }}>
        <Link to="/">
          <button type="button" className="secundario">← Volver al Dashboard</button>
        </Link>
      </div>

      <h1>Documento <span className="sin-mayusculas">DeCA</span></h1>

      <div className="card">
        <div className="ficha-grid">
          <div className="ficha-dato">
            <label>Transporte</label>
            <p>nº {documento.transporteId}</p>
          </div>
          <div className="ficha-dato">
            <label>Fecha de creación</label>
            <p>{documento.fechaCreacion ? new Date(documento.fechaCreacion).toLocaleString() : '—'}</p>
          </div>
          <div className="ficha-dato">
            <label>Versión</label>
            <p>{documento.version}</p>
          </div>
        </div>

        <div className="fila-botones">
          <a href={urlPdf} target="_blank" rel="noreferrer">
            <button type="button" className="primario">Descargar PDF</button>
          </a>
          <button type="button" className="secundario" onClick={copiaUrl}>
            {copiado ? 'URL copiada ✓' : 'Copiar URL pública'}
          </button>
        </div>

        <p className="url-box">{urlCompleta}</p>
      </div>

      <div className="card card--sin-padding">
        <iframe src={urlPdf} title="Vista previa del DeCA" className="pdf-preview" />
      </div>
    </div>
  )
}