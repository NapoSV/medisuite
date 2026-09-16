import { useEffect, useState } from 'react'
import { listUpcoming, type Cita } from '../api/appointments'

// MOCK temporal — quitar cuando entre el PR de citas (Orellana)
const MOCK: Cita[] = [
  { id: 1, scheduledAt: '2026-09-18T09:00:00', patient: { firstName: 'Ana', lastName: 'Ramirez' },
    doctor: { user: { firstName: 'Carlos', lastName: 'Mejia' } }, status: 'PENDING', reservationCode: 'CT-0001' },
  { id: 2, scheduledAt: '2026-09-18T10:30:00', patient: { firstName: 'Luis', lastName: 'Portillo' },
    doctor: { user: { firstName: 'Sofia', lastName: 'Hernandez' } }, status: 'CONFIRMED', reservationCode: 'CT-0002' },
  { id: 3, scheduledAt: '2026-09-19T14:00:00', patient: { firstName: 'Marta', lastName: 'Guzman' },
    doctor: { user: { firstName: 'Carlos', lastName: 'Mejia' } }, status: 'COMPLETED', reservationCode: 'CT-0003' },
]

const badge = (s: string) => ({
  PENDING: 'bg-yellow-100 text-yellow-800',
  CONFIRMED: 'bg-blue-100 text-blue-800',
  IN_WAITING: 'bg-purple-100 text-purple-800',
  IN_CONSULTATION: 'bg-orange-100 text-orange-800',
  COMPLETED: 'bg-green-100 text-green-800',
  CANCELLED: 'bg-red-100 text-red-800',
  NO_SHOW: 'bg-gray-200 text-gray-700',
}[s] ?? 'bg-gray-100')

export default function Citas() {
  const [rows, setRows] = useState<Cita[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    listUpcoming()
      .then(setRows)
      .catch(() => { setRows(MOCK); setError('Backend de citas no disponible — mostrando datos de ejemplo') })
      .finally(() => setLoading(false))
  }, [])

  const cancel = (id: number) => {
    if (!confirm('¿Cancelar cita?')) return
    setRows(rs => rs.map(r => r.id === id ? { ...r, status: 'CANCELLED' } : r))
  }

  if (loading) return <p className="p-4">Cargando citas...</p>

  return (
    <div>
      <div className="flex justify-between mb-4">
        <h2 className="text-2xl font-bold">Citas</h2>
        <a href="/citas/nueva" className="bg-blue-600 text-white px-4 py-2 rounded">+ Nueva cita</a>
      </div>

      {error && <p className="mb-3 text-sm text-amber-700 bg-amber-50 p-2 rounded">{error}</p>}

      <table className="w-full bg-white rounded shadow">
        <thead>
          <tr className="border-b">
            <th className="text-left p-3">Fecha</th>
            <th className="text-left p-3">Paciente</th>
            <th className="text-left p-3">Doctor</th>
            <th className="text-left p-3">Codigo</th>
            <th className="text-left p-3">Estado</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {rows.map(c => (
            <tr key={c.id} className="border-b">
              <td className="p-3">{new Date(c.scheduledAt).toLocaleString('es-SV')}</td>
              <td className="p-3">{c.patient.firstName} {c.patient.lastName}</td>
              <td className="p-3">{c.doctor.user.firstName} {c.doctor.user.lastName}</td>
              <td className="p-3 font-mono">{c.reservationCode}</td>
              <td className="p-3">
                <span className={`px-2 py-1 rounded text-xs ${badge(c.status)}`}>{c.status}</span>
              </td>
              <td className="p-3 text-right">
                {c.status !== 'CANCELLED' && c.status !== 'COMPLETED' && (
                  <button onClick={() => cancel(c.id)} className="text-red-600 text-sm">Cancelar</button>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}