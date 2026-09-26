import { useEffect, useMemo, useState } from 'react'
import { api } from '../api/client'
import { createVitalSign, getMedicalRecord, type SignosVitales } from '../api/medicalRecords'
import { ApiError } from '../api/http'
import LoadingSpinner from '../components/LoadingSpinner'
import ErrorAlert from '../components/ErrorAlert'

type Patient = { id: number; firstName: string; lastName: string; dui: string; birthDate?: string }

const PRIORITY_OPTS = [
  { value: 'LOW',      label: 'Normal',       color: 'bg-green-100 text-green-800' },
  { value: 'MEDIUM',   label: 'Urgente',      color: 'bg-yellow-100 text-yellow-800' },
  { value: 'HIGH',     label: 'Urgente alto', color: 'bg-orange-100 text-orange-800' },
  { value: 'CRITICAL', label: 'Emergencia',   color: 'bg-red-100 text-red-800' },
]

const EMPTY_FORM = {
  temperatureC: '',
  heartRate: '',
  bloodPressure: '',
  weightKg: '',
  heightCm: '',
  symptoms: '',
  priority: 'LOW',
}

function calcAge(birthDate?: string) {
  if (!birthDate) return null
  const today = new Date()
  const birth = new Date(birthDate)
  let age = today.getFullYear() - birth.getFullYear()
  const m = today.getMonth() - birth.getMonth()
  if (m < 0 || (m === 0 && today.getDate() < birth.getDate())) age--
  return age
}

export default function Triaje() {
  const [patients, setPatients] = useState<Patient[]>([])
  const [search, setSearch] = useState('')
  const [selected, setSelected] = useState<Patient | null>(null)
  const [recent, setRecent] = useState<SignosVitales[]>([])
  const [loadingHistory, setLoadingHistory] = useState(false)
  const [form, setForm] = useState(EMPTY_FORM)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [ok, setOk] = useState<string | null>(null)

  useEffect(() => {
    api.get<{ content: Patient[] }>('/api/patients?size=200')
      .then(r => setPatients(r.data.content))
      .catch(() => setPatients([]))
  }, [])

  const filtered = useMemo(() => {
    const q = search.trim().toLowerCase()
    if (!q) return patients
    const digits = q.replace(/\D/g, '')
    return patients.filter(p =>
      `${p.firstName} ${p.lastName}`.toLowerCase().includes(q) ||
      (digits && p.dui.replace('-', '').includes(digits))
    )
  }, [patients, search])

  const selectPatient = async (p: Patient) => {
    setSelected(p)
    setForm(EMPTY_FORM)
    setOk(null)
    setError(null)
    setLoadingHistory(true)
    try {
      const exp = await getMedicalRecord(p.id)
      setRecent(exp.vitalSigns.slice(0, 5))
    } catch {
      setRecent([])
    } finally {
      setLoadingHistory(false)
    }
  }

  const set = (k: string, v: string) => setForm(f => ({ ...f, [k]: v }))

  const submit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!selected) return
    setError(null); setOk(null); setSaving(true)
    try {
      await createVitalSign(selected.id, {
        temperatureC: form.temperatureC ? parseFloat(form.temperatureC) : undefined,
        heartRate:    form.heartRate    ? parseInt(form.heartRate)       : undefined,
        bloodPressure: form.bloodPressure || undefined,
        weightKg:     form.weightKg     ? parseFloat(form.weightKg)     : undefined,
        heightCm:     form.heightCm     ? parseFloat(form.heightCm)     : undefined,
        symptoms:     form.symptoms     || undefined,
        priority:     form.priority,
      })
      setOk(`Triaje registrado para ${selected.firstName} ${selected.lastName}`)
      setForm(EMPTY_FORM)
      // refrescar historial
      const exp = await getMedicalRecord(selected.id)
      setRecent(exp.vitalSigns.slice(0, 5))
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'Error al guardar el triaje')
    } finally {
      setSaving(false)
    }
  }

  return (
    <div className="max-w-4xl">
      <h2 className="text-2xl font-bold mb-6">Triaje: Signos vitales</h2>

      {/* Búsqueda de paciente */}
      <div className="bg-white rounded shadow p-4 mb-6">
        <h3 className="font-semibold text-slate-700 mb-3">Buscar paciente</h3>
        <input
          placeholder="Nombre o DUI..."
          value={search}
          onChange={e => setSearch(e.target.value)}
          className="border rounded px-3 py-2 w-full mb-3"
        />
        {search.trim() && (
          <div className="border rounded overflow-hidden max-h-48 overflow-y-auto">
            {filtered.length === 0 ? (
              <p className="p-3 text-slate-500 text-sm">Sin resultados</p>
            ) : filtered.map(p => (
              <button
                key={p.id} type="button"
                onClick={() => { selectPatient(p); setSearch('') }}
                className="w-full text-left px-4 py-2 hover:bg-blue-50 border-b last:border-0 text-sm"
              >
                <span className="font-medium">{p.firstName} {p.lastName}</span>
                <span className="text-slate-400 ml-2 font-mono text-xs">{p.dui}</span>
              </button>
            ))}
          </div>
        )}
      </div>

      {/* Panel del paciente seleccionado */}
      {selected && (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          {/* Formulario */}
          <div className="bg-white rounded shadow p-4">
            <div className="mb-4 pb-3 border-b">
              <p className="font-semibold text-lg">{selected.firstName} {selected.lastName}</p>
              <p className="text-slate-500 text-sm font-mono">{selected.dui}
                {calcAge(selected.birthDate) !== null && (
                  <span className="ml-3 font-sans">{calcAge(selected.birthDate)} años</span>
                )}
              </p>
            </div>

            {error && <ErrorAlert message={error} />}
            {ok && <p className="text-sm text-green-700 bg-green-50 p-2 rounded mb-3">{ok}</p>}

            <form onSubmit={submit} className="flex flex-col gap-3">
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="text-xs text-slate-500 block mb-1">Temperatura (°C)</label>
                  <input type="number" step="0.1" min="30" max="45"
                         placeholder="36.8"
                         value={form.temperatureC}
                         onChange={e => set('temperatureC', e.target.value)}
                         className="border rounded p-2 w-full" />
                </div>
                <div>
                  <label className="text-xs text-slate-500 block mb-1">Frec. cardíaca (lpm)</label>
                  <input type="number" min="20" max="300"
                         placeholder="72"
                         value={form.heartRate}
                         onChange={e => set('heartRate', e.target.value)}
                         className="border rounded p-2 w-full" />
                </div>
                <div>
                  <label className="text-xs text-slate-500 block mb-1">Presión arterial</label>
                  <input placeholder="120/80"
                         value={form.bloodPressure}
                         onChange={e => set('bloodPressure', e.target.value)}
                         className="border rounded p-2 w-full" />
                </div>
                <div>
                  <label className="text-xs text-slate-500 block mb-1">Peso (kg)</label>
                  <input type="number" step="0.1" min="0"
                         placeholder="70.0"
                         value={form.weightKg}
                         onChange={e => set('weightKg', e.target.value)}
                         className="border rounded p-2 w-full" />
                </div>
                <div>
                  <label className="text-xs text-slate-500 block mb-1">Talla (cm)</label>
                  <input type="number" step="0.1" min="0"
                         placeholder="165"
                         value={form.heightCm}
                         onChange={e => set('heightCm', e.target.value)}
                         className="border rounded p-2 w-full" />
                </div>
                <div>
                  <label className="text-xs text-slate-500 block mb-1">Prioridad</label>
                  <select value={form.priority}
                          onChange={e => set('priority', e.target.value)}
                          className="border rounded p-2 w-full">
                    {PRIORITY_OPTS.map(o => (
                      <option key={o.value} value={o.value}>{o.label}</option>
                    ))}
                  </select>
                </div>
              </div>

              <div>
                <label className="text-xs text-slate-500 block mb-1">Síntomas / motivo</label>
                <textarea rows={3} placeholder="Describe los síntomas..."
                          value={form.symptoms}
                          onChange={e => set('symptoms', e.target.value)}
                          className="border rounded p-2 w-full resize-none" />
              </div>

              <button disabled={saving}
                      className="bg-blue-600 text-white p-2 rounded disabled:opacity-50 font-medium">
                {saving ? 'Guardando...' : 'Registrar triaje'}
              </button>
            </form>
          </div>

          {/* Historial reciente */}
          <div className="bg-white rounded shadow p-4">
            <h3 className="font-semibold text-slate-700 mb-3">Últimos registros</h3>
            {loadingHistory ? (
              <LoadingSpinner label="Cargando historial..." />
            ) : recent.length === 0 ? (
              <p className="text-slate-400 text-sm">Sin registros previos.</p>
            ) : (
              <ol className="flex flex-col gap-3">
                {recent.map(v => {
                  const pri = PRIORITY_OPTS.find(o => o.value === v.priority)
                  return (
                    <li key={v.id} className="border rounded p-3 text-sm">
                      <div className="flex justify-between items-start mb-2">
                        <span className="text-xs text-slate-500">
                          {new Date(v.recordedAt).toLocaleString('es-SV')}
                        </span>
                        {pri && (
                          <span className={`px-2 py-0.5 rounded text-xs font-medium ${pri.color}`}>
                            {pri.label}
                          </span>
                        )}
                      </div>
                      <div className="grid grid-cols-2 gap-x-4 gap-y-1 text-slate-700">
                        {v.temperatureC != null && <span>Temp: <b>{v.temperatureC}°C</b></span>}
                        {v.heartRate    != null && <span>FC: <b>{v.heartRate} lpm</b></span>}
                        {v.bloodPressure && <span>PA: <b>{v.bloodPressure}</b></span>}
                        {v.weightKg     != null && <span>Peso: <b>{v.weightKg} kg</b></span>}
                        {v.heightCm     != null && <span>Talla: <b>{v.heightCm} cm</b></span>}
                      </div>
                      {v.symptoms && (
                        <p className="mt-2 text-slate-500 text-xs italic">"{v.symptoms}"</p>
                      )}
                    </li>
                  )
                })}
              </ol>
            )}
          </div>
        </div>
      )}
    </div>
  )
}
