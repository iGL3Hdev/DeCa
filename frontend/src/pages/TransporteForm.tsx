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

      <form onSubmit={handleSubmit(onSubmit)}>
        <div>
          <label>Cargador</label>
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
          <button type="button" onClick={() => setModalAbierto('cargador')}>
            + Nueva empresa
          </button>
          {errors.cargadorId && <p style={{ color: 'red' }}>{errors.cargadorId.message}</p>}
        </div>

        <div>
          <label>Transportista</label>
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
          <button type="button" onClick={() => setModalAbierto('transportista')}>
            + Nueva empresa
          </button>
          {errors.transportistaId && (
            <p style={{ color: 'red' }}>{errors.transportistaId.message}</p>
          )}
        </div>

        <div>
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
          {errors.vehiculoId && <p style={{ color: 'red' }}>{errors.vehiculoId.message}</p>}
        </div>

        <div>
          <label>Fecha operación</label>
          <input type="date" {...register('fechaOperacion')} />
          {errors.fechaOperacion && <p style={{ color: 'red' }}>{errors.fechaOperacion.message}</p>}
        </div>

        <div>
          <label>Lugar de carga</label>
          <input {...register('lugarCarga')} />
          {errors.lugarCarga && <p style={{ color: 'red' }}>{errors.lugarCarga.message}</p>}
        </div>

        <div>
          <label>Fecha de carga</label>
          <input type="date" {...register('fechaCarga')} />
          {errors.fechaCarga && <p style={{ color: 'red' }}>{errors.fechaCarga.message}</p>}
        </div>

        <div>
          <label>Lugar de descarga</label>
          <input {...register('lugarDescarga')} />
          {errors.lugarDescarga && <p style={{ color: 'red' }}>{errors.lugarDescarga.message}</p>}
        </div>

        <div>
          <label>Fecha de descarga</label>
          <input type="date" {...register('fechaDescarga')} />
          {errors.fechaDescarga && <p style={{ color: 'red' }}>{errors.fechaDescarga.message}</p>}
        </div>

        <div>
          <label>Naturaleza de la mercancía</label>
          <input {...register('mercanciaNaturaleza')} />
          {errors.mercanciaNaturaleza && (
            <p style={{ color: 'red' }}>{errors.mercanciaNaturaleza.message}</p>
          )}
        </div>

        <div>
          <label>Peso</label>
          <input type="number" step="0.01" {...register('mercanciaPeso', { valueAsNumber: true })} />
          {errors.mercanciaPeso && <p style={{ color: 'red' }}>{errors.mercanciaPeso.message}</p>}
        </div>

        <div>
          <label>Unidad</label>
          <select {...register('mercanciaUnidad')}>
            <option value="TM">TM</option>
            <option value="KG">KG</option>
            <option value="M3">M3</option>
          </select>
        </div>

        <div>
          <label>Notas</label>
          <textarea {...register('notas')} />
        </div>

        {errorEnvio && <p style={{ color: 'red' }}>{errorEnvio}</p>}

        <button type="submit" disabled={isSubmitting}>
          {isSubmitting ? 'Guardando...' : 'Guardar borrador'}
        </button>
      </form>

      {modalAbierto && (
        <EmpresaModal onCreada={onEmpresaCreada} onCerrar={() => setModalAbierto(null)} />
      )}
    </div>
  )
}
