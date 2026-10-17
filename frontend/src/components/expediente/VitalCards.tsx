import type { SignosVitales } from '../../api/medicalRecords'
import { priorityBadge, priorityLabel } from '../../lib/priority'

type Props = { data: SignosVitales; showDate?: boolean }

export default function VitalCards({ data: v, showDate = false }: Props) {
    const priority = v.priority?.trim()
    const symptoms = v.symptoms?.trim()

    const measures: { label: string; value: number | string | null | undefined; suffix: string }[] = [
        { label: 'Temp', value: v.temperatureC, suffix: '°C' },
        { label: 'FC', value: v.heartRate, suffix: ' lpm' },
        { label: 'PA', value: v.bloodPressure?.trim(), suffix: '' },
        { label: 'Peso', value: v.weightKg, suffix: ' kg' },
        { label: 'Talla', value: v.heightCm, suffix: ' cm' },
    ].filter(m => m.value != null && m.value !== '')

    return (
        <div className="bg-white rounded shadow p-3 mt-1">
            <div className="flex justify-between items-center gap-2">
                <div className="min-w-0">
                    <p className="font-semibold text-sm">Signos vitales</p>
                    {showDate && (
                        <p className="text-xs text-slate-500">
                            {new Date(v.recordedAt).toLocaleString('es-SV')}
                        </p>
                    )}
                </div>
                {priority && (
                    <span className={`shrink-0 px-2 py-0.5 rounded text-xs font-medium ${priorityBadge(priority)}`}>
                        {priorityLabel(priority)}
                    </span>
                )}
            </div>

            {measures.length > 0 ? (
                <div className="mt-2 flex flex-wrap gap-x-4 gap-y-1 text-sm">
                    {measures.map(m => (
                        <span key={m.label}>{m.label}: <b>{m.value}{m.suffix}</b></span>
                    ))}
                </div>
            ) : (
                !symptoms && <p className="mt-2 text-xs text-slate-500">Sin mediciones registradas.</p>
            )}

            {symptoms && (
                <p className="mt-2 text-slate-500 text-xs italic break-words">"{symptoms}"</p>
            )}
        </div>
    )
}