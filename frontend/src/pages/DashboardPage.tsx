import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { Users, CalendarCheck, Bell, FileText, RefreshCw, CalendarPlus, ClipboardList } from 'lucide-react'
import { fetchDashboardMetrics, type DashboardMetrics } from '../api/dashboard'
import { useAuth } from '../auth/useAuth'
import ErrorAlert from '../components/ErrorAlert'

interface KpiCardProps {
  label: string
  value: number | null
  icon: React.ReactNode
  accent: string
  loading: boolean
}

function KpiCard({ label, value, icon, accent, loading }: KpiCardProps) {
  return (
    <div className="bg-white rounded-2xl shadow-sm border border-slate-200 p-6 flex items-start gap-4">
      <div className={`w-11 h-11 rounded-xl flex items-center justify-center flex-shrink-0 ${accent}`}>
        {icon}
      </div>
      <div className="flex-1 min-w-0">
        <p className="text-xs font-medium uppercase tracking-wide text-slate-500">{label}</p>
        {loading ? (
          <div className="h-8 w-16 mt-1 rounded-md bg-slate-200 animate-pulse" />
        ) : (
          <p className="text-3xl font-semibold text-slate-800 mt-1 tabular-nums">
            {value !== null ? value.toLocaleString('es-SV') : '-'}
          </p>
        )}
      </div>
    </div>
  )
}

interface QuickAction { to: string; label: string; desc: string; icon: React.ReactNode; color: string }

const ROLE_CONFIG: Record<string, {
  greeting: string
  actions: QuickAction[]
  kpis: (m: DashboardMetrics | null, loading: boolean) => React.ReactNode
}> = {
  ADMIN: {
    greeting: 'Panel de administración',
    actions: [
      { to: '/pacientes', label: 'Gestionar pacientes', desc: 'Ver y crear expedientes', icon: <Users className="w-5 h-5 text-green-600" />, color: 'bg-green-50 border-green-200' },
      { to: '/doctores',  label: 'Gestionar doctores',  desc: 'Registrar médicos y especialidades', icon: <ClipboardList className="w-5 h-5 text-violet-600" />, color: 'bg-violet-50 border-violet-200' },
      { to: '/citas/nueva', label: 'Nueva cita', desc: 'Agendar una consulta', icon: <CalendarPlus className="w-5 h-5 text-blue-600" />, color: 'bg-blue-50 border-blue-200' },
    ],
    kpis: (m, loading) => (
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <KpiCard label="Citas hoy"          value={m?.appointmentsToday ?? null}    icon={<CalendarCheck className="w-5 h-5 text-blue-600" />}   accent="bg-blue-50"   loading={loading} />
        <KpiCard label="Pacientes activos"  value={m?.activePatients ?? null}        icon={<Users className="w-5 h-5 text-green-600" />}          accent="bg-green-50"  loading={loading} />
        <KpiCard label="Alertas"            value={m?.alerts ?? null}                icon={<Bell className="w-5 h-5 text-amber-600" />}           accent="bg-amber-50"  loading={loading} />
        <KpiCard label="Recetas emitidas"   value={m?.prescriptionsIssued ?? null}   icon={<FileText className="w-5 h-5 text-violet-600" />}      accent="bg-violet-50" loading={loading} />
      </div>
    ),
  },
  DOCTOR: {
    greeting: 'Tu agenda de hoy',
    actions: [
      { to: '/citas',     label: 'Mis citas', desc: 'Ver el listado de tus consultas', icon: <CalendarCheck className="w-5 h-5 text-blue-600" />, color: 'bg-blue-50 border-blue-200' },
      { to: '/pacientes', label: 'Pacientes', desc: 'Buscar expedientes clínicos', icon: <Users className="w-5 h-5 text-green-600" />, color: 'bg-green-50 border-green-200' },
    ],
    kpis: (m, loading) => (
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <KpiCard label="Mis citas hoy"      value={m?.appointmentsToday ?? null}    icon={<CalendarCheck className="w-5 h-5 text-blue-600" />}   accent="bg-blue-50"   loading={loading} />
        <KpiCard label="Alertas clínicas"   value={m?.alerts ?? null}               icon={<Bell className="w-5 h-5 text-amber-600" />}           accent="bg-amber-50"  loading={loading} />
        <KpiCard label="Recetas emitidas"   value={m?.prescriptionsIssued ?? null}  icon={<FileText className="w-5 h-5 text-violet-600" />}      accent="bg-violet-50" loading={loading} />
      </div>
    ),
  },
  NURSE: {
    greeting: 'Resumen de la clínica',
    actions: [
      { to: '/pacientes', label: 'Pacientes', desc: 'Consultar expedientes', icon: <Users className="w-5 h-5 text-green-600" />, color: 'bg-green-50 border-green-200' },
      { to: '/citas',     label: 'Citas del día', desc: 'Ver agenda de consultas', icon: <CalendarCheck className="w-5 h-5 text-blue-600" />, color: 'bg-blue-50 border-blue-200' },
    ],
    kpis: (m, loading) => (
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <KpiCard label="Citas hoy"          value={m?.appointmentsToday ?? null}    icon={<CalendarCheck className="w-5 h-5 text-blue-600" />}   accent="bg-blue-50"   loading={loading} />
        <KpiCard label="Pacientes activos"  value={m?.activePatients ?? null}        icon={<Users className="w-5 h-5 text-green-600" />}          accent="bg-green-50"  loading={loading} />
        <KpiCard label="Alertas"            value={m?.alerts ?? null}               icon={<Bell className="w-5 h-5 text-amber-600" />}           accent="bg-amber-50"  loading={loading} />
      </div>
    ),
  },
  RECEPTIONIST: {
    greeting: 'Gestión de agenda',
    actions: [
      { to: '/citas/nueva', label: 'Agendar cita',   desc: 'Registrar nueva consulta', icon: <CalendarPlus className="w-5 h-5 text-blue-600" />,  color: 'bg-blue-50 border-blue-200' },
      { to: '/pacientes',   label: 'Nuevo paciente', desc: 'Registrar en el sistema',   icon: <Users className="w-5 h-5 text-green-600" />, color: 'bg-green-50 border-green-200' },
      { to: '/citas',       label: 'Ver agenda',     desc: 'Consultar citas del día',   icon: <CalendarCheck className="w-5 h-5 text-amber-600" />, color: 'bg-amber-50 border-amber-200' },
    ],
    kpis: (m, loading) => (
      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
        <KpiCard label="Citas hoy"         value={m?.appointmentsToday ?? null}   icon={<CalendarCheck className="w-5 h-5 text-blue-600" />}  accent="bg-blue-50"  loading={loading} />
        <KpiCard label="Pacientes activos" value={m?.activePatients ?? null}       icon={<Users className="w-5 h-5 text-green-600" />}         accent="bg-green-50" loading={loading} />
      </div>
    ),
  },
}

export default function DashboardPage() {
  const { user } = useAuth()
  const [metrics, setMetrics] = useState<DashboardMetrics | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  const load = () => {
    setLoading(true)
    setError(null)
    fetchDashboardMetrics()
      .then(setMetrics)
      .catch((err: Error) => setError(err.message))
      .finally(() => setLoading(false))
  }

  useEffect(load, [])

  const role = user?.role ?? 'ADMIN'
  const config = ROLE_CONFIG[role] ?? ROLE_CONFIG.ADMIN

  return (
    <div>
      <div className="flex items-end justify-between mb-6">
        <div>
          <p className="text-sm text-slate-500">Bienvenido/a, <span className="font-medium text-slate-700">{user?.fullName}</span></p>
          <h1 className="text-2xl font-semibold text-slate-800 mt-0.5">{config.greeting}</h1>
        </div>
        <button
          onClick={load}
          disabled={loading}
          className="flex items-center gap-2 py-2 px-3 rounded-lg border border-slate-200 text-sm font-medium text-slate-500 hover:text-slate-800 hover:border-slate-400 transition-all disabled:opacity-50"
        >
          <RefreshCw className={`w-4 h-4 ${loading ? 'animate-spin' : ''}`} />
          Actualizar
        </button>
      </div>

      {error && (
        <ErrorAlert className="mb-6" message="No se pudieron cargar las métricas" detail={error} onRetry={load} />
      )}

      {config.kpis(metrics, loading)}

      <div className="mt-8">
        <h2 className="text-sm font-semibold uppercase tracking-wide text-slate-500 mb-3">Acciones rápidas</h2>
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
          {config.actions.map(a => (
            <Link key={a.to + a.label} to={a.to}
              className={`flex items-start gap-3 p-4 rounded-xl border bg-white hover:shadow-sm transition-shadow ${a.color}`}>
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
