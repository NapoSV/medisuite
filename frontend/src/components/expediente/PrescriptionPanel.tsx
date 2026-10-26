import { Link } from 'react-router-dom'
import type { Receta } from '../../api/medicalRecords'
import { formatDateOnly } from '../../lib/dates'

type Props = { prescriptions: Receta[]; limit?: number }

export default function PrescriptionPanel({ prescriptions, limit }: Props) {
    const sorted = [...prescriptions].sort((a, b) => b.issuedOn.localeCompare(a.issuedOn))
    const visible = limit ? sorted.slice(0, limit) : sorted

    if (visible.length === 0) {
        return <p className="text-slate-500 text-sm">Este paciente no tiene recetas todavía.</p>
    }

    return (
        <ul className="flex flex-col gap-3">
            {visible.map(r => (
                <li key={r.id} className="bg-white rounded shadow p-3">
                    <div className="flex flex-wrap justify-between items-start gap-2">
                        <div className="min-w-0">
                            <p className="font-semibold text-sm break-words">
                                Receta · Dr. {r.doctor.user.firstName} {r.doctor.user.lastName}
                            </p>
                            <p className="text-xs text-slate-500">
                                {formatDateOnly(r.issuedOn)}
                            </p>
                        </div>
                        <Link to={`/receta-print/${r.id}`}
                              aria-label={`Imprimir receta ${r.id}`}
                              className="text-blue-600 text-sm hover:underline shrink-0">
                            Imprimir
                        </Link>
                    </div>

                    {r.items.length === 0 ? (
                        <p className="mt-2 text-xs text-slate-500">Sin medicamentos registrados.</p>
                    ) : (
                        <ul className="mt-2 text-sm list-disc ml-5">
                            {r.items.map((it, i) => (
                                <li key={i} className="break-words">
                                    {it.medication}
                                    {it.dosage && <span className="text-slate-600">: {it.dosage}</span>}
                                </li>
                            ))}
                        </ul>
                    )}
                </li>
            ))}
        </ul>
    )
}
