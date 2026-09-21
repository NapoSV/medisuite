import { useEffect, useState } from 'react';
import { api } from '../api/client';

type Patient = { id: number; firstName: string; lastName: string; dui: string; phone: string };

export default function Pacientes() {
    const [rows, setRows] = useState<Patient[]>([]);
    const [q, setQ] = useState('');

    const load = () => api.get<Patient[] | { content: Patient[] }>(`/patients?search=${q}`)
        .then(r => setRows(Array.isArray(r.data) ? r.data : (r.data.content ?? [])));
    useEffect(() => { load(); }, []);

    return (
        <div className="p-4">
            <div className="flex flex-wrap items-center justify-between gap-2 mb-4">
                <h2 className="text-2xl font-bold">Pacientes</h2>
                <button className="bg-blue-600 text-white px-4 py-2 rounded">+ Nuevo paciente</button>
            </div>
            <div className="flex flex-col sm:flex-row gap-2 mb-4">
                <input value={q} onChange={e => setQ(e.target.value)}
                       placeholder="Buscar por nombre o DUI (########-#)"
                       className="w-full sm:flex-1 border rounded px-3 py-2"/>
                <button onClick={load} className="bg-slate-800 text-white px-4 py-2 rounded">Buscar</button>
            </div>
            <div className="overflow-x-auto">
                <table className="w-full min-w-[480px] bg-white rounded shadow">
                    <thead>
                    <tr className="border-b">
                        <th className="text-left p-3">Nombre</th>
                        <th className="text-left p-3">DUI</th>
                        <th className="text-left p-3">Telefono</th>
                        <th></th>
                    </tr>
                    </thead>
                    <tbody>
                    {rows.map(p => (
                        <tr key={p.id} className="border-b">
                            <td className="p-3">{p.firstName} {p.lastName}</td>
                            <td className="p-3 font-mono">{p.dui}</td>
                            <td className="p-3">{p.phone}</td>
                            <td className="p-3 text-right"><a className="text-blue-600" href={`/pacientes/${p.id}`}>Ver</a></td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
}