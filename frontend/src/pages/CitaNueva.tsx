import { useEffect, useState } from 'react'
import ErrorAlert from '../components/ErrorAlert'
import { ApiError } from '../api/http'
import {listDoctors, listPatients, listSlots, createAppointment,
  type Doctor, type Paciente,
} from '../api/appointments'

// MOCK temporal — quitar cuando entre el PR de citas (Orellana)
const MOCK_DOCTORS: Doctor[] = [
  { id: 1, user: { firstName: 'Carlos', lastName: 'Mejia' }, specialty: { name: 'Medicina General' } },
  { id: 2, user: { firstName: 'Sofia', lastName: 'Hernandez' }, specialty: { name: 'Pediatria' } },
]
const MOCK_PATIENTS: Paciente[] = [
  { id: 1, firstName: 'Ana', lastName: 'Ramirez', dui: '01234567-8' },
  { id: 2, firstName: 'Luis', lastName: 'Portillo', dui: '09876543-2' },
]
const mockSlots = (date: string) =>
  ['08:00', '09:00', '10:00', '14:00', '15:00'].map(h => `${date}T${h}:00`)

export default function CitaNueva() {
  const [doctors, setDoctors] = useState<Doctor[]>([])
  const [patients, setPatients] = useState<Paciente[]>([])
  const [slots, setSlots] = useState<string[]>([])
  const [form, setForm] = useState({ doctorId: '', patientId: '', date: '', slot: '', reason: '' })
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [ok, setOk] = useState<string | null>(null)

  useEffect(() => {
    listDoctors().then(setDoctors).catch(() => setDoctors(MOCK_DOCTORS))
    listPatients().then(setPatients).catch(() => setPatients(MOCK_PATIENTS))
  }, [])

  useEffect(() => {
    if (!form.doctorId || !form.date) { setSlots([]); return }
    listSlots(+form.doctorId, form.date)
      .then(setSlots)
      .catch(() => setSlots(mockSlots(form.date)))
  }, [form.doctorId, form.date])

  const submit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError(null); setOk(null); setSaving(true)
    try {
      const cita = await createAppointment({
        patientId: +form.patientId,
        doctorId: +form.doctorId,
        scheduledAt: form.slot,
        reason: form.reason,
      })
      setOk(`Cita creada. Codigo de reserva: ${cita.reservationCode}`)
      setForm({ doctorId: '', patientId: '', date: '', slot: '', reason: '' })
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'Error creando la cita')
    } finally {
      setSaving(false)
    }
  }

  const hoy = new Date().toISOString().slice(0, 10)

  return (
    <form onSubmit={submit} className="max-w-lg flex flex-col gap-3">
      <h2 className="text-2xl font-bold">Nueva cita</h2>

      {error && <ErrorAlert message={error} />}
      {ok && <p className="text-sm text-green-700 bg-green-50 p-2 rounded">{ok}</p>}

      <select value={form.patientId} onChange={e => setForm({ ...form, patientId: e.target.value })}
              className="border p-2 rounded" required>
        <option value="">Paciente...</option>
        {patients.map(p => (
          <option key={p.id} value={p.id}>{p.firstName} {p.lastName} — {p.dui}</option>
        ))}
      </select>

      <select value={form.doctorId} onChange={e => setForm({ ...form, doctorId: e.target.value, slot: '' })}
              className="border p-2 rounded" required>
        <option value="">Doctor...</option>
        {doctors.map(d => (
          <option key={d.id} value={d.id}>
            {d.user.firstName} {d.user.lastName} — {d.specialty?.name ?? 'Sin especialidad'}
          </option>
        ))}
      </select>

      <input type="date" min={hoy} value={form.date}
             onChange={e => setForm({ ...form, date: e.target.value, slot: '' })}
             className="border p-2 rounded" required />

      <select value={form.slot} onChange={e => setForm({ ...form, slot: e.target.value })}
              className="border p-2 rounded" required
              disabled={!form.doctorId || !form.date}>
        <option value="">
          {!form.doctorId || !form.date ? 'Elegi doctor y fecha primero...' : 'Hora disponible...'}
        </option>
        {slots.map(s => (
          <option key={s} value={s}>{new Date(s).toLocaleTimeString('es-SV')}</option>
        ))}
      </select>

      <textarea placeholder="Motivo de la consulta" value={form.reason}
                onChange={e => setForm({ ...form, reason: e.target.value })}
                className="border p-2 rounded" required />

      <button disabled={saving} className="bg-blue-600 text-white p-2 rounded disabled:opacity-50">
        {saving ? 'Guardando...' : 'Confirmar cita'}
      </button>
    </form>
  )
}