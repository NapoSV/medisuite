import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../auth/useAuth';
import LoadingSpinner from '../components/LoadingSpinner';

export default function ProtectedRoute({ roles }: { roles?: string[] }) {
  const { user, loading } = useAuth();
  if (loading) return <LoadingSpinner fullScreen label="Cargando..." />;
  if (!user) return <Navigate to="/login" replace />;
  if (roles && !roles.includes(user.role)) return <Navigate to="/403" replace />;
  return <Outlet />;
}
