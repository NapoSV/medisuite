import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { api } from '../api/client';

export default function RecetaPrint() {
    const { id } = useParams();
    const [receta, setReceta] = useState<any>(null);
    useEffect(() => { api.get(`/api/prescriptions/${id}`).then(r => setReceta(r.data)); }, [id]);
    if (!receta) return <p>Cargando...</p>;
    return (
        <div className="print-only max-w-[21cm] mx-auto p-8 font-serif">
            <h1 className="text-2xl font-bold mb-1">MediSuite · Receta Médica</h1>
            <p className="text-sm text-slate-600 mb-4">Fecha: {new Date(receta.issuedOn).toLocaleDateString('es-SV')}</p>
            <p><b>Paciente:</b> {receta.patient?.firstName} {receta.patient?.lastName} · DUI: {receta.patient?.dui}</p>
            <p><b>Doctor:</b> Dr. {receta.doctor?.user?.firstName} {receta.doctor?.user?.lastName}</p>
            <hr className="my-4"/>
            <table className="w-full text-sm">
                <thead><tr><th className="text-left">Medicamento</th><th>Dosis</th><th>Frecuencia</th></tr></thead>
                <tbody>
                {receta.items?.map((i: any, idx: number) => (
                    <tr key={idx}><td>{i.medicationName}</td><td>{i.dosage}</td><td>{i.frequency}</td></tr>
                ))}
                </tbody>
            </table>
            <p className="mt-8 text-xs text-slate-500">Firma del médico: ___________________________</p>
            <button onClick={() => window.print()} className="no-print mt-4 bg-blue-600 text-white px-4 py-2 rounded">
                Imprimir
            </button>
        </div>
    );
}