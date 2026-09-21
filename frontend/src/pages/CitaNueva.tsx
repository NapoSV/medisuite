import { useEffect, useMemo, useRef, useState } from 'react'
import ErrorAlert from '../components/ErrorAlert'
import { ApiError } from '../api/http'
import {
  listDoctors, listPatients, listSlots, createAppointment,
  type Doctor, type Paciente,
} from '../api/appointments'

export default function CitaNueva() {
  const [doctors, setDoctors] = useState<Doctor[]>([])
  const [patients, setPatients] = useState<Paciente[]>([])
  const [slots, setSlots] = useState<string[]>([])

  const [specialtyName, setSpecialtyName] = useState('')
  const [doctorId, setDoctorId] = useState('')
  const [patientQuery, setPatientQuery] = useState('')
  const [selectedPatient, setSelectedPatient] = useState<Paciente | null>(null)
  const [showSuggestions, setShowSuggestions] = useState(false)
  const [date, setDate] = useState('')
  const [slot, setSlot] = useState('')
  const [reason, setReason] = useState('')

  const [saving, setSaving] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [ok, setOk] = useState<string | null>(null)

  const comboRef = useRef<HTMLDivElement>(null)

  useEffect(() => {
    listDoctors().then(setDoctors).catch(() => setDoctors([]))
    listPatients().then(setPatients).catch(() => setPatients([]))
  }, [])

  useEffect(() => {
    if (!doctorId || !date) { setSlots([]); return }
    listSlots(+doctorId, date).then(setSlots).catch(() => setSlots([]))
  }, [doctorId, date])

  useEffect(() => {
    function onClickOutside(e: MouseEvent) {
      if (comboRef.current && !comboRef.current.contains(e.target as Node)) {
        setShowSuggestions(false)
      }
    }
    document.addEventListener('mousedown', onClickOutside)
    return () => document.removeEventListener('mousedown', onClickOutside)
  }, [])

  const specialties = useMemo(() =>
    [...new Set(doctors.map(d => d.specialty?.name).filter(Boolean) as string[])].sort(),
    [doctors]
  )

  const filteredDoctors = useMemo(() =>
    specialtyName ? doctors.filter(d => d.specialty?.name === specialtyName) : doctors,
    [doctors, specialtyName]
  )

  const suggestions = useMemo(() => {
    const q = patientQuery.trim().toLowerCase()
    if (!q) return []
    const digits = q.replace(/\D/g, '')
    return patients.filter(p =>
      `${p.firstName} ${p.lastName}`.toLowerCase().includes(q) ||
      (digits && p.dui.replace('-', '').includes(digits))
    ).slice(0, 8)
  }, [patients, patientQuery])

  const selectPatient = (p: Paciente) => {
    setSelectedPatient(p)
    setPatientQuery(`${p.firstName} ${p.lastName}`)
    setShowSuggestions(false)
  }

  const clearPatient = () => {
    setSelectedPatient(null)
    setPatientQuery('')
    setShowSuggestions(false)
  }

  const resetForm = () => {
    setSpecialtyName('')
    setDoctorId('')
    clearPatient()
    setDate('')
    setSlot('')
    setReason('')
    setSlots([])
  }

  const submit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!selectedPatient) { setError('Selecciona un paciente.'); return }
    setError(null); setOk(null); setSaving(true)
    try {
      const cita = await createAppointment({
        patientId: selectedPatient.id,
        doctorId:  +doctorId,
        scheduledAt: slot,
        reason,
      })
      setOk(`Cita creada. Código de reserva: ${cita.reservationCode}`)
      resetForm()
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'Error al crear la cita')
    } finally {
      setSaving(false)
    }
  }

  const hoy = new Date().toISOString().slice(0, 10)

  return (
    <form onSubmit={submit} className="max-w-lg flex flex-col gap-5">
      <h2 className="text-2xl font-bold">Nueva cita</h2>

      {error && <ErrorAlert message={error} />}
      {ok && <p className="text-sm text-green-700 bg-green-50 p-3 rounded">{ok}</p>}

      {/* 1. Especialidad */}
      <div className="flex flex-col gap-1">
        <label className="text-sm font-medium text-slate-700">1. Especialidad</label>
        <select
          value={specialtyName}
          onChange={e => { setSpecialtyName(e.target.value); setDoctorId('') }}
          className="border p-2 rounded"
        >
          <option value="">Todas las especialidades</option>
          {specialties.map(s => <option key={s} value={s}>{s}</option>)}
        </select>
      </div>

      {/* 2. Doctor */}
      <div className="flex flex-col gap-1">
        <label className="text-sm font-medium text-slate-700">2. Doctor</label>
        <select
          value={doctorId}
          onChange={e => { setDoctorId(e.target.value); setSlot('') }}
          className="border p-2 rounded"
          required
        >
          <option value="">Seleccionar doctor</option>
          {filteredDoctors.map(d => (
            <option key={d.id} value={d.id}>
              {d.user.firstName} {d.user.lastName}
              {d.specialty?.name ? ` (${d.specialty.name})` : ''}
            </option>
          ))}
        </select>
        {specialtyName && filteredDoctors.length === 0 && (
          <p className="text-sm text-amber-600">No hay doctores para esta especialidad.</p>
        )}
      </div>

      {/* 3. Paciente (autocomplete) */}
      <div className="flex flex-col gap-1">
        <label className="text-sm font-medium text-slate-700">3. Paciente</label>
        <div className="relative" ref={comboRef}>
          <input
            type="text"
            placeholder="Buscar por nombre o DUI..."
            value={patientQuery}
            onChange={e => {
              setPatientQuery(e.target.value)
              setSelectedPatient(null)
              setShowSuggestions(true)
            }}
            onFocus={() => { if (patientQuery && !selectedPatient) setShowSuggestions(true) }}
            className={`border p-2 rounded w-full pr-8 ${selectedPatient ? 'bg-blue-50 border-blue-400' : ''}`}
            autoComplete="off"
          />
          {selectedPatient && (
            <button
              type="button"
              onClick={clearPatient}
              className="absolute right-2 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-700 text-lg leading-none"
              tabIndex={-1}
            >
              ×
            </button>
          )}
          {showSuggestions && suggestions.length > 0 && (
            <ul className="absolute z-10 left-0 right-0 top-full mt-1 bg-white border rounded shadow-lg max-h-52 overflow-y-auto">
              {suggestions.map(p => (
                <li key={p.id}>
                  <button
                    type="button"
                    onMouseDown={() => selectPatient(p)}
                    className="w-full text-left px-4 py-2 hover:bg-blue-50 text-sm"
                  >
                    <span className="font-medium">{p.firstName} {p.lastName}</span>
                    <span className="text-slate-400 ml-2 font-mono text-xs">{p.dui}</span>
                  </button>
                </li>
              ))}
            </ul>
          )}
          {showSuggestions && patientQuery.trim() && suggestions.length === 0 && !selectedPatient && (
            <div className="absolute z-10 left-0 right-0 top-full mt-1 bg-white border rounded shadow p-3 text-sm text-slate-500">
              Sin resultados para "{patientQuery}"
            </div>
          )}
        </div>
        {selectedPatient && (
          <p className="text-xs text-blue-700">
            Paciente seleccionado: <b>{selectedPatient.firstName} {selectedPatient.lastName}</b> · {selectedPatient.dui}
          </p>
        )}
      </div>

      {/* 4. Fecha */}
      <div className="flex flex-col gap-1">
        <label className="text-sm font-medium text-slate-700">4. Fecha</label>
        <input
          type="date"
          min={hoy}
          value={date}
          onChange={e => { setDate(e.target.value); setSlot('') }}
          className="border p-2 rounded"
          required
        />
      </div>

      {/* 5. Hora */}
      <div className="flex flex-col gap-1">
        <label className="text-sm font-medium text-slate-700">5. Hora disponible</label>
        <select
          value={slot}
          onChange={e => setSlot(e.target.value)}
          className="border p-2 rounded"
          required
          disabled={!doctorId || !date}
        >
          <option value="">
            {!doctorId ? 'Selecciona un doctor primero' :
             !date ? 'Selecciona una fecha primero' :
             slots.length === 0 ? 'Sin horarios disponibles' :
             'Seleccionar hora'}
          </option>
          {slots.map(s => (
            <option key={s} value={s}>
              {new Date(s).toLocaleTimeString('es-SV', { hour: '2-digit', minute: '2-digit' })}
            </option>
          ))}
        </select>
        {doctorId && date && slots.length === 0 && (
          <p className="text-xs text-slate-500">El doctor no tiene horarios libres ese día (08:00 a 17:00).</p>
        )}
      </div>

      {/* 6. Motivo */}
      <div className="flex flex-col gap-1">
        <label className="text-sm font-medium text-slate-700">6. Motivo de la consulta</label>
        <textarea
          placeholder="Describe el motivo de la cita..."
          value={reason}
          onChange={e => setReason(e.target.value)}
          rows={3}
          className="border p-2 rounded resize-none"
          required
        />
      </div>

      <button
        disabled={saving || !selectedPatient}
        className="bg-blue-600 text-white p-2 rounded disabled:opacity-50 font-medium"
      >
        {saving ? 'Guardando...' : 'Confirmar cita'}
      </button>
    </form>
  )
}
