import { useEffect, useState } from 'react'
import { Users, CalendarCheck, Clock, Stethoscope, LogOut, RefreshCw, AlertCircle } from 'lucide-react'
import { useAuthStore } from '../store/authStore'
import { fetchDashboardMetrics, type DashboardMetrics } from '../api/dashboard'
import Logo from '../components/Logo'

interface KpiCardProps {
  label: string
  value: number | null
  icon: React.ReactNode
  accent: string
  loading: boolean
}

function KpiCard({ label, value, icon, accent, loading }: KpiCardProps) {
  return (
    <div className="bg-surface rounded-[20px] shadow-card border border-border p-6 flex items-start gap-4">
      <div className={`w-11 h-11 rounded-[12px] flex items-center justify-center flex-shrink-0 ${accent}`}>
        {icon}
      </div>
      <div className="flex-1 min-w-0">
        <p className="text-xs font-medium uppercase tracking-wide text-muted">{label}</p>
        {loading ? (
          <div className="h-8 w-16 mt-1 rounded-md bg-border/60 animate-pulse" />
        ) : (
          <p className="text-3xl font-semibold text-text mt-1 tabular-nums">
            {value !== null ? value.toLocaleString('es-SV') : '—'}
          </p>
        )}
      </div>
    </div>
  )
}

export default function DashboardPage() {
  const { user, logout } = useAuthStore()
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

  return (
    <div className="min-h-screen bg-background">
      <header className="bg-surface border-b border-border">
        <div className="max-w-6xl mx-auto px-6 py-4 flex items-center justify-between gap-4">
          <Logo variant="horizontal" mode="light" />
          <div className="flex items-center gap-3">
            <div className="text-right hidden sm:block">
              <p className="text-sm font-medium text-text leading-tight">{user?.fullName}</p>
              <p className="text-xs text-muted leading-tight">{user?.role}</p>
            </div>
            <button
              onClick={logout}
              className="flex items-center gap-2 py-2 px-3 rounded-[10px] border border-border text-sm font-medium text-muted hover:text-text hover:border-text/30 transition-all"
              aria-label="Cerrar sesión"
            >
              <LogOut className="w-4 h-4" />
              <span className="hidden sm:inline">Salir</span>
            </button>
          </div>
        </div>
      </header>

      <main className="max-w-6xl mx-auto px-6 py-8">
        <div className="flex items-end justify-between mb-6">
          <div>
            <h1 className="text-2xl font-semibold text-text">Panel de control</h1>
            <p className="text-sm text-muted mt-1">Indicadores clave de la clínica en tiempo real</p>
          </div>
          <button
            onClick={load}
            disabled={loading}
            className="flex items-center gap-2 py-2 px-3 rounded-[10px] border border-border text-sm font-medium text-muted hover:text-text hover:border-text/30 transition-all disabled:opacity-50 disabled:cursor-not-allowed"
          >
            <RefreshCw className={`w-4 h-4 ${loading ? 'animate-spin' : ''}`} />
            Actualizar
          </button>
        </div>

        {error && (
          <div className="mb-6 flex items-start gap-3 p-4 bg-[#FEE2E2] border border-[#FCA5A5] rounded-[12px]" role="alert">
            <AlertCircle className="w-5 h-5 text-[#DC2626] flex-shrink-0 mt-0.5" />
            <div className="flex-1">
              <p className="text-sm font-medium text-[#991B1B]">No se pudieron cargar las métricas</p>
              <p className="text-sm text-[#991B1B]/80 mt-0.5">{error}</p>
            </div>
            <button
              onClick={load}
              className="text-sm font-medium text-[#991B1B] hover:underline flex-shrink-0"
            >
              Reintentar
            </button>
          </div>
        )}

        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
          <KpiCard
            label="Pacientes registrados"
            value={metrics?.totalPatients ?? null}
            icon={<Users className="w-5 h-5 text-primary" />}
            accent="bg-primary-light"
            loading={loading}
          />
          <KpiCard
            label="Citas de hoy"
            value={metrics?.appointmentsToday ?? null}
            icon={<CalendarCheck className="w-5 h-5 text-[#16A34A]" />}
            accent="bg-[#DCFCE7]"
            loading={loading}
          />
          <KpiCard
            label="Citas pendientes"
            value={metrics?.pendingAppointments ?? null}
            icon={<Clock className="w-5 h-5 text-[#D97706]" />}
            accent="bg-[#FEF3C7]"
            loading={loading}
          />
          <KpiCard
            label="Doctores activos"
            value={metrics?.activeDoctors ?? null}
            icon={<Stethoscope className="w-5 h-5 text-[#7C3AED]" />}
            accent="bg-[#EDE9FE]"
            loading={loading}
          />
        </div>
      </main>
    </div>
  )
}
