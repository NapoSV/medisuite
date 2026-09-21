import { useEffect, useState } from 'react'
import LoadingSpinner from '../components/LoadingSpinner'
import { useParams } from 'react-router-dom'
import { getMedicalRecord, createVitalSign, type Expediente as Exp, type SignosVitales } from '../api/medicalRecords'
import { useAuth } from '../auth/useAuth'

type Evento =
  | { tipo: 'receta'; fecha: string; data: Exp['prescriptions'][0] }
  | { tipo: 'signos'; fecha: string; data: SignosVitales }

const PRIORITY_COLOR: Record<string, string> = {
  NORMAL: 'bg-green-100 text-green-800',
  URGENTE: 'bg-yellow-100 text-yellow-800',
  EMERGENCIA: 'bg-red-100 text-red-800',
}

export default function Expediente() {
  const { id } = useParams()
  const { user } = useAuth()
  const canTriage = user?.role === 'NURSE' || user?.role === 'DOCTOR'

  const [rec, setRec] = useState<Exp | null>(null)
  const [loading, setLoading] = useState(true)
  const [showTriaje, setShowTriaje] = useState(false)
  const [triajeForm, setTriajeForm] = useState({
    temperatureC: '', heartRate: '', bloodPressure: '',
    weightKg: '', heightCm: '', symptoms: '', priority: 'NORMAL',
  })
  const [saving, setSaving] = useState(false)
  const [triajeOk, setTriajeOk] = useState<string | null>(null)
  const [triajeErr, setTriajeErr] = useState<string | null>(null)

  const patientId = Number(id ?? 0)

  const load = () => {
    setLoading(true)
    getMedicalRecord(patientId)
      .then(setRec)
      .catch(() => setRec(null))
      .finally(() => setLoading(false))
  }

  useEffect(() => { load() }, [patientId])

  const setT = (k: string, v: string) => setTriajeForm(f => ({ ...f, [k]: v }))

  const submitTriaje = async (e: React.FormEvent) => {
    e.preventDefault()
    setTriajeErr(null); setTriajeOk(null); setSaving(true)
    try {
      await createVitalSign(patientId, {
        temperatureC:  triajeForm.temperatureC  ? parseFloat(triajeForm.temperatureC)  : undefined,
        heartRate:     triajeForm.heartRate      ? parseInt(triajeForm.heartRate)       : undefined,
        bloodPressure: triajeForm.bloodPressure  || undefined,
        weightKg:      triajeForm.weightKg       ? parseFloat(triajeForm.weightKg)      : undefined,
        heightCm:      triajeForm.heightCm       ? parseFloat(triajeForm.heightCm)      : undefined,
        symptoms:      triajeForm.symptoms       || undefined,
        priority:      triajeForm.priority,
      })
      setTriajeOk('Signos vitales registrados correctamente.')
      setTriajeForm({ temperatureC: '', heartRate: '', bloodPressure: '', weightKg: '', heightCm: '', symptoms: '', priority: 'NORMAL' })
      setShowTriaje(false)
      load()
    } catch {
      setTriajeErr('Error al guardar los signos vitales.')
    } finally {
      setSaving(false)
    }
  }

  if (loading) return <LoadingSpinner label="Cargando expediente..." />
  if (!rec) return <p className="p-4 text-red-600">No se encontró el expediente.</p>

  const { patient } = rec

  const calcAge = () => {
    if (!patient.birthDate) return null
    const today = new Date()
    const birth = new Date(patient.birthDate)
    let age = today.getFullYear() - birth.getFullYear()
    const m = today.getMonth() - birth.getMonth()
    if (m < 0 || (m === 0 && today.getDate() < birth.getDate())) age--
    return age
  }

  const eventos: Evento[] = [
    ...rec.prescriptions.map(p => ({ tipo: 'receta' as const, fecha: p.issuedOn, data: p })),
    ...rec.vitalSigns.map(v => ({ tipo: 'signos' as const, fecha: v.recordedAt, data: v })),
  ].sort((a, b) => b.fecha.localeCompare(a.fecha))

  return (
    <div className="max-w-3xl">
      <div className="flex justify-between items-start mb-2">
        <h2 className="text-2xl font-bold">
          Expediente: {patient.firstName} {patient.lastName}
        </h2>
        {canTriage && (
          <button onClick={() => { setShowTriaje(!showTriaje); setTriajeOk(null); setTriajeErr(null) }}
                  className="bg-emerald-600 text-white text-sm px-3 py-2 rounded">
            {showTriaje ? 'Cerrar triaje' : '+ Registrar signos vitales'}
          </button>
        )}
      </div>

      <div className="mt-1 flex flex-wrap gap-4 text-sm text-slate-600">
        <span>DUI: <b className="font-mono">{patient.dui}</b></span>
        {calcAge() !== null && <span>Edad: <b>{calcAge()} años</b></span>}
        {patient.bloodType && <span>Tipo de sangre: <b>{patient.bloodType}</b></span>}
        {patient.allergies && <span className="text-red-700">Alergias: <b>{patient.allergies}</b></span>}
      </div>

      {/* Formulario de triaje inline */}
      {showTriaje && (
        <div className="mt-4 bg-emerald-50 border border-emerald-200 rounded p-4">
          <h3 className="font-semibold text-emerald-800 mb-3">Registrar signos vitales</h3>
          {triajeErr && <p className="text-red-600 text-sm mb-2">{triajeErr}</p>}
          <form onSubmit={submitTriaje} className="grid grid-cols-2 gap-3">
            <div>
              <label className="text-xs text-slate-500 block mb-1">Temperatura (°C)</label>
              <input type="number" step="0.1" placeholder="36.8"
                     value={triajeForm.temperatureC} onChange={e => setT('temperatureC', e.target.value)}
                     className="border rounded p-2 w-full" />
            </div>
            <div>
              <label className="text-xs text-slate-500 block mb-1">Frec. cardíaca (lpm)</label>
              <input type="number" placeholder="72"
                     value={triajeForm.heartRate} onChange={e => setT('heartRate', e.target.value)}
                     className="border rounded p-2 w-full" />
            </div>
            <div>
              <label className="text-xs text-slate-500 block mb-1">Presión arterial</label>
              <input placeholder="120/80"
                     value={triajeForm.bloodPressure} onChange={e => setT('bloodPressure', e.target.value)}
                     className="border rounded p-2 w-full" />
            </div>
            <div>
              <label className="text-xs text-slate-500 block mb-1">Peso (kg)</label>
              <input type="number" step="0.1" placeholder="70.0"
                     value={triajeForm.weightKg} onChange={e => setT('weightKg', e.target.value)}
                     className="border rounded p-2 w-full" />
            </div>
            <div>
              <label className="text-xs text-slate-500 block mb-1">Talla (cm)</label>
              <input type="number" step="0.1" placeholder="165"
                     value={triajeForm.heightCm} onChange={e => setT('heightCm', e.target.value)}
                     className="border rounded p-2 w-full" />
            </div>
            <div>
              <label className="text-xs text-slate-500 block mb-1">Prioridad</label>
              <select value={triajeForm.priority} onChange={e => setT('priority', e.target.value)}
                      className="border rounded p-2 w-full">
                <option value="NORMAL">Normal</option>
                <option value="URGENTE">Urgente</option>
                <option value="EMERGENCIA">Emergencia</option>
              </select>
            </div>
            <div className="col-span-2">
              <label className="text-xs text-slate-500 block mb-1">Síntomas</label>
              <textarea rows={2} placeholder="Describe los síntomas..."
                        value={triajeForm.symptoms} onChange={e => setT('symptoms', e.target.value)}
                        className="border rounded p-2 w-full resize-none" />
            </div>
            <div className="col-span-2 flex justify-end gap-2">
              <button type="button" onClick={() => setShowTriaje(false)}
                      className="border rounded px-4 py-2 text-sm">Cancelar</button>
              <button disabled={saving}
                      className="bg-emerald-600 text-white rounded px-4 py-2 text-sm disabled:opacity-50">
                {saving ? 'Guardando...' : 'Guardar'}
              </button>
            </div>
          </form>
        </div>
      )}

      {triajeOk && (
        <p className="mt-3 text-sm text-emerald-700 bg-emerald-50 p-2 rounded">{triajeOk}</p>
      )}

      <h3 className="text-lg font-semibold mt-8 mb-4">Historial clínico</h3>

      {eventos.length === 0 ? (
        <p className="text-slate-500">Este paciente no tiene registros todavía.</p>
      ) : (
        <ol className="border-l-2 border-slate-200 ml-2">
          {eventos.map(ev => (
            <li key={`${ev.tipo}-${ev.data.id}`} className="relative pl-6 pb-8">
              <span className={`absolute -left-[7px] top-1 w-3 h-3 rounded-full
                ${ev.tipo === 'receta' ? 'bg-blue-500' : 'bg-emerald-500'}`} />

              <p className="text-xs text-slate-500">
                {new Date(ev.fecha).toLocaleString('es-SV')}
              </p>

              {ev.tipo === 'receta' ? (
                <div className="bg-white rounded shadow p-3 mt-1">
                  <p className="font-semibold text-sm">
                    Receta · Dr. {ev.data.doctor.user.firstName} {ev.data.doctor.user.lastName}
                  </p>
                  <ul className="mt-2 text-sm list-disc ml-5">
                    {ev.data.items.map((it, i) => (
                      <li key={i}>{it.medication}: <span className="text-slate-600">{it.dosage}</span></li>
                    ))}
                  </ul>
                </div>
              ) : (
                <div className="bg-white rounded shadow p-3 mt-1">
                  <div className="flex justify-between items-center">
                    <p className="font-semibold text-sm">Signos vitales</p>
                    {ev.data.priority && (
                      <span className={`px-2 py-0.5 rounded text-xs font-medium ${PRIORITY_COLOR[ev.data.priority] ?? 'bg-gray-100'}`}>
                        {ev.data.priority}
                      </span>
                    )}
                  </div>
                  <div className="mt-2 flex flex-wrap gap-4 text-sm">
                    {ev.data.temperatureC != null && <span>Temp: <b>{ev.data.temperatureC}°C</b></span>}
                    {ev.data.heartRate    != null && <span>FC: <b>{ev.data.heartRate} lpm</b></span>}
                    {ev.data.bloodPressure && <span>PA: <b>{ev.data.bloodPressure}</b></span>}
                    {ev.data.weightKg     != null && <span>Peso: <b>{ev.data.weightKg} kg</b></span>}
                    {ev.data.heightCm     != null && <span>Talla: <b>{ev.data.heightCm} cm</b></span>}
                  </div>
                  {ev.data.symptoms && (
                    <p className="mt-2 text-slate-500 text-xs italic">"{ev.data.symptoms}"</p>
                  )}
                </div>
              )}
            </li>
          ))}
        </ol>
      )}
    </div>
  )
}
