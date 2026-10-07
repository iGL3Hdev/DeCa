interface Columna<T> {
  encabezado: string
  render: (fila: T) => React.ReactNode
}

interface TableProps<T> {
  columnas: Columna<T>[]
  datos: T[]
  claveFila: (fila: T) => string | number
  vacio?: string
}

export function Table<T>({ columnas, datos, claveFila, vacio = 'Sin datos' }: TableProps<T>) {
  if (datos.length === 0) {
    return <p>{vacio}</p>
  }

  return (
    <table className="tabla">
      <thead>
        <tr>
          {columnas.map((columna) => (
            <th key={columna.encabezado}>{columna.encabezado}</th>
          ))}
        </tr>
      </thead>
      <tbody>
        {datos.map((fila) => (
          <tr key={claveFila(fila)}>
            {columnas.map((columna) => (
              <td key={columna.encabezado}>{columna.render(fila)}</td>
            ))}
          </tr>
        ))}
      </tbody>
    </table>
  )
}
