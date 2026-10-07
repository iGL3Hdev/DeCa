import { useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { Table } from '../components/Table'
import { transportesApi } from '../api/transportes'
import { documentosApi } from '../api/documentos'
import { ApiError } from '../api/client'
import type { Transporte } from '../types'

export function Dashboard() {
  const navigate = useNavigate()
  const [transportes, setTransportes] = useState<Transporte[]>([])
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [generando, setGenerando] = useState<number | null>(null)

  function cargar() {
    setCargando(true)
    transportesApi
      .listar()
      .then(setTransportes)
      .catch(() => setError('No se pudieron cargar los transportes'))
      .finally(() => setCargando(false))
  }

  useEffect(() => {
    cargar()
  }, [])

  async function generarDeca(transporteId: number) {
    setError(null)
    setGenerando(transporteId)
    try {
      const documento = await documentosApi.generar(transporteId)
      navigate(`/documentos/${documento.id}`)
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'Error al generar el DeCA')
      cargar()
    } finally {
      setGenerando(null)
    }
  }

  return (
    <div>
      <div className="cabecera-pagina">
        <h1>Transportes</h1>
        <div className="cabecera-pagina__accion">
          <Link to="/transportes/nuevo">
            <button type="button" className="primario">+ Nuevo transporte</button>
          </Link>
        </div>
      </div>

      {error && <p className="mensaje-error">{error}</p>}

      <div className="card">
        {cargando ? (
          <p className="texto-muted">Cargando...</p>
        ) : (
          <Table
            datos={transportes}
            claveFila={(t) => t.id}
            vacio="No hay transportes todavía"
            columnas={[
              { encabezado: 'Fecha', render: (t) => t.fechaOperacion },
              { encabezado: 'Cargador', render: (t) => t.cargador.nombre },
              { encabezado: 'Transportista', render: (t) => t.transportista.nombre },
              { encabezado: 'Vehículo', render: (t) => t.vehiculo.matricula },
              {
                encabezado: 'Estado',
                render: (t) => (
                  <span className={`badge badge-${t.estado.toLowerCase()}`}>{t.estado}</span>
                ),
              },
              {
                encabezado: 'Acciones',
                render: (t) =>
                  t.estado === 'BORRADOR' ? (
                    <button
                      type="button"
                      className="primario"
                      disabled={generando === t.id}
                      onClick={() => generarDeca(t.id)}
                    >
                      {generando === t.id ? 'Generando...' : 'Generar DeCA'}
                    </button>
                  ) : (
                    <Link to={`/documentos/por-transporte/${t.id}`}>
                      <button type="button" className="secundario">Ver documento</button>
                    </Link>
                  ),
              },
            ]}
          />
        )}
      </div>
    </div>
  )
}
