import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { api } from '../api/client';
import { formatDateOnly } from '../lib/dates';
import ErrorAlert from '../components/ErrorAlert';

type RecetaImprimible = {
    id: number;
    issuedOn: string;
    patient: { firstName: string; lastName: string; dui: string };
    doctor: { fullName: string; licenseNumber: string };
    instructions?: string;
    items: {
        medication: string;
        dosage?: string;
        frequency?: string;
        durationDays?: number;
    }[];
};

export default function RecetaPrint() {
    const { id } = useParams();
    const [receta, setReceta] = useState<RecetaImprimible | null>(null);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        api.get<RecetaImprimible>(`/api/prescriptions/${id}/print`)
            .then(r => setReceta(r.data))
            .catch(() => setError('No se pudo cargar la receta. Verifica el enlace y vuelve a intentar.'));
    }, [id]);

    if (error) return <ErrorAlert message={error} />;
    if (!receta) return <p>Cargando...</p>;

    return (
        <div className="print-only max-w-[21cm] mx-auto p-8 font-serif">
            <h1 className="text-2xl font-bold mb-1">
                MediSuite · Receta Médica
            </h1>
            <p className="text-sm text-slate-600 mb-4">
                Fecha: {formatDateOnly(receta.issuedOn)}
            </p>
            <p>
                <b>Paciente:</b> {receta.patient?.firstName}{' '}
                {receta.patient?.lastName} · DUI: {receta.patient?.dui}
            </p>
            <p>
                <b>Doctor:</b> Dr. {receta.doctor.fullName} · Matrícula: {receta.doctor.licenseNumber}
            </p>
            <hr className="my-4" />
            <table className="w-full text-sm">
                <thead>
                    <tr>
                        <th className="text-left">Medicamento</th>
                        <th>Dosis</th>
                        <th>Frecuencia</th>
                        <th>Duración (días)</th>
                    </tr>
                </thead>
                <tbody>
                    {receta.items?.map((item, idx) => (
                        <tr key={idx}>
                            <td>{item.medication}</td>
                            <td>{item.dosage}</td>
                            <td>{item.frequency}</td>
                            <td>{item.durationDays}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
            {receta.instructions && <p className="mt-4 whitespace-pre-wrap"><b>Indicaciones:</b> {receta.instructions}</p>}
            <p className="mt-8 text-xs text-slate-500">
                Firma del médico: ___________________________
            </p>
            <button
                onClick={() => window.print()}
                className="no-print mt-4 bg-blue-600 text-white px-4 py-2 rounded"
            >
                Imprimir
            </button>
        </div>
    );
}
