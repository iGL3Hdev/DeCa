import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { Table } from '../components/Table'
import { transportesApi } from '../api/transportes'
import type { Transporte } from '../types'

export function Dashboard() {
  const [transportes, setTransportes] = useState<Transporte[]>([])
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    transportesApi
      .listar()
      .then(setTransportes)
      .catch(() => setError('No se pudieron cargar los transportes'))
      .finally(() => setCargando(false))
  }, [])

  return (
    <div>
      <h1>Transportes</h1>

      <Link to="/transportes/nuevo">
        <button type="button">Nuevo transporte</button>
      </Link>

      {error && <p style={{ color: 'red' }}>{error}</p>}

      {cargando ? (
        <p>Cargando...</p>
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
            { encabezado: 'Estado', render: (t) => t.estado },
          ]}
        />
      )}
    </div>
  )
}
