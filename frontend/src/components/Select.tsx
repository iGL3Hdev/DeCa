import { useEffect, useState } from 'react'

interface Opcion {
  value: number
  label: string
}

interface SelectAsyncProps {
  cargarOpciones: () => Promise<Opcion[]>
  value: number | undefined
  onChange: (value: number) => void
  placeholder?: string
  recargarSenal?: unknown
}

export function SelectAsync({
  cargarOpciones,
  value,
  onChange,
  placeholder = 'Selecciona...',
  recargarSenal,
}: SelectAsyncProps) {
  const [opciones, setOpciones] = useState<Opcion[]>([])
  const [cargando, setCargando] = useState(true)

  useEffect(() => {
    setCargando(true)
    cargarOpciones()
      .then(setOpciones)
      .finally(() => setCargando(false))
  }, [recargarSenal])

  return (
    <select
      value={value ?? ''}
      onChange={(e) => onChange(Number(e.target.value))}
      disabled={cargando}
    >
      <option value="" disabled>
        {cargando ? 'Cargando...' : placeholder}
      </option>
      {opciones.map((opcion) => (
        <option key={opcion.value} value={opcion.value}>
          {opcion.label}
        </option>
      ))}
    </select>
  )
}

