import { useState } from "react";
import { empresasApi } from '../api/empresas'
import { ApiError } from '../api/client'
import type { Empresa } from '../types'

interface EmpresaModalProps {
    onCreada: (empresa: Empresa) => void
    onCerrar: () => void
}

export function EmpresaModal({ onCreada, onCerrar }: EmpresaModalProps){
    const [nombre, setNombre] = useState('')
    const [nif, setNif] = useState('')
    const [domicilio, setDomicilio] = useState('')
    const [telefono, setTelefono] = useState('')
    const [error, setError] = useState<string | null>(null)
    const [guardando, setGuardando] = useState(false)

    async function guardar(e: React.FormEvent) {
        e.preventDefault()
        setError(null)
        setGuardando(true)
        try{
            const empresa = await empresasApi.crear({ nombre, nif, domicilio, telefono})
            onCreada(empresa)
        } catch(err) {
            setError(err instanceof ApiError ? err.message : 'Error al crear la empresa')
        } finally {
            setGuardando(false)
        }
    }

    return (
        <div
            style={{
                position: 'fixed',
                inset: 0,
                background: 'rgba(0,0,0,0.4)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
            }}
        >
            <div style={{ background: 'white', padding: 24, borderRadius: 8, minWidth: 320 }}>
                <h2>Nueva empresa</h2>
                <form onSubmit={guardar}>
                    <div>
                        <input 
                            placeholder="Nombre"
                            value={nombre}
                            onChange={(e) => setNombre(e.target.value)}
                            required
                        />
                    </div>
                    <div>
                        <input
                            placeholder="NIF"
                            value={nif}
                            onChange={(e) => setNif(e.target.value)}
                            required
                        />
                    </div>
                    <div>
                        <input
                            placeholder="Domicilio (opcional)"
                            value={domicilio}
                            onChange={(e) => setDomicilio(e.target.value)}
                        />
                    </div>
                    <div>
                        <input
                            placeholder="Teléfono (opcional)"
                            value={telefono}
                            onChange={(e) => setTelefono(e.target.value)}
                        />
                    </div>

                    {error && <p style={{ color: 'red' }}>{error}</p>}

                    <div style={{ marginTop: 12}}>
                        <button type="submit" disabled={guardando}>
                            {guardando ? 'Guardando...' : 'Crear empresa'}
                        </button>
                        <button type="button" onClick={onCerrar} style={{ marginLeft: 8}}>
                            Cancelar
                        </button>
                    </div>
                </form>
            </div>
        </div>
    )
}