import { useEffect, useMemo, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { FileText, Search } from 'lucide-react'
import { api } from '../api/client'
import { useAuth } from '../auth/useAuth'

type Paciente = {
  id: number
  firstName: string
  lastName: string
  dui: string
}

/**
 * Punto de entrada visible a recetas (bug reportado: "el boton de recetas no se ve").
 * Buscar paciente -> abrir expediente para prescribir.
 * El expediente ya tiene el boton "+ Nueva receta" para DOCTOR.
 */
export default function RecetasInicio() {
  const { user } = useAuth()
  const navigate = useNavigate()
  const esDoctor = user?.role === 'DOCTOR'
  const [patients, setPatients] = useState<Paciente[]>([])
  const [q, setQ] = useState('')
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    api.get<{ content: Paciente[] }>('/api/patients?size=200')
      .then(r => setPatients(r.data.content))
      .catch(() => setPatients([]))
      .finally(() => setLoading(false))
  }, [])

  const resultados = useMemo(() => {
    const query = q.trim().toLowerCase()
    if (!query) return patients.slice(0, 20)
    const digits = query.replace(/\D/g, '')
    return patients.filter(p =>
      `${p.firstName} ${p.lastName}`.toLowerCase().includes(query) ||
      (digits && p.dui.replace('-', '').includes(digits))
    ).slice(0, 20)
  }, [patients, q])

  const abrirExpediente = (id: number) => navigate(`/pacientes/${id}/expediente`)

  return (
    <div className="max-w-2xl">
      <div className="flex items-center gap-3 mb-2">
        <FileText className="w-6 h-6 text-blue-600" />
        <h2 className="text-2xl font-bold">Recetas médicas</h2>
      </div>
      <p className="text-sm text-slate-500 mb-6">
        {esDoctor
          ? 'Busca un paciente para abrir su expediente y emitir una nueva receta.'
          : 'Busca un paciente para ver sus recetas en el expediente.'}
      </p>

      <div className="relative mb-4">
        <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400" />
        <input
          autoFocus
          value={q}
          onChange={e => setQ(e.target.value)}
          placeholder="Nombre o DUI del paciente..."
          className="w-full pl-10 pr-4 py-2.5 border rounded"
        />
      </div>

      {loading ? (
        <p className="text-slate-500 text-sm">Cargando pacientes...</p>
      ) : resultados.length === 0 ? (
        <p className="text-slate-500 text-sm">
          {q ? `Sin resultados para "${q}".` : 'No hay pacientes registrados.'}
        </p>
      ) : (
        <ul className="bg-white rounded shadow divide-y">
          {resultados.map(p => (
            <li key={p.id}>
              <button
                onClick={() => abrirExpediente(p.id)}
                className="w-full text-left px-4 py-3 hover:bg-blue-50 flex items-center justify-between"
              >
                <span>
                  <span className="font-medium">{p.firstName} {p.lastName}</span>
                  <span className="text-slate-400 ml-2 font-mono text-xs">{p.dui}</span>
                </span>
                <span className="text-xs text-blue-600">
                  {esDoctor ? 'Abrir expediente para prescribir →' : 'Ver expediente →'}
                </span>
              </button>
            </li>
          ))}
        </ul>
      )}
    </div>
  )
}
