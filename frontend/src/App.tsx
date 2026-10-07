import { BrowserRouter, Routes, Route, NavLink } from 'react-router-dom'
import { Dashboard } from './pages/Dashboard'
import { Vehiculos } from './pages/Vehiculos'
import { TransporteForm } from './pages/TransporteForm'
import { DocumentoDetalle } from './pages/DocumentoDetalle'
import { DocumentoPorTransporte } from './pages/DocumentoPorTransporte'
import './App.css'

function App() {
  return (
    <BrowserRouter>
      <nav className="app-nav">
        <span className="app-nav__marca">
          <span className="app-nav__marca-acento">DeCA</span> Manager
        </span>
        <div className="app-nav__links">
          <NavLink to="/" end className={({ isActive }) => (isActive ? 'activo' : undefined)}>
            Dashboard
          </NavLink>
          <NavLink to="/vehiculos" className={({ isActive }) => (isActive ? 'activo' : undefined)}>
            Vehículos
          </NavLink>
        </div>
      </nav>
      <main className="app-main">
        <Routes>
          <Route path="/" element={<Dashboard />} />
          <Route path="/vehiculos" element={<Vehiculos />} />
          <Route path="/transportes/nuevo" element={<TransporteForm />} />
          <Route path="/documentos/:id" element={<DocumentoDetalle />} />
          <Route path="/documentos/por-transporte/:transporteId" element={<DocumentoPorTransporte />} />
        </Routes>
      </main>
    </BrowserRouter>
  )
}

export default App

