import { Outlet, NavLink } from 'react-router-dom';
import { useAuth } from '../auth/useAuth';

export default function AppLayout() {
    const { user, logout } = useAuth();
    return (
        <div className="min-h-screen flex">
            <aside className="w-64 bg-slate-900 text-white p-4">
                <h1 className="text-xl font-bold mb-8">MediSuite</h1>
                <nav className="flex flex-col gap-2">
                    <NavLink to="/dashboard" className="px-3 py-2 rounded hover:bg-slate-700">Dashboard</NavLink>
                    <NavLink to="/pacientes" className="px-3 py-2 rounded hover:bg-slate-700">Pacientes</NavLink>
                    <NavLink to="/citas"     className="px-3 py-2 rounded hover:bg-slate-700">Citas</NavLink>
                    {user?.role === 'ADMIN' && (
                        <NavLink to="/doctores" className="px-3 py-2 rounded hover:bg-slate-700">Doctores</NavLink>
                    )}
                </nav>
            </aside>
            <main className="flex-1 bg-slate-50">
                <header className="flex justify-between items-center bg-white border-b px-6 py-3">
                    <span>{user?.fullName}</span>
                    <button onClick={logout} className="text-sm text-red-600">Cerrar sesion</button>
                </header>
                <section className="p-6"><Outlet /></section>
            </main>
        </div>
    );
}