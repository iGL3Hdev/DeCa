import { BrowserRouter, Routes, Route, Link } from 'react-router-dom'
import { Dashboard } from './pages/Dashboard'
import { Vehiculos } from './pages/Vehiculos'
import { TransporteForm } from './pages/TransporteForm'
import { DocumentoDetalle } from './pages/DocumentoDetalle'
import { DocumentoPorTransporte } from './pages/DocumentoPorTransporte'
import './App.css'

function App() {
  return (
    <BrowserRouter>
      <nav>
        <Link to="/">Dashboard</Link> | <Link to="/vehiculos">Vehículos</Link>
      </nav>
      <Routes>
        <Route path="/" element={<Dashboard />} />
        <Route path="/vehiculos" element={<Vehiculos />} />
        <Route path="/transportes/nuevo" element={<TransporteForm />} />
        <Route path="/documentos/:id" element={<DocumentoDetalle />} />
        <Route path="/documentos/por-transporte/:transporteId" element={<DocumentoPorTransporte />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App

