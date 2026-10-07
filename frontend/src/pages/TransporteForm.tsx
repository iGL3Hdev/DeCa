import { useState } from 'react'
import { useForm, Controller } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { useNavigate } from 'react-router-dom'
import { SelectAsync } from '../components/Select'
import { EmpresaModal } from '../components/EmpresaModal'
import { empresasApi } from '../api/empresas'
import { vehiculosApi } from '../api/vehiculos'
import { transportesApi } from '../api/transportes'
import { ApiError } from '../api/client'
import type { Empresa } from '../types'

const schema = z
  .object({
    cargadorId: z.number({ message: 'Selecciona un cargador' }),
    transportistaId: z.number({ message: 'Selecciona un transportista' }),
    vehiculoId: z.number({ message: 'Selecciona un vehículo' }),
    fechaOperacion: z.string().min(1, 'Obligatoria'),
    lugarCarga: z.string().min(1, 'Obligatorio'),
    fechaCarga: z.string().min(1, 'Obligatoria'),
    lugarDescarga: z.string().min(1, 'Obligatorio'),
    fechaDescarga: z.string().min(1, 'Obligatoria'),
    mercanciaNaturaleza: z.string().min(1, 'Obligatoria'),
    mercanciaPeso: z.number({ message: 'Indica el peso' }).positive('Debe ser mayor que 0'),
    mercanciaUnidad: z.string().min(1, 'Obligatoria'),
    notas: z.string().optional(),
  })
  .refine((datos) => datos.fechaCarga <= datos.fechaDescarga, {
    message: 'La fecha de carga no puede ser posterior a la de descarga',
    path: ['fechaDescarga'],
  })

type FormValues = z.infer<typeof schema>

export function TransporteForm() {
  const navigate = useNavigate()
  const [modalAbierto, setModalAbierto] = useState<'cargador' | 'transportista' | null>(null)
  const [recargarEmpresas, setRecargarEmpresas] = useState(0)
  const [errorEnvio, setErrorEnvio] = useState<string | null>(null)

  const {
    register,
    handleSubmit,
    control,
    setValue,
    formState: { errors, isSubmitting },
  } = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: { mercanciaUnidad: 'TM' },
  })

  async function cargarEmpresas() {
    const empresas = await empresasApi.listar()
    return empresas.map((e) => ({ value: e.id, label: `${e.nombre} (${e.nif})` }))
  }

  async function cargarVehiculos() {
    const vehiculos = await vehiculosApi.listar(true)
    return vehiculos.map((v) => ({ value: v.id, label: `${v.matricula} — ${v.tipo}` }))
  }

  function onEmpresaCreada(empresa: Empresa) {
    if (modalAbierto === 'cargador') setValue('cargadorId', empresa.id)
    if (modalAbierto === 'transportista') setValue('transportistaId', empresa.id)
    setModalAbierto(null)
    setRecargarEmpresas((n) => n + 1)
  }

  async function onSubmit(datos: FormValues) {
    setErrorEnvio(null)
    try {
      await transportesApi.crear(datos)
      navigate('/')
    } catch (err) {
      setErrorEnvio(err instanceof ApiError ? err.message : 'Error al guardar el transporte')
    }
  }

  return (
    <div>
      <h1>Nuevo transporte</h1>

      {errorEnvio && <p className="mensaje-error">{errorEnvio}</p>}

      <form onSubmit={handleSubmit(onSubmit)}>
        <div className="card">
          <h2>Partes del transporte</h2>
          <div className="form-grid">
            <div className="campo">
              <label>Cargador</label>
              <div className="campo-con-boton">
                <Controller
                  name="cargadorId"
                  control={control}
                  render={({ field }) => (
                    <SelectAsync
                      cargarOpciones={cargarEmpresas}
                      value={field.value}
                      onChange={field.onChange}
                      recargarSenal={recargarEmpresas}
                    />
                  )}
                />
                <button type="button" className="secundario" onClick={() => setModalAbierto('cargador')}>
                  + Nueva
                </button>
              </div>
              {errors.cargadorId && <p className="error-campo">{errors.cargadorId.message}</p>}
            </div>

            <div className="campo">
              <label>Transportista</label>
              <div className="campo-con-boton">
                <Controller
                  name="transportistaId"
                  control={control}
                  render={({ field }) => (
                    <SelectAsync
                      cargarOpciones={cargarEmpresas}
                      value={field.value}
                      onChange={field.onChange}
                      recargarSenal={recargarEmpresas}
                    />
                  )}
                />
                <button type="button" className="secundario" onClick={() => setModalAbierto('transportista')}>
                  + Nueva
                </button>
              </div>
              {errors.transportistaId && (
                <p className="error-campo">{errors.transportistaId.message}</p>
              )}
            </div>

            <div className="campo campo-full">
              <label>Vehículo</label>
              <Controller
                name="vehiculoId"
                control={control}
                render={({ field }) => (
                  <SelectAsync
                    cargarOpciones={cargarVehiculos}
                    value={field.value}
                    onChange={field.onChange}
                  />
                )}
              />
              {errors.vehiculoId && <p className="error-campo">{errors.vehiculoId.message}</p>}
            </div>
          </div>
        </div>

        <div className="card">
          <h2>Fechas y lugares</h2>
          <div className="form-grid">
            <div className="campo campo-full">
              <label>Fecha operación</label>
              <input type="date" {...register('fechaOperacion')} />
              {errors.fechaOperacion && <p className="error-campo">{errors.fechaOperacion.message}</p>}
            </div>

            <div className="campo">
              <label>Lugar de carga</label>
              <input placeholder="Madrid" {...register('lugarCarga')} />
              {errors.lugarCarga && <p className="error-campo">{errors.lugarCarga.message}</p>}
            </div>

            <div className="campo">
              <label>Fecha de carga</label>
              <input type="date" {...register('fechaCarga')} />
              {errors.fechaCarga && <p className="error-campo">{errors.fechaCarga.message}</p>}
            </div>

            <div className="campo">
              <label>Lugar de descarga</label>
              <input placeholder="Barcelona" {...register('lugarDescarga')} />
              {errors.lugarDescarga && <p className="error-campo">{errors.lugarDescarga.message}</p>}
            </div>

            <div className="campo">
              <label>Fecha de descarga</label>
              <input type="date" {...register('fechaDescarga')} />
              {errors.fechaDescarga && <p className="error-campo">{errors.fechaDescarga.message}</p>}
            </div>
          </div>
        </div>

        <div className="card">
          <h2>Mercancía</h2>
          <div className="form-grid">
            <div className="campo campo-full">
              <label>Naturaleza de la mercancía</label>
              <input placeholder="Textil, maquinaria..." {...register('mercanciaNaturaleza')} />
              {errors.mercanciaNaturaleza && (
                <p className="error-campo">{errors.mercanciaNaturaleza.message}</p>
              )}
            </div>

            <div className="campo">
              <label>Peso</label>
              <input type="number" step="0.01" {...register('mercanciaPeso', { valueAsNumber: true })} />
              {errors.mercanciaPeso && <p className="error-campo">{errors.mercanciaPeso.message}</p>}
            </div>

            <div className="campo">
              <label>Unidad</label>
              <select {...register('mercanciaUnidad')}>
                <option value="TM">TM</option>
                <option value="KG">KG</option>
                <option value="M3">M3</option>
              </select>
            </div>

            <div className="campo campo-full">
              <label>Notas</label>
              <textarea placeholder="Opcional" {...register('notas')} />
            </div>
          </div>
        </div>

        <div className="form-acciones">
          <button type="submit" className="primario" disabled={isSubmitting}>
            {isSubmitting ? 'Guardando...' : 'Guardar borrador'}
          </button>
        </div>
      </form>

      {modalAbierto && (
        <EmpresaModal onCreada={onEmpresaCreada} onCerrar={() => setModalAbierto(null)} />
      )}
    </div>
  )
}
