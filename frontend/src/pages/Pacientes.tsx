import { useEffect, useState } from 'react';
import { api } from '../api/client';
import PatientModal from '../components/patients/PatientModal';

type Patient = { id: number; firstName: string; lastName: string; dui: string; phone: string };

export default function Pacientes() {
    const [rows, setRows] = useState<Patient[]>([]);
    const [q, setQ] = useState('');
    const [modalOpen, setModalOpen] = useState(false);
    const [editId, setEditId] = useState<number | undefined>(undefined);

    const load = () => api.get<Patient[]>(`/patients?search=${q}`).then(r => setRows(r.data));
    useEffect(() => { load(); }, []);

    const openCreate = () => { setEditId(undefined); setModalOpen(true); };
    const openEdit   = (id: number) => { setEditId(id); setModalOpen(true); };
    const closeModal = () => setModalOpen(false);

    return (
        <div>
            <div className="flex justify-between mb-4">
                <h2 className="text-2xl font-bold">Pacientes</h2>
                <button onClick={openCreate} className="bg-blue-600 text-white px-4 py-2 rounded">+ Nuevo paciente</button>
            </div>
            <div className="flex gap-2 mb-4">
                <input value={q} onChange={e => setQ(e.target.value)}
                       placeholder="Buscar por nombre o DUI (########-#)"
                       className="flex-1 border rounded px-3 py-2"/>
                <button onClick={load} className="bg-slate-800 text-white px-4 rounded">Buscar</button>
            </div>
            <table className="w-full bg-white rounded shadow">
                <thead>
                <tr className="border-b">
                    <th className="text-left p-3">Nombre</th>
                    <th className="text-left p-3">DUI</th>
                    <th className="text-left p-3">Teléfono</th>
                    <th></th>
                </tr>
                </thead>
                <tbody>
                {rows.map(p => (
                    <tr key={p.id} className="border-b hover:bg-slate-50">
                        <td className="p-3">{p.firstName} {p.lastName}</td>
                        <td className="p-3 font-mono">{p.dui}</td>
                        <td className="p-3">{p.phone}</td>
                        <td className="p-3 text-right flex gap-3 justify-end">
                            <button onClick={() => openEdit(p.id)} className="text-blue-600 hover:underline text-sm">Editar</button>
                            <a className="text-slate-600 hover:underline text-sm" href={`/pacientes/${p.id}`}>Ver</a>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>

            {modalOpen && (
                <PatientModal
                    patientId={editId}
                    onClose={closeModal}
                    onSaved={() => { closeModal(); load(); }}
                />
            )}
        </div>
    );
}
