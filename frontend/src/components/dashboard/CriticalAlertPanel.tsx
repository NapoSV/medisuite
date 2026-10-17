import { Bell } from 'lucide-react'
import type { CriticalAlert, ClinicalPriority } from '../../api/dashboard'

const ESTILO: Record<ClinicalPriority, string> = {
  NORMAL: 'bg-slate-100 text-slate-700 border-slate-300',
  URGENTE: 'bg-amber-50 text-amber-800 border-amber-300',
  EMERGENCIA: 'bg-red-50 text-red-800 border-red-300',
}

const fecha = (iso: string) =>
  new Date(iso).toLocaleString('es-SV', {
    timeZone: 'America/El_Salvador',
    day: '2-digit', month: '2-digit',
    hour: '2-digit', minute: '2-digit',
  })

export default function CriticalAlertPanel({ alerts, loading }: { alerts: CriticalAlert[]; loading: boolean }) {
  if (loading) return <div className="h-32 rounded-2xl bg-slate-200 animate-pulse" />

  return (
    <section className="bg-white rounded-2xl shadow-sm border border-slate-200 p-6">
      <h2 className="text-sm font-semibold uppercase tracking-wide text-slate-500 mb-3 flex items-center gap-2">
        <Bell className="w-4 h-4 text-amber-600" aria-hidden="true" />
        Alertas clínicas
      </h2>

      {alerts.length === 0 ? (
        <p className="text-sm text-slate-500">Sin alertas clínicas registradas.</p>
      ) : (
        <ul className="flex flex-col gap-2">
          {alerts.map(a => (
            <li key={a.vitalSignId}
                className={`flex items-center justify-between gap-3 border rounded-lg px-3 py-2 ${ESTILO[a.priority] ?? ESTILO.NORMAL}`}>
              <span className="text-sm font-medium">
                Prioridad {a.priority}
                <span className="font-normal"> · paciente #{a.patientId}</span>
              </span>
              <span className="text-xs tabular-nums whitespace-nowrap">{fecha(a.recordedAt)}</span>
            </li>
          ))}
        </ul>
      )}
    </section>
  )
}