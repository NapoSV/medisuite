import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { Users, CalendarCheck, Bell, FileText, RefreshCw, CalendarPlus, ClipboardList } from 'lucide-react'
import { fetchDashboardMetrics, type DashboardResponse } from '../api/dashboard'
import { useAuth } from '../auth/useAuth'
import ErrorAlert from '../components/ErrorAlert'
import KpiCard from '../components/dashboard/KpiCard'
import WaitingRoomTable from '../components/dashboard/WaitingRoomTable'
import CriticalAlertPanel from '../components/dashboard/CriticalAlertPanel'
import OccupancyChart from '../components/dashboard/OccupancyChart'

interface QuickAction { to: string; label: string; desc: string; icon: React.ReactNode; color: string }

const ROLE_CONFIG: Record<string, { greeting: string; appointmentsLabel: string; actions: QuickAction[] }> = {
  ADMIN: {
    greeting: 'Panel de administración',
    appointmentsLabel: 'Citas hoy',
    actions: [
      { to: '/pacientes', label: 'Gestionar pacientes', desc: 'Ver y crear expedientes', icon: <Users className="w-5 h-5 text-green-600" />, color: 'bg-green-50 border-green-200' },
      { to: '/doctores', label: 'Gestionar doctores', desc: 'Registrar médicos y especialidades', icon: <ClipboardList className="w-5 h-5 text-violet-600" />, color: 'bg-violet-50 border-violet-200' },
      { to: '/citas/nueva', label: 'Nueva cita', desc: 'Agendar una consulta', icon: <CalendarPlus className="w-5 h-5 text-blue-600" />, color: 'bg-blue-50 border-blue-200' },
    ],
  },
  DOCTOR: {
    greeting: 'Tu agenda de hoy',
    appointmentsLabel: 'Mis citas hoy',
    actions: [
      { to: '/citas', label: 'Mis citas', desc: 'Ver el listado de tus consultas', icon: <CalendarCheck className="w-5 h-5 text-blue-600" />, color: 'bg-blue-50 border-blue-200' },
      { to: '/pacientes', label: 'Pacientes', desc: 'Buscar expedientes clínicos', icon: <Users className="w-5 h-5 text-green-600" />, color: 'bg-green-50 border-green-200' },
    ],
  },
  NURSE: {
    greeting: 'Resumen de la clínica',
    appointmentsLabel: 'Citas hoy',
    actions: [
      { to: '/pacientes', label: 'Pacientes', desc: 'Consultar expedientes', icon: <Users className="w-5 h-5 text-green-600" />, color: 'bg-green-50 border-green-200' },
      { to: '/citas', label: 'Citas del día', desc: 'Ver agenda de consultas', icon: <CalendarCheck className="w-5 h-5 text-blue-600" />, color: 'bg-blue-50 border-blue-200' },
    ],
  },
  RECEPTIONIST: {
    greeting: 'Gestión de agenda',
    appointmentsLabel: 'Citas hoy',
    actions: [
      { to: '/citas/nueva', label: 'Agendar cita', desc: 'Registrar nueva consulta', icon: <CalendarPlus className="w-5 h-5 text-blue-600" />, color: 'bg-blue-50 border-blue-200' },
      { to: '/pacientes', label: 'Nuevo paciente', desc: 'Registrar en el sistema', icon: <Users className="w-5 h-5 text-green-600" />, color: 'bg-green-50 border-green-200' },
      { to: '/citas', label: 'Ver agenda', desc: 'Consultar citas del día', icon: <CalendarCheck className="w-5 h-5 text-amber-600" />, color: 'bg-amber-50 border-amber-200' },
    ],
  },
}

export default function DashboardPage() {
  const { user, loading: authLoading } = useAuth()
  const [metrics, setMetrics] = useState<DashboardResponse | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [reloadKey, setReloadKey] = useState(0)

  useEffect(() => {
    let cancelado = false
    fetchDashboardMetrics()
      .then(m => { if (!cancelado) setMetrics(m) })
      .catch((err: Error) => { if (!cancelado) setError(err.message) })
      .finally(() => { if (!cancelado) setLoading(false) })
    return () => { cancelado = true }
  }, [reloadKey])

  const recargar = () => {
    setLoading(true)
    setError(null)
    setReloadKey(k => k + 1)
  }

  // No asumir ADMIN: esperar a que termine la carga de autenticación.
  if (authLoading) return <p className="text-sm text-slate-500">Cargando sesión...</p>
  if (!user) return <p className="text-sm text-slate-500">Sesión no disponible.</p>

  const config = ROLE_CONFIG[user.role] ?? ROLE_CONFIG.RECEPTIONIST
  const m = metrics

  return (
    <div>
      <div className="flex flex-wrap items-end justify-between gap-3 mb-6">
        <div className="min-w-0">
          <p className="text-sm text-slate-500">
            Bienvenido/a, <span className="font-medium text-slate-700">{user.fullName}</span>
          </p>
          <h1 className="text-2xl font-semibold text-slate-800 mt-0.5">{config.greeting}</h1>
        </div>
        <button
          onClick={recargar}
          disabled={loading}
          className="flex items-center gap-2 py-2 px-3 rounded-lg border border-slate-200 text-sm font-medium text-slate-500 hover:text-slate-800 hover:border-slate-400 transition-all disabled:opacity-50 focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue-600"
        >
          <RefreshCw className={`w-4 h-4 ${loading ? 'animate-spin' : ''}`} aria-hidden="true" />
          Actualizar
        </button>
      </div>

      {error && (
        <ErrorAlert className="mb-6" message="No se pudieron cargar las métricas" detail={error} onRetry={recargar} />
      )}

      {/* Cada KPI se renderiza solo si el backend lo autorizó para este rol. */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        {(loading || m?.appointmentsToday !== undefined) && (
          <KpiCard label={config.appointmentsLabel} value={m?.appointmentsToday ?? null}
            icon={<CalendarCheck className="w-5 h-5 text-blue-600" />} accent="bg-blue-50" loading={loading} />
        )}
        {(loading || m?.activePatients !== undefined) && (
          <KpiCard label="Pacientes registrados" value={m?.activePatients ?? null}
            icon={<Users className="w-5 h-5 text-green-600" />} accent="bg-green-50" loading={loading} />
        )}
        {(loading || m?.criticalAlerts !== undefined) && (
          <KpiCard label="Alertas críticas" value={m?.criticalAlerts ?? null}
            icon={<Bell className="w-5 h-5 text-amber-600" />} accent="bg-amber-50" loading={loading} />
        )}
        {(loading || m?.prescriptionsThisWeek !== undefined) && (
          <KpiCard label="Recetas de esta semana" value={m?.prescriptionsThisWeek ?? null}
            icon={<FileText className="w-5 h-5 text-violet-600" />} accent="bg-violet-50" loading={loading} />
        )}
      </div>

      {(loading || m?.waitingRoom !== undefined) && (
        <div className="mt-6">
          <WaitingRoomTable rows={m?.waitingRoom ?? []} loading={loading} />
        </div>
      )}

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mt-6">
        {(loading || m?.occupancyByHour !== undefined) && (
          <OccupancyChart slots={m?.occupancyByHour ?? []} loading={loading} />
        )}
        {(loading || m?.clinicalAlerts !== undefined) && (
          <CriticalAlertPanel alerts={m?.clinicalAlerts ?? []} loading={loading} />
        )}
      </div>

      <div className="mt-8">
        <h2 className="text-sm font-semibold uppercase tracking-wide text-slate-500 mb-3">Acciones rápidas</h2>
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-3">
          {config.actions.map(a => (
            <Link key={a.to + a.label} to={a.to}
              className={`flex items-start gap-3 p-4 rounded-xl border bg-white hover:shadow-sm transition-shadow focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue-600 ${a.color}`}>
              <div className="mt-0.5">{a.icon}</div>
              <div>
                <p className="text-sm font-semibold text-slate-800">{a.label}</p>
                <p className="text-xs text-slate-500 mt-0.5">{a.desc}</p>
              </div>
            </Link>
          ))}
        </div>
      </div>
    </div>
  )
}
