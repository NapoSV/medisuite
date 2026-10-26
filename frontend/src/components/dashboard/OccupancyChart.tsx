import type { OccupancySlot } from '../../api/dashboard'

const etiquetaHora = (h: number) => `${String(h).padStart(2, '0')}:00`

export default function OccupancyChart({ slots, loading }: { slots: OccupancySlot[]; loading: boolean }) {
  if (loading) return <div className="h-48 rounded-2xl bg-slate-200 animate-pulse" />

  const max = slots.reduce((m, s) => Math.max(m, s.appointments), 0)

  return (
    <section className="bg-white rounded-2xl shadow-sm border border-slate-200 p-6">
      <h2 className="text-sm font-semibold uppercase tracking-wide text-slate-500">
        Citas por hora
      </h2>
      <p className="text-xs text-slate-500 mt-0.5 mb-4">
        Citas programadas no canceladas, por hora local. No representa capacidad de la clínica.
      </p>

      {slots.length === 0 || max === 0 ? (
        <p className="text-sm text-slate-500">No hay citas programadas para hoy.</p>
      ) : (
        <>
          <ul className="flex items-end gap-1 h-40" aria-hidden="true">
            {slots.map(s => (
              <li key={s.hour} className="flex-1 flex flex-col items-center justify-end gap-1 min-w-0 h-full">
                <span className="text-xs tabular-nums text-slate-600">{s.appointments || ''}</span>
                <div className="w-full flex-1 min-h-0 flex items-end">
                  <div className="w-full bg-blue-500 rounded-t"
                     style={{ height: `${Math.round((s.appointments / max) * 100)}%` }} />
                </div>
                <span className="text-[10px] tabular-nums text-slate-400">{s.hour}</span>
              </li>
            ))}
          </ul>

          <table className="sr-only">
            <caption>Citas programadas por hora local</caption>
            <thead>
              <tr><th scope="col">Hora</th><th scope="col">Citas</th></tr>
            </thead>
            <tbody>
              {slots.map(s => (
                <tr key={s.hour}>
                  <td>{etiquetaHora(s.hour)}</td>
                  <td>{s.appointments}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </>
      )}
    </section>
  )
}
