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
        <div className="modal-overlay">
            <div className="modal-box">
                <h2>Nueva empresa</h2>
                <form onSubmit={guardar}>
                    <div className="campo">
                        <label>Nombre</label>
                        <input
                            placeholder="Transportes Norte SL"
                            value={nombre}
                            onChange={(e) => setNombre(e.target.value)}
                            required
                        />
                    </div>
                    <div className="campo">
                        <label>NIF</label>
                        <input
                            placeholder="B12345678"
                            value={nif}
                            onChange={(e) => setNif(e.target.value)}
                            required
                        />
                    </div>
                    <div className="campo">
                        <label>Domicilio</label>
                        <input
                            placeholder="Opcional"
                            value={domicilio}
                            onChange={(e) => setDomicilio(e.target.value)}
                        />
                    </div>
                    <div className="campo">
                        <label>Teléfono</label>
                        <input
                            placeholder="Opcional"
                            value={telefono}
                            onChange={(e) => setTelefono(e.target.value)}
                        />
                    </div>

                    {error && <p className="mensaje-error">{error}</p>}

                    <div className="modal-acciones">
                        <button type="submit" className="primario" disabled={guardando}>
                            {guardando ? 'Guardando...' : 'Crear empresa'}
                        </button>
                        <button type="button" className="secundario" onClick={onCerrar}>
                            Cancelar
                        </button>
                    </div>
                </form>
            </div>
        </div>
    )
}