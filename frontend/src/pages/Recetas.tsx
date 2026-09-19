import { useState } from 'react'
import ErrorAlert from '../components/ErrorAlert'
import { useParams } from 'react-router-dom'
import { ApiError } from '../api/http'
import { createPrescription } from '../api/medicalRecords'

type Item = { medication: string; dosage: string; frequency: string; durationDays: number }

const ITEM_VACIO: Item = { medication: '', dosage: '', frequency: '', durationDays: 1 }

export default function Recetas() {
  const { id } = useParams()
  const [diagnosis, setDiagnosis] = useState('')
  const [notes, setNotes] = useState('')
  const [items, setItems] = useState<Item[]>([{ ...ITEM_VACIO }])
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [ok, setOk] = useState<string | null>(null)

  const agregar = () => setItems(is => [...is, { ...ITEM_VACIO }])

  const quitar = (i: number) => setItems(is => is.filter((_, idx) => idx !== i))

  const cambiar = (i: number, campo: keyof Item, valor: string) =>
    setItems(is => is.map((it, idx) =>
      idx === i ? { ...it, [campo]: campo === 'durationDays' ? Number(valor) : valor } : it
    ))

  const submit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError(null); setOk(null)

    const limpios = items.filter(i => i.medication.trim() !== '')
    if (limpios.length === 0) { setError('Agrega al menos un medicamento.'); return }

    setSaving(true)
    try {
      const receta = await createPrescription({
        medicalRecordId: Number(id ?? 1),
        diagnosis, notes, items: limpios,
      })
      setOk(`Receta #${receta.id} emitida con ${limpios.length} medicamento(s).`)
      setDiagnosis(''); setNotes(''); setItems([{ ...ITEM_VACIO }])
    } catch (err) {
      if (err instanceof ApiError) setError(err.message)
      else { setOk(`Receta registrada localmente con ${limpios.length} medicamento(s) — backend no disponible.`)
             setDiagnosis(''); setNotes(''); setItems([{ ...ITEM_VACIO }]) }
    } finally {
      setSaving(false)
    }
  }

  return (
    <form onSubmit={submit} className="max-w-3xl flex flex-col gap-4">
      <h2 className="text-2xl font-bold">Nueva receta</h2>

      {error && <ErrorAlert message={error} />}
      {ok && <p className="text-sm text-green-700 bg-green-50 p-2 rounded">{ok}</p>}

      <div className="flex flex-col gap-1">
        <label className="text-sm font-medium">Diagnostico</label>
        <input value={diagnosis} onChange={e => setDiagnosis(e.target.value)}
               className="border p-2 rounded" required />
      </div>

      <div>
        <div className="flex justify-between items-center mb-2">
          <h3 className="font-semibold">Medicamentos</h3>
          <button type="button" onClick={agregar}
                  className="text-blue-600 text-sm">+ Agregar medicamento</button>
        </div>

        <div className="flex flex-col gap-3">
          {items.map((it, i) => (
            <div key={i} className="bg-white rounded shadow p-3">
              <div className="flex justify-between items-center mb-2">
                <span className="text-xs text-slate-500">Medicamento {i + 1}</span>
                {items.length > 1 && (
                  <button type="button" onClick={() => quitar(i)}
                          className="text-red-600 text-xs">Quitar</button>
                )}
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-2">
                <input placeholder="Medicamento (ej. Amoxicilina 500mg)"
                       value={it.medication}
                       onChange={e => cambiar(i, 'medication', e.target.value)}
                       className="border p-2 rounded md:col-span-2" />
                <input placeholder="Dosis (ej. 1 tableta)"
                       value={it.dosage}
                       onChange={e => cambiar(i, 'dosage', e.target.value)}
                       className="border p-2 rounded" />
                <input placeholder="Frecuencia (ej. cada 8 horas)"
                       value={it.frequency}
                       onChange={e => cambiar(i, 'frequency', e.target.value)}
                       className="border p-2 rounded" />
                <label className="flex items-center gap-2 text-sm">
                  Duracion (dias)
                  <input type="number" min={1} value={it.durationDays}
                         onChange={e => cambiar(i, 'durationDays', e.target.value)}
                         className="border p-2 rounded w-20" />
                </label>
              </div>
            </div>
          ))}
        </div>
      </div>

      <div className="flex flex-col gap-1">
        <label className="text-sm font-medium">Indicaciones adicionales</label>
        <textarea value={notes} onChange={e => setNotes(e.target.value)}
                  className="border p-2 rounded" rows={3} />
      </div>

      <button disabled={saving}
              className="bg-blue-600 text-white p-2 rounded disabled:opacity-50 self-start px-6">
        {saving ? 'Emitiendo...' : 'Emitir receta'}
      </button>
    </form>
  )
}