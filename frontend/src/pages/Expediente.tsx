import { useEffect, useState } from 'react'
import LoadingSpinner from '../components/LoadingSpinner'
import { useParams, useNavigate } from 'react-router-dom'
import { getMedicalRecord, createVitalSign, type Expediente as Exp, type SignosVitales, type NuevoTriaje } from '../api/medicalRecords'
import { useAuth } from '../auth/useAuth'
import { ApiError } from '../api/http'
import PatientHeader from '../components/expediente/PatientHeader'
import ClinicalTabs from '../components/expediente/ClinicalTabs'
import { panelId, tabId, type TabId } from '../components/expediente/tabIds'
import VitalCards from '../components/expediente/VitalCards'
import PrescriptionPanel from '../components/expediente/PrescriptionPanel'
import { PRIORITIES, PRIORITY_DEFAULT } from '../lib/priority'

type Evento =
    | { tipo: 'receta'; fecha: string; data: Exp['prescriptions'][0] }
    | { tipo: 'signos'; fecha: string; data: SignosVitales }

const INITIAL_TRIAJE = {
  temperatureC: '', heartRate: '', bloodPressure: '',
  weightKg: '', heightCm: '', symptoms: '', priority: PRIORITY_DEFAULT as string,
}
type TriajeForm = typeof INITIAL_TRIAJE

function parseOptional(raw: string, label: string, min: number, max: number, integer = false):
    { value?: number; error?: string } {
  const t = raw.trim()
  if (t === '') return {}
  const n = Number(t)
  if (!Number.isFinite(n)) return { error: `${label}: ingresa un número válido.` }
  if (integer && !Number.isInteger(n)) return { error: `${label}: debe ser un número entero.` }
  if (n < min || n > max) return { error: `${label}: debe estar entre ${min} y ${max}.` }
  return { value: n }
}

function buildTriaje(f: TriajeForm): { data: NuevoTriaje } | { error: string } {
  const temp = parseOptional(f.temperatureC, 'Temperatura (°C)', 30, 45)
  const hr = parseOptional(f.heartRate, 'Frecuencia cardíaca (lpm)', 20, 300, true)
  const w = parseOptional(f.weightKg, 'Peso (kg)', 0.5, 500)
  const h = parseOptional(f.heightCm, 'Talla (cm)', 20, 280)
  const bad = [temp, hr, w, h].find(r => r.error)
  if (bad) return { error: bad.error! }

  const bp = f.bloodPressure.trim()
  if (bp && !/^\d{2,3}\/\d{2,3}$/.test(bp)) return { error: 'Presión arterial: usa el formato 120/80.' }

  return {
    data: {
      temperatureC: temp.value, heartRate: hr.value, bloodPressure: bp || undefined,
      weightKg: w.value, heightCm: h.value,
      symptoms: f.symptoms.trim() || undefined, priority: f.priority,
    },
  }
}

type LoadError = 'invalid-id' | 'unauthorized' | 'forbidden' | 'not-found' | 'error'

const LOAD_ERROR_TEXT: Record<Exclude<LoadError, 'unauthorized'>, string> = {
  'invalid-id': 'La dirección del expediente no es válida.',
  forbidden: 'No tienes permiso para ver este expediente.',
  'not-found': 'No se encontró el expediente.',
  error: 'No se pudo cargar el expediente. Intenta de nuevo.',
}

function toLoadError(err: unknown): LoadError {
  if (err instanceof ApiError) {
    if (err.status === 401) return 'unauthorized'
    if (err.status === 403) return 'forbidden'
    if (err.status === 404) return 'not-found'
  }
  return 'error'
}

export default function Expediente() {
  const { id } = useParams()
  const navigate = useNavigate()
  const { user } = useAuth()
  const canTriage = user?.role === 'NURSE' || user?.role === 'DOCTOR'
  const canPrescribe = user?.role === 'DOCTOR'

  const [rec, setRec] = useState<Exp | null>(null)
  const [loading, setLoading] = useState(true)
<<<<<<< HEAD
  const [loadError, setLoadError] = useState<LoadError | null>(null)
=======
  const [loadedPatientId, setLoadedPatientId] = useState<number | null>(null)
>>>>>>> feature/avance3-fase-b
  const [showTriaje, setShowTriaje] = useState(false)
  const [triajeForm, setTriajeForm] = useState<TriajeForm>(INITIAL_TRIAJE)
  const [saving, setSaving] = useState(false)
  const [triajeOk, setTriajeOk] = useState<string | null>(null)
  const [triajeErr, setTriajeErr] = useState<string | null>(null)
  const [tab, setTab] = useState<TabId>('resumen')

  const patientId = /^[1-9]\d*$/.test(id ?? '') ? Number(id) : null

  const load = (silent = false) => {
    if (patientId === null) {
      setRec(null); setLoadError('invalid-id'); setLoading(false)
      return
    }
    if (!silent) setLoading(true)
    getMedicalRecord(patientId)
        .then(r => { setRec(r); setLoadError(null) })
        .catch(err => {
          const kind = toLoadError(err)
          if (kind === 'unauthorized') { setLoadError(kind); return }
          if (silent) {
            setTriajeOk('Signos vitales registrados, pero no se pudo actualizar la lista. Recarga la página.')
            return
          }
          setRec(null)
          setLoadError(kind)
        })
        .finally(() => setLoading(false))
  }

  useEffect(() => {
    let activo = true

    getMedicalRecord(patientId)
      .then(data => {
        if (activo) setRec(data)
      })
      .catch(() => {
        if (activo) setRec(null)
      })
      .finally(() => {
        if (!activo) return
        setLoadedPatientId(patientId)
        setLoading(false)
      })

    return () => {
      activo = false
    }
  }, [patientId])
  const setT = (k: string, v: string) => setTriajeForm(f => ({ ...f, [k]: v }))

  const submitTriaje = async (e: React.FormEvent) => {
    e.preventDefault()
    if (patientId === null) return
    setTriajeErr(null); setTriajeOk(null)

    const parsed = buildTriaje(triajeForm)
    if ('error' in parsed) { setTriajeErr(parsed.error); return }

    setSaving(true)
    try {
      await createVitalSign(patientId, parsed.data)
      setTriajeOk('Signos vitales registrados correctamente.')
      setTriajeForm(INITIAL_TRIAJE)
      setShowTriaje(false)
      load(true)
    } catch (err) {
      if (err instanceof ApiError) {
        if (err.status === 401) return // apiFetch ya redirige a /login
        if (err.status === 403) setTriajeErr('No tienes permiso para registrar signos vitales.')
        else if (err.status === 400 || err.status === 422) setTriajeErr(err.message)
        else setTriajeErr(`No se pudieron guardar los signos vitales (error ${err.status}).`)
      } else {
        setTriajeErr('No se pudo conectar con el servidor. Los signos vitales no fueron registrados.')
      }
    } finally {
      setSaving(false)
    }
  }

<<<<<<< HEAD
  if (loading) return <LoadingSpinner label="Cargando expediente..." />
  if (loadError === 'unauthorized') {
    return <LoadingSpinner label="Sesión expirada. Redirigiendo al inicio de sesión..." />
  }
  if (loadError || !rec) {
    return <p role="alert" className="p-4 text-red-600">{LOAD_ERROR_TEXT[loadError ?? 'error']}</p>
  }
=======
    if (loading || loadedPatientId !== patientId) {
    return <LoadingSpinner label="Cargando expediente..." />
  }
  if (!rec) return <p className="p-4 text-red-600">No se encontró el expediente.</p>
>>>>>>> feature/avance3-fase-b

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
  const age = calcAge()

  const eventos: Evento[] = [
    ...rec.prescriptions.map(p => ({ tipo: 'receta' as const, fecha: p.issuedOn, data: p })),
    ...rec.vitalSigns.map(v => ({ tipo: 'signos' as const, fecha: v.recordedAt, data: v })),
  ].sort((a, b) => b.fecha.localeCompare(a.fecha))

  const vitalsDesc = [...rec.vitalSigns].sort((a, b) => b.recordedAt.localeCompare(a.recordedAt))
  const lastVitals = vitalsDesc[0]

  return (
      <div className="max-w-3xl">
        <PatientHeader
            patient={patient}
            age={age}
            canPrescribe={canPrescribe}
            canTriage={canTriage}
            showTriaje={showTriaje}
            onNewPrescription={() => navigate(`/recetas/${rec.id}`)}
            onToggleTriaje={() => {
              setShowTriaje(!showTriaje)
              setTriajeOk(null)
              setTriajeErr(null)
            }}
        />

        {/* Formulario de triaje inline */}
        {showTriaje && (
            <div className="mt-4 bg-emerald-50 border border-emerald-200 rounded p-4">
              <h3 className="font-semibold text-emerald-800 mb-3">Registrar signos vitales</h3>
              {triajeErr && <p role="alert" className="text-red-600 text-sm mb-2">{triajeErr}</p>}
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
                    {PRIORITIES.map(p => (
                        <option key={p.value} value={p.value}>{p.label}</option>
                    ))}
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

        <div className="mt-6">
          <ClinicalTabs value={tab} onChange={setTab} />

          <div role="tabpanel" id={panelId(tab)} aria-labelledby={tabId(tab)} tabIndex={0} className="pt-4">
            {tab === 'resumen' && (
                <div className="flex flex-col gap-6">
                  <section>
                    <h3 className="text-sm font-semibold text-slate-600 mb-2">Últimos signos vitales</h3>
                    {lastVitals
                        ? <VitalCards data={lastVitals} showDate />
                        : <p className="text-slate-500 text-sm">Sin signos vitales registrados.</p>}
                  </section>
                  <section>
                    <h3 className="text-sm font-semibold text-slate-600 mb-2">Última receta</h3>
                    <PrescriptionPanel prescriptions={rec.prescriptions} limit={1} />
                  </section>
                  {rec.generalNotes?.trim() && (
                      <section>
                        <h3 className="text-sm font-semibold text-slate-600 mb-2">Notas generales</h3>
                        <p className="text-sm whitespace-pre-line break-words">{rec.generalNotes}</p>
                      </section>
                  )}
                </div>
            )}

            {tab === 'historial' && (
                eventos.length === 0 ? (
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
                                <VitalCards data={ev.data} />
                            )}
                          </li>
                      ))}
                    </ol>
                )
            )}

            {tab === 'signos' && (
                vitalsDesc.length === 0 ? (
                    <p className="text-slate-500 text-sm">Sin signos vitales registrados.</p>
                ) : (
                    <div className="flex flex-col gap-3">
                      {vitalsDesc.map(v => <VitalCards key={v.id} data={v} showDate />)}
                    </div>
                )
            )}

            {tab === 'recetas' && <PrescriptionPanel prescriptions={rec.prescriptions} />}

            {tab === 'documentos' && (
                <p className="text-slate-500 text-sm">No disponible en esta fase.</p>
            )}
          </div>
        </div>
      </div>
  )
}