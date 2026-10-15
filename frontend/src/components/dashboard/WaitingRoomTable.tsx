 import type { WaitingRoomEntry } from '../../api/dashboard'

const hora = (iso: string) =>
  new Date(iso).toLocaleTimeString('es-SV', {
    timeZone: 'America/El_Salvador',
    hour: '2-digit',
    minute: '2-digit',
  })

export default function WaitingRoomTable({ rows, loading }: { rows: WaitingRoomEntry[]; loading: boolean }) {
  if (loading) return <div className="h-32 rounded-2xl bg-slate-200 animate-pulse" />

  return (
    <section className="bg-white rounded-2xl shadow-sm border border-slate-200 overflow-hidden">
      <h2 className="text-sm font-semibold uppercase tracking-wide text-slate-500 px-6 pt-5 pb-3">
        Sala de espera
      </h2>

      {rows.length === 0 ? (
        <p className="px-6 pb-6 text-sm text-slate-500">No hay pacientes en sala de espera.</p>
      ) : (
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <caption className="sr-only">Pacientes en sala de espera</caption>
            <thead className="bg-slate-50 text-slate-500">
              <tr>
                <th scope="col" className="text-left font-medium px-6 py-2">Paciente</th>
                <th scope="col" className="text-left font-medium px-6 py-2">Hora</th>
                <th scope="col" className="text-left font-medium px-6 py-2">Estado</th>
              </tr>
            </thead>
            <tbody>
              {rows.map(r => (
                <tr key={r.appointmentId} className="border-t border-slate-100">
                  <td className="px-6 py-3 text-slate-800">{r.patientName}</td>
                  <td className="px-6 py-3 tabular-nums text-slate-600">{hora(r.scheduledAt)}</td>
                  <td className="px-6 py-3 text-slate-600">{r.status}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </section>
  )
}