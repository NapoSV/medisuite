import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './auth/useAuth';
import ProtectedRoute from './routes/ProtectedRoute';
import AppLayout from './layouts/AppLayout';
import Login from './pages/LoginPage';
import Dashboard from './pages/DashboardPage';
import Pacientes from './pages/Pacientes';
import Doctores from './pages/Doctores';
import Citas from './pages/Citas';
import CitaNueva from './pages/CitaNueva';
import Expediente from './pages/Expediente';
import Recetas from './pages/Recetas';
import RecetaPrint from './pages/RecetaPrint';
import Perfil from './pages/Perfil';
import Forbidden from './pages/Forbidden';
import Triaje from './pages/Triaje';

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/403" element={<Forbidden />} />
          <Route path="/receta-print/:id" element={<RecetaPrint />} />
          <Route element={<ProtectedRoute />}>
            <Route element={<AppLayout />}>
              <Route path="/" element={<Navigate to="/dashboard" replace />} />
              <Route path="/dashboard" element={<Dashboard />} />
              <Route path="/pacientes" element={<Pacientes />} />
              <Route path="/citas" element={<Citas />} />
              <Route path="/citas/nueva" element={<CitaNueva />} />
              <Route path="/pacientes/:id/expediente" element={<Expediente />} />
              <Route path="/expediente/:id" element={<Expediente />} />
              <Route path="/triaje" element={<Triaje />} />
              <Route path="/recetas/:id" element={<Recetas />} />
              <Route path="/perfil" element={<Perfil />} />
            </Route>
          </Route>
          <Route element={<ProtectedRoute roles={['ADMIN']} />}>
            <Route element={<AppLayout />}>
              <Route path="/doctores" element={<Doctores />} />
            </Route>
          </Route>
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}
