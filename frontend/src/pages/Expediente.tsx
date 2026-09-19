import { useEffect, useState } from 'react'
import LoadingSpinner from '../components/LoadingSpinner'
import { useParams } from 'react-router-dom'
import { getMedicalRecord, type Expediente as Exp } from '../api/medicalRecords'

// MOCK temporal — quitar cuando entre el PR de expediente (Ventura)
const MOCK: Exp = {
  id: 1,
  patient: { firstName: 'Ana', lastName: 'Ramirez', dui: '01234567-8', birthDate: '1991-04-12' },
  bloodType: 'O+',
  allergies: 'Penicilina',
  prescriptions: [
    { id: 1, emittedAt: '2026-08-20T10:00:00',
      doctor: { user: { firstName: 'Carlos', lastName: 'Mejia' } },
      items: [{ medication: 'Amoxicilina 500mg', dosage: '1 cada 8h por 7 dias' }] },
    { id: 2, emittedAt: '2026-09-02T11:30:00',
      doctor: { user: { firstName: 'Sofia', lastName: 'Hernandez' } },
      items: [{ medication: 'Ibuprofeno 400mg', dosage: '1 cada 12h por 3 dias' },
              { medication: 'Omeprazol 20mg', dosage: '1 en ayunas' }] },
  ],
  vitalSigns: [
    { id: 1, recordedAt: '2026-08-20T09:50:00', temperature: 38.2, heartRate: 92, bloodPressure: '120/80' },
    { id: 2, recordedAt: '2026-09-02T11:15:00', temperature: 36.8, heartRate: 74, bloodPressure: '118/76' },
  ],
}

type Evento =
  | { tipo: 'receta'; fecha: string; data: Exp['prescriptions'][0] }
  | { tipo: 'signos'; fecha: string; data: Exp['vitalSigns'][0] }

export default function Expediente() {
  const { id } = useParams()
  const [rec, setRec] = useState<Exp | null>(null)
  const [loading, setLoading] = useState(true)
  const [aviso, setAviso] = useState<string | null>(null)

  useEffect(() => {
    getMedicalRecord(Number(id ?? 1))
      .then(setRec)
      .catch(() => { setRec(MOCK); setAviso('Backend de expediente no disponible — mostrando datos de ejemplo') })
      .finally(() => setLoading(false))
  }, [id])

  if (loading) return <LoadingSpinner label="Cargando expediente..." />
  if (!rec) return <p className="p-4 text-red-600">No se encontro el expediente.</p>

  const eventos: Evento[] = [
    ...rec.prescriptions.map(p => ({ tipo: 'receta' as const, fecha: p.emittedAt, data: p })),
    ...rec.vitalSigns.map(v => ({ tipo: 'signos' as const, fecha: v.recordedAt, data: v })),
  ].sort((a, b) => b.fecha.localeCompare(a.fecha))

  return (
    <div className="max-w-3xl">
      <h2 className="text-2xl font-bold">
        Expediente: {rec.patient.firstName} {rec.patient.lastName}
      </h2>

      <div className="mt-2 flex flex-wrap gap-4 text-sm text-slate-600">
        <span>DUI: <b className="font-mono">{rec.patient.dui}</b></span>
        {rec.bloodType && <span>Tipo de sangre: <b>{rec.bloodType}</b></span>}
        {rec.allergies && <span className="text-red-700">Alergias: <b>{rec.allergies}</b></span>}
      </div>

      {aviso && <p className="mt-4 text-sm text-amber-700 bg-amber-50 p-2 rounded">{aviso}</p>}

      <h3 className="text-lg font-semibold mt-8 mb-4">Historial clinico</h3>

      {eventos.length === 0 ? (
        <p className="text-slate-500">Este paciente no tiene registros todavia.</p>
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
                    Receta — Dr. {ev.data.doctor.user.firstName} {ev.data.doctor.user.lastName}
                  </p>
                  <ul className="mt-2 text-sm list-disc ml-5">
                    {ev.data.items.map((it, i) => (
                      <li key={i}>{it.medication} — <span className="text-slate-600">{it.dosage}</span></li>
                    ))}
                  </ul>
                </div>
              ) : (
                <div className="bg-white rounded shadow p-3 mt-1">
                  <p className="font-semibold text-sm">Signos vitales</p>
                  <div className="mt-2 flex flex-wrap gap-4 text-sm">
                    <span>Temp: <b>{ev.data.temperature}°C</b></span>
                    <span>FC: <b>{ev.data.heartRate} lpm</b></span>
                    {ev.data.bloodPressure && <span>PA: <b>{ev.data.bloodPressure}</b></span>}
                  </div>
                </div>
              )}
            </li>
          ))}
        </ol>
      )}
    </div>
  )
}