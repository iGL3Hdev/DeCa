import { BrowserRouter, Routes, Route, Link } from "react-router-dom";
import { Vehiculos } from "./pages/Vehiculos";
import './App.css'

function App() {
  return (
    <BrowserRouter>
      <nav>
        <Link to="/">Dashboard</Link> | <Link to="/vehiculos">Vehículos</Link>
      </nav>
      <Routes>
        <Route path="/" element={<p>Dashboard (pendiente, Fase6)</p>} />
        <Route path="/vehiculos" element={<Vehiculos />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App
