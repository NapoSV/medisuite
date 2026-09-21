import { Outlet, NavLink } from 'react-router-dom';
import { useAuth } from '../auth/useAuth';
import Logo from '../components/Logo';
import { LogOut, LayoutDashboard, Users, CalendarDays, Stethoscope, UserCircle, CalendarPlus, ClipboardList } from 'lucide-react';

type Role = 'ADMIN' | 'DOCTOR' | 'NURSE' | 'RECEPTIONIST'

interface NavItem { to: string; label: string; icon: React.ReactNode }

function navItems(role: Role | undefined): NavItem[] {
    const dashboard = { to: '/dashboard', label: 'Dashboard', icon: <LayoutDashboard className="w-4 h-4" /> }
    const pacientes  = { to: '/pacientes', label: 'Pacientes',  icon: <Users className="w-4 h-4" /> }
    const citas      = { to: '/citas',     label: 'Citas',      icon: <CalendarDays className="w-4 h-4" /> }
    const misCitas   = { to: '/citas',     label: 'Mis citas',  icon: <CalendarDays className="w-4 h-4" /> }
    const nuevaCita  = { to: '/citas/nueva', label: 'Agendar cita', icon: <CalendarPlus className="w-4 h-4" /> }
    const doctores   = { to: '/doctores',  label: 'Doctores',   icon: <Stethoscope className="w-4 h-4" /> }
    const triaje     = { to: '/triaje',    label: 'Triaje',     icon: <ClipboardList className="w-4 h-4" /> }
    const perfil     = { to: '/perfil',    label: 'Mi perfil',  icon: <UserCircle className="w-4 h-4" /> }

    switch (role) {
        case 'ADMIN':        return [dashboard, pacientes, citas, doctores, perfil]
        case 'DOCTOR':       return [dashboard, misCitas, pacientes, triaje, perfil]
        case 'NURSE':        return [dashboard, pacientes, triaje, citas, perfil]
        case 'RECEPTIONIST': return [dashboard, pacientes, citas, nuevaCita, perfil]
        default:             return [dashboard, perfil]
    }
}

const ROLE_LABEL: Record<string, string> = {
    ADMIN: 'Administrador',
    DOCTOR: 'Doctor',
    NURSE: 'Enfermero/a',
    RECEPTIONIST: 'Recepcionista',
}

const link = ({ isActive }: { isActive: boolean }) =>
    `flex items-center gap-2 px-3 py-2 rounded-lg text-sm font-medium transition-colors ${
        isActive ? 'bg-slate-700 text-white' : 'text-slate-300 hover:bg-slate-700 hover:text-white'
    }`;

export default function AppLayout() {
    const { user, logout } = useAuth();
    const items = navItems(user?.role as Role)

    return (
        <div className="min-h-screen flex bg-slate-50">
            <aside className="w-56 bg-slate-900 text-white flex flex-col flex-shrink-0">
                <div className="px-4 py-5 border-b border-slate-700">
                    <Logo variant="horizontal" mode="dark" />
                </div>
                <nav className="flex flex-col gap-1 p-3 flex-1">
                    {items.map(item => (
                        <NavLink key={item.to + item.label} to={item.to} className={link} end={item.to === '/dashboard'}>
                            {item.icon}
                            {item.label}
                        </NavLink>
                    ))}
                </nav>
                <div className="p-3 border-t border-slate-700">
                    <p className="text-xs text-slate-400 px-3 truncate">{user?.fullName}</p>
                    <p className="text-xs text-slate-500 px-3 mb-2">{ROLE_LABEL[user?.role ?? ''] ?? user?.role}</p>
                    <button
                        onClick={logout}
                        className="flex items-center gap-2 px-3 py-2 w-full rounded-lg text-sm text-slate-300 hover:bg-slate-700 hover:text-white transition-colors"
                    >
                        <LogOut className="w-4 h-4" /> Cerrar sesión
                    </button>
                </div>
            </aside>
            <main className="flex-1 overflow-auto">
                <section className="p-6 max-w-6xl mx-auto">
                    <Outlet />
                </section>
            </main>
        </div>
    );
}
