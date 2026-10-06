import { useEffect, useMemo, useState } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
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

// Preset "signos normales" para agilizar el llenado cuando el paciente no reporta anomalias.
const PRESET_NORMAL = {
  temperatureC: '36.7',
  heartRate: '72',
  bloodPressure: '120/80',
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

/** Alertas visuales por rango — devuelve clase extra para el input. */
function rangoClase(campo: string, valor: string): string {
  if (!valor) return ''
  const n = parseFloat(valor)
  if (Number.isNaN(n)) return ''
  switch (campo) {
    case 'temperatureC':
      if (n >= 38 || n < 35) return 'border-red-400 bg-red-50'
      if (n >= 37.5)         return 'border-amber-400 bg-amber-50'
      return 'border-emerald-400 bg-emerald-50'
    case 'heartRate':
      if (n < 50 || n > 120) return 'border-red-400 bg-red-50'
      if (n < 60 || n > 100) return 'border-amber-400 bg-amber-50'
      return 'border-emerald-400 bg-emerald-50'
    default:
      return ''
  }
}

/** Parsea "120/80" y marca anomalias. */
function presionClase(valor: string): string {
  const m = valor.match(/^(\d{2,3})\s*\/\s*(\d{2,3})$/)
  if (!m) return ''
  const sys = parseInt(m[1]); const dia = parseInt(m[2])
  if (sys >= 180 || dia >= 120 || sys < 90 || dia < 60) return 'border-red-400 bg-red-50'
  if (sys >= 140 || dia >= 90) return 'border-amber-400 bg-amber-50'
  return 'border-emerald-400 bg-emerald-50'
}

export default function Triaje() {
  const [params] = useSearchParams()
  const preselectedPatientId = params.get('patientId')

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

  // Deeplink: ?patientId=X preselecciona el paciente (bug Ing. Guevara: triaje engorroso)
  useEffect(() => {
    if (!preselectedPatientId || selected || patients.length === 0) return
    const p = patients.find(x => x.id === Number(preselectedPatientId))
    if (p) selectPatient(p)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [preselectedPatientId, patients])

  const filtered = useMemo(() => {
    const q = search.trim().toLowerCase()
    if (!q) return patients.slice(0, 10)
    const digits = q.replace(/\D/g, '')
    return patients.filter(p =>
      `${p.firstName} ${p.lastName}`.toLowerCase().includes(q) ||
      (digits && p.dui.replace('-', '').includes(digits))
    ).slice(0, 10)
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
  const aplicarPresetNormal = () => { setForm(PRESET_NORMAL); setError(null) }
  const limpiar = () => { setForm(EMPTY_FORM); setError(null); setOk(null) }

  // IMC calculado en vivo
  const imc = useMemo(() => {
    const w = parseFloat(form.weightKg); const h = parseFloat(form.heightCm)
    if (!w || !h) return null
    const m = h / 100
    const v = w / (m * m)
    if (!Number.isFinite(v)) return null
    let etiqueta = 'Normal'
    let color = 'text-emerald-700 bg-emerald-50'
    if (v < 18.5)      { etiqueta = 'Bajo peso';   color = 'text-amber-700 bg-amber-50' }
    else if (v < 25)   { etiqueta = 'Normal';      color = 'text-emerald-700 bg-emerald-50' }
    else if (v < 30)   { etiqueta = 'Sobrepeso';   color = 'text-amber-700 bg-amber-50' }
    else               { etiqueta = 'Obesidad';    color = 'text-red-700 bg-red-50' }
    return { valor: v.toFixed(1), etiqueta, color }
  }, [form.weightKg, form.heightCm])

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
      const exp = await getMedicalRecord(selected.id)
      setRecent(exp.vitalSigns.slice(0, 5))
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'Error al guardar el triaje')
    } finally {
      setSaving(false)
    }
  }

  // Submit con Ctrl+Enter sobre el form (atajo pedido por operadores de triaje)
  const onKeyDown = (e: React.KeyboardEvent) => {
    if ((e.ctrlKey || e.metaKey) && e.key === 'Enter') {
      (e.currentTarget as HTMLFormElement).requestSubmit?.()
    }
  }

  return (
    <div className="max-w-5xl">
      <h2 className="text-2xl font-bold mb-6">Triaje: Signos vitales</h2>

      {/* Búsqueda */}
      <div className="bg-white rounded shadow p-4 mb-6">
        <h3 className="font-semibold text-slate-700 mb-3">Paciente</h3>
        <input
          placeholder="Buscar por nombre o DUI..."
          value={search}
          onChange={e => setSearch(e.target.value)}
          className="border rounded px-3 py-2 w-full mb-3"
        />
        {search.trim() && (
          <div className="border rounded overflow-hidden max-h-56 overflow-y-auto">
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

      {selected && (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          {/* Formulario */}
          <div className="bg-white rounded shadow p-4">
            <div className="mb-4 pb-3 border-b flex justify-between items-start">
              <div>
                <p className="font-semibold text-lg">{selected.firstName} {selected.lastName}</p>
                <p className="text-slate-500 text-sm font-mono">{selected.dui}
                  {calcAge(selected.birthDate) !== null && (
                    <span className="ml-3 font-sans">{calcAge(selected.birthDate)} años</span>
                  )}
                </p>
              </div>
              <Link to={`/pacientes/${selected.id}/expediente`}
                    className="text-xs text-blue-600 hover:underline">
                Ver expediente →
              </Link>
            </div>

            <div className="flex gap-2 mb-3">
              <button type="button" onClick={aplicarPresetNormal}
                      className="text-xs px-3 py-1.5 border rounded hover:bg-emerald-50 text-emerald-700 border-emerald-300">
                Signos normales
              </button>
              <button type="button" onClick={limpiar}
                      className="text-xs px-3 py-1.5 border rounded hover:bg-slate-50 text-slate-600">
                Limpiar
              </button>
              <span className="ml-auto text-xs text-slate-400">Ctrl+Enter para guardar</span>
            </div>

            {error && <ErrorAlert message={error} />}
            {ok && <p className="text-sm text-green-700 bg-green-50 p-2 rounded mb-3">{ok}</p>}

            <form onSubmit={submit} onKeyDown={onKeyDown} className="flex flex-col gap-3">
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="text-xs text-slate-500 block mb-1">Temperatura (°C)</label>
                  <input type="number" step="0.1" min="30" max="45"
                         placeholder="36.7"
                         value={form.temperatureC}
                         onChange={e => set('temperatureC', e.target.value)}
                         className={`border rounded p-2 w-full ${rangoClase('temperatureC', form.temperatureC)}`} />
                </div>
                <div>
                  <label className="text-xs text-slate-500 block mb-1">Frec. cardíaca (lpm)</label>
                  <input type="number" min="20" max="300"
                         placeholder="72"
                         value={form.heartRate}
                         onChange={e => set('heartRate', e.target.value)}
                         className={`border rounded p-2 w-full ${rangoClase('heartRate', form.heartRate)}`} />
                </div>
                <div>
                  <label className="text-xs text-slate-500 block mb-1">Presión arterial</label>
                  <input placeholder="120/80"
                         value={form.bloodPressure}
                         onChange={e => set('bloodPressure', e.target.value)}
                         className={`border rounded p-2 w-full ${presionClase(form.bloodPressure)}`} />
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
              </div>

              {imc && (
                <div className={`rounded px-3 py-2 text-sm flex justify-between ${imc.color}`}>
                  <span>IMC calculado: <b>{imc.valor}</b></span>
                  <span>{imc.etiqueta}</span>
                </div>
              )}

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

          {/* Historial */}
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
