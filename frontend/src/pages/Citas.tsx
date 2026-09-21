import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { ApiError } from '../api/http'
import {
  listUpcoming, cancelAppointment, completeAppointment,
  rescheduleAppointment, listSlots, type Cita,
} from '../api/appointments'
import { useAuth } from '../auth/useAuth'
import LoadingSpinner from '../components/LoadingSpinner'
import ErrorAlert from '../components/ErrorAlert'

// MOCK temporal - quitar cuando entre el PR de citas (Orellana)
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

const FINALES = ['CANCELLED', 'COMPLETED', 'NO_SHOW']

export default function Citas() {
  const { user } = useAuth()
  const isDoctor = user?.role === 'DOCTOR'
  const isReceptionist = user?.role === 'RECEPTIONIST'
  const canManage = user?.role === 'ADMIN' || user?.role === 'RECEPTIONIST' || user?.role === 'NURSE'

  const [rows, setRows] = useState<Cita[]>([])
  const [loading, setLoading] = useState(true)
  const [aviso, setAviso] = useState<string | null>(null)
  const [error, setError] = useState<string | null>(null)

  // estado del panel de reprogramacion
  const [repro, setRepro] = useState<Cita | null>(null)
  const [fecha, setFecha] = useState('')
  const [slots, setSlots] = useState<string[]>([])
  const [slot, setSlot] = useState('')

  useEffect(() => {
    listUpcoming()
      .then(data => {
        if (isDoctor && user?.fullName) {
          const [first, ...rest] = user.fullName.split(' ')
          const last = rest.join(' ')
          setRows(data.filter(c =>
            c.doctor.user.firstName.toLowerCase() === first.toLowerCase() ||
            c.doctor.user.lastName.toLowerCase() === last.toLowerCase()
          ))
        } else {
          setRows(data)
        }
      })
      .catch(() => {
        const mock = isDoctor && user?.fullName
          ? MOCK.filter(c => c.doctor.user.firstName === user.fullName?.split(' ')[0])
          : MOCK
        setRows(mock)
        setAviso('Backend de citas no disponible, mostrando datos de ejemplo')
      })
      .finally(() => setLoading(false))
  }, [isDoctor, user?.fullName])

  useEffect(() => {
    if (!repro || !fecha) { setSlots([]); return }
    listSlots(repro.doctor.id, fecha)
      .then(setSlots)
      .catch(() => setSlots(['08:00', '09:00', '10:00', '14:00'].map(h => `${fecha}T${h}:00`)))
  }, [repro, fecha])

  const actualizar = (c: Cita) => setRows(rs => rs.map(r => r.id === c.id ? c : r))

  const onCancel = async (c: Cita) => {
    if (!confirm(`¿Cancelar la cita ${c.reservationCode}?`)) return
    setError(null)
    try {
      await cancelAppointment(c.id)
      actualizar({ ...c, status: 'CANCELLED' })
    } catch (err) {
      if (err instanceof ApiError) setError(err.message)
      else actualizar({ ...c, status: 'CANCELLED' })
    }
  }

  const onComplete = async (c: Cita) => {
    if (!confirm(`¿Marcar como completada la cita ${c.reservationCode}?`)) return
    setError(null)
    try {
      actualizar(await completeAppointment(c.id))
    } catch (err) {
      if (err instanceof ApiError) setError(err.message)
      else actualizar({ ...c, status: 'COMPLETED' })
    }
  }

  const onReprogramar = async () => {
    if (!repro || !slot) return
    setError(null)
    try {
      actualizar(await rescheduleAppointment(repro.id, slot))
    } catch (err) {
      if (err instanceof ApiError) { setError(err.message); return }
      actualizar({ ...repro, scheduledAt: slot })
    }
    setRepro(null); setFecha(''); setSlot('')
  }

  if (loading) return <LoadingSpinner label="Cargando citas..." />

  const hoy = new Date().toISOString().slice(0, 10)

  return (
    <div>
      <div className="flex justify-between mb-4">
        <div>
          <h2 className="text-2xl font-bold">
            {isDoctor ? 'Mis citas' : 'Citas'}
          </h2>
          {isDoctor && (
            <p className="text-sm text-slate-500 mt-0.5">Solo se muestran tus consultas asignadas</p>
          )}
        </div>
        {(canManage || isReceptionist) && (
          <Link to="/citas/nueva" className="bg-blue-600 text-white px-4 py-2 rounded">+ Nueva cita</Link>
        )}
      </div>

      {aviso && <p className="mb-3 text-sm text-amber-700 bg-amber-50 p-2 rounded">{aviso}</p>}
      {error && <ErrorAlert className="mb-3" message={error} />}

      <table className="w-full bg-white rounded shadow">
        <thead>
          <tr className="border-b">
            <th className="text-left p-3">Fecha</th>
            <th className="text-left p-3">Paciente</th>
            <th className="text-left p-3">Doctor</th>
            <th className="text-left p-3">Codigo</th>
            <th className="text-left p-3">Estado</th>
            <th className="text-right p-3">Acciones</th>
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
              <td className="p-3 text-right whitespace-nowrap">
                {!FINALES.includes(c.status) && (
                  <>
                    {!isDoctor && (
                      <button onClick={() => { setRepro(c); setFecha(''); setSlot('') }}
                              className="text-blue-600 text-sm mr-3">Reprogramar</button>
                    )}
                    <button onClick={() => onComplete(c)}
                            className="text-green-700 text-sm mr-3">Completar</button>
                    {!isDoctor && (
                      <button onClick={() => onCancel(c)}
                              className="text-red-600 text-sm">Cancelar</button>
                    )}
                  </>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {repro && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center">
          <div className="bg-white p-6 rounded shadow max-w-sm w-full flex flex-col gap-3">
            <h3 className="text-lg font-semibold">Reprogramar {repro.reservationCode}</h3>
            <p className="text-sm text-slate-600">
              Actual: {new Date(repro.scheduledAt).toLocaleString('es-SV')}
            </p>

            <input type="date" min={hoy} value={fecha}
                   onChange={e => { setFecha(e.target.value); setSlot('') }}
                   className="border p-2 rounded" />

            <select value={slot} onChange={e => setSlot(e.target.value)}
                    className="border p-2 rounded" disabled={!fecha}>
              <option value="">{fecha ? 'Hora disponible...' : 'Elegi una fecha primero...'}</option>
              {slots.map(s => (
                <option key={s} value={s}>{new Date(s).toLocaleTimeString('es-SV')}</option>
              ))}
            </select>

            <div className="flex gap-2 justify-end mt-2">
              <button onClick={() => setRepro(null)} className="px-3 py-2 text-slate-600">Cerrar</button>
              <button onClick={onReprogramar} disabled={!slot}
                      className="bg-blue-600 text-white px-4 py-2 rounded disabled:opacity-50">
                Confirmar
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}