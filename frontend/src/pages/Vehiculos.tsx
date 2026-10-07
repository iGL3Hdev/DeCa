import { useEffect, useState } from 'react'
import { Table } from '../components/Table'
import { vehiculosApi } from '../api/vehiculos'
import { ApiError } from '../api/client'
import type { Vehiculo, VehiculoRequest } from '../types'

const FORM_INICIAL: VehiculoRequest = { matricula: '', tipo: '', matriculaRemolque: '' }

export function Vehiculos() {
  const [vehiculos, setVehiculos] = useState<Vehiculo[]>([])
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [form, setForm] = useState<VehiculoRequest>(FORM_INICIAL)

  async function cargar() {
    setCargando(true)
    setError(null)
    try {
      const datos = await vehiculosApi.listar()
      setVehiculos(datos)
    } catch {
      setError('No se pudieron cargar los vehículos')
    } finally {
      setCargando(false)
    }
  }

  useEffect(() => {
    cargar()
  }, [])

  async function crear(e: React.FormEvent) {
    e.preventDefault()
    setError(null)
    try {
      await vehiculosApi.crear(form)
      setForm(FORM_INICIAL)
      await cargar()
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'Error al crear el vehículo')
    }
  }

  async function darDeBaja(id: number) {
    try {
      await vehiculosApi.darDeBaja(id)
      await cargar()
    } catch {
      setError('No se pudo dar de baja el vehículo')
    }
  }

  return (
    <div>
      <h1>Vehículos</h1>

      {error && <p className="mensaje-error">{error}</p>}

      <div className="card">
        <form onSubmit={crear} className="form-inline">
          <div className="campo">
            <label>Matrícula</label>
            <input
              placeholder="1234ABC"
              value={form.matricula}
              onChange={(e) => setForm({ ...form, matricula: e.target.value })}
              required
            />
          </div>
          <div className="campo">
            <label>Tipo</label>
            <input
              placeholder="Tractora"
              value={form.tipo}
              onChange={(e) => setForm({ ...form, tipo: e.target.value })}
              required
            />
          </div>
          <div className="campo">
            <label>Matrícula remolque</label>
            <input
              placeholder="Opcional"
              value={form.matriculaRemolque ?? ''}
              onChange={(e) => setForm({ ...form, matriculaRemolque: e.target.value })}
            />
          </div>
          <button type="submit" className="primario">Añadir vehículo</button>
        </form>
      </div>

      <div className="card">
        {cargando ? (
          <p className="texto-muted">Cargando...</p>
        ) : (
          <Table
            datos={vehiculos}
            claveFila={(v) => v.id}
            vacio="No hay vehículos todavía"
            columnas={[
              { encabezado: 'Matrícula', render: (v) => v.matricula },
              { encabezado: 'Tipo', render: (v) => v.tipo },
              { encabezado: 'Remolque', render: (v) => v.matriculaRemolque ?? '—' },
              {
                encabezado: 'Estado',
                render: (v) => (
                  <span className={v.activo ? 'badge badge-generado' : 'badge badge-borrador'}>
                    {v.activo ? 'Activo' : 'Baja'}
                  </span>
                ),
              },
              {
                encabezado: 'Acciones',
                render: (v) =>
                  v.activo && (
                    <button className="secundario" onClick={() => darDeBaja(v.id)}>
                      Dar de baja
                    </button>
                  ),
              },
            ]}
          />
        )}
      </div>
    </div>
  )
}

