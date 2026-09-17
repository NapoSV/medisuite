import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './auth/useAuth';
import ProtectedRoute from './routes/ProtectedRoute';
import Login from './pages/LoginPage';
import Dashboard from './pages/DashboardPage';
import Pacientes from './pages/Pacientes';
import Doctores from './pages/Doctores';
import Perfil from './pages/Perfil';
import Forbidden from './pages/Forbidden';

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/403" element={<Forbidden />} />
          <Route element={<ProtectedRoute />}>
            <Route path="/" element={<Navigate to="/dashboard" replace />} />
            <Route path="/dashboard" element={<Dashboard />} />
            <Route path="/pacientes" element={<Pacientes />} />
            <Route path="/perfil" element={<Perfil />} />
          </Route>
          <Route element={<ProtectedRoute roles={['ADMIN']} />}>
            <Route path="/doctores" element={<Doctores />} />
          </Route>
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}