import { ageOn } from '../lib/dates'
import { useEffect, useState } from 'react';
import { api } from '../api/client';
import PatientModal from '../components/patients/PatientModal';
import { useAuth } from '../auth/useAuth';
import ErrorAlert from '../components/ErrorAlert';

type Patient = {
    id: number;
    firstName: string;
    lastName: string;
    dui: string;
    phone: string;
    birthDate: string;
    address: string;
};

const calcAge = ageOn

export default function Pacientes() {
    const { user } = useAuth();
    const esDoctor = user?.role === 'DOCTOR';
    const puedeTriar = user?.role === 'DOCTOR' || user?.role === 'NURSE';
    const [rows, setRows] = useState<Patient[]>([]);
    const [q, setQ] = useState('');
    const [modalOpen, setModalOpen] = useState(false);
    const [editId, setEditId] = useState<number | undefined>(undefined);
    const [error, setError] = useState<string | null>(null);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);

    const load = (nextPage = 0) => api.get<{ content: Patient[]; totalPages: number }>(
        '/api/patients', { params: { search: q, page: nextPage, size: 20, sort: 'id,asc' } })
        .then(r => { setRows(r.data.content); setPage(nextPage); setTotalPages(r.data.totalPages); setError(null); })
        .catch(() => setError('No se pudieron cargar los pacientes. Intenta buscar nuevamente.'));
        useEffect(() => {
        let activo = true;

        api.get<{ content: Patient[]; totalPages: number }>('/api/patients?sort=id,asc')
            .then(r => {
                if (activo) { setRows(r.data.content); setTotalPages(r.data.totalPages); }
            }).catch(() => { if (activo) setError('No se pudieron cargar los pacientes. Intenta buscar nuevamente.'); });

        return () => {
            activo = false;
        };
    }, []);

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
                <button onClick={() => load()} className="bg-slate-800 text-white px-4 rounded">Buscar</button>
            </div>
            {error && <ErrorAlert message={error} />}
            <table className="w-full bg-white rounded shadow">
                <thead>
                <tr className="border-b">
                    <th className="text-left p-3">Nombre</th>
                    <th className="text-left p-3">DUI</th>
                    <th className="text-left p-3">Edad</th>
                    <th className="text-left p-3">Teléfono</th>
                    <th className="text-left p-3">Dirección</th>
                    <th></th>
                </tr>
                </thead>
                <tbody>
                {rows.map(p => (
                    <tr key={p.id} className="border-b hover:bg-slate-50">
                        <td className="p-3">{p.firstName} {p.lastName}</td>
                        <td className="p-3 font-mono">{p.dui}</td>
                        <td className="p-3">{p.birthDate ? `${calcAge(p.birthDate)} años` : '-'}</td>
                        <td className="p-3">{p.phone || '-'}</td>
                        <td className="p-3 max-w-xs truncate">{p.address || '-'}</td>
                        <td className="p-3 text-right flex gap-3 justify-end">
                            <button onClick={() => openEdit(p.id)} className="text-blue-600 hover:underline text-sm">Editar</button>
                            <a className="text-slate-600 hover:underline text-sm" href={`/pacientes/${p.id}/expediente`}>Ver</a>
                            {puedeTriar && (
                                <a className="text-purple-700 hover:underline text-sm" href={`/triaje?patientId=${p.id}`}>Triar</a>
                            )}
                            {esDoctor && (
                                <a className="text-emerald-700 hover:underline text-sm" href={`/pacientes/${p.id}/expediente`}>+ Receta</a>
                            )}
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
            {totalPages > 1 && <div className="flex gap-3 items-center mt-3">
                <button disabled={page === 0} onClick={() => load(page - 1)} className="disabled:opacity-50">Anterior</button>
                <span>Página {page + 1} de {totalPages}</span>
                <button disabled={page + 1 >= totalPages} onClick={() => load(page + 1)} className="disabled:opacity-50">Siguiente</button>
            </div>}

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
