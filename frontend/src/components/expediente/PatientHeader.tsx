import type { ExpedientePatient } from '../../api/medicalRecords'
type Props = {
    patient: ExpedientePatient
    age: number | null
    canPrescribe: boolean
    canTriage: boolean
    showTriaje: boolean
    onNewPrescription: () => void
    onToggleTriaje: () => void
}

export default function PatientHeader({
                                          patient, age, canPrescribe, canTriage, showTriaje, onNewPrescription, onToggleTriaje,
                                      }: Props) {
    const bloodType = patient.bloodType?.trim()
    const allergies = patient.allergies?.trim()

    return (
        <header className="flex flex-col gap-2">
            <div className="flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between">
                <h2 className="min-w-0 break-words text-2xl font-bold">
                    Expediente: {patient.firstName} {patient.lastName}
                </h2>

                {(canPrescribe || canTriage) && (
                    <div className="flex flex-wrap gap-2 sm:shrink-0">
                        {canPrescribe && (
                            <button type="button" onClick={onNewPrescription}
                                    className="bg-blue-600 text-white text-sm px-3 py-2 rounded">
                                + Nueva receta
                            </button>
                        )}
                        {canTriage && (
                            <button type="button" onClick={onToggleTriaje}
                                    className="bg-emerald-600 text-white text-sm px-3 py-2 rounded">
                                {showTriaje ? 'Cerrar triaje' : '+ Registrar signos vitales'}
                            </button>
                        )}
                    </div>
                )}
            </div>

            <div className="flex flex-wrap gap-x-4 gap-y-1 text-sm text-slate-600">
                <span>DUI: <b className="font-mono">{patient.dui}</b></span>
                {age !== null && <span>Edad: <b>{age} años</b></span>}
                {bloodType && <span>Tipo de sangre: <b>{bloodType}</b></span>}
            </div>

            {allergies && (
                <p className="text-sm text-red-700 break-words">
                    Alergias: <b>{allergies}</b>
                </p>
            )}
        </header>
    )
}