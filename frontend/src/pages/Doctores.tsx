import { useEffect, useState } from 'react';
import { api } from '../api/client';
import { useAuth } from '../auth/useAuth';
import LoadingSpinner from '../components/LoadingSpinner';
import ErrorAlert from '../components/ErrorAlert';

type Specialty = { id: number; name: string };
type DoctorUser = { firstName: string; lastName: string; email: string };
type Doctor = { id: number; user: DoctorUser; specialty: Specialty; licenseNumber: string };

type CreateForm = {
    firstName: string; lastName: string; email: string; cif: string;
    licenseNumber: string; specialtyId: string;
};

const EMPTY_FORM: CreateForm = { firstName: '', lastName: '', email: '', cif: '', licenseNumber: '', specialtyId: '' };

export default function Doctores() {
    const { user: authUser } = useAuth();
    const isAdmin = authUser?.role === 'ADMIN';

    const [doctors, setDoctors] = useState<Doctor[]>([]);
    const [specialties, setSpecialties] = useState<Specialty[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    const [modalOpen, setModalOpen] = useState(false);
    const [form, setForm] = useState<CreateForm>(EMPTY_FORM);
    const [saving, setSaving] = useState(false);
    const [formError, setFormError] = useState<string | null>(null);

    const load = async () => {
        try {
            setLoading(true);
            const [docRes, spRes] = await Promise.all([
                api.get<Doctor[]>('/doctors'),
                api.get<Specialty[]>('/specialties'),
            ]);
            setDoctors(docRes.data);
            setSpecialties(spRes.data);
        } catch {
            setError('No se pudo cargar la lista de doctores.');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => { load(); }, []);

    const openModal = () => { setForm(EMPTY_FORM); setFormError(null); setModalOpen(true); };
    const closeModal = () => setModalOpen(false);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) =>
        setForm(prev => ({ ...prev, [e.target.name]: e.target.value }));

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!form.specialtyId) { setFormError('Selecciona una especialidad.'); return; }
        setSaving(true);
        setFormError(null);
        try {
            await api.post('/doctors', {
                firstName: form.firstName,
                lastName: form.lastName,
                email: form.email,
                cif: form.cif,
                licenseNumber: form.licenseNumber,
                specialtyId: Number(form.specialtyId),
            });
            closeModal();
            load();
        } catch (err: unknown) {
            const msg = (err as { response?: { data?: { message?: string } } })
                ?.response?.data?.message ?? 'Error al guardar el doctor.';
            setFormError(msg);
        } finally {
            setSaving(false);
        }
    };

    if (loading) return <LoadingSpinner />;
    if (error) return <ErrorAlert message={error} />;

    return (
        <div>
            <div className="flex justify-between mb-4">
                <h2 className="text-2xl font-bold">Doctores</h2>
                {isAdmin && (
                    <button onClick={openModal} className="bg-blue-600 text-white px-4 py-2 rounded">
                        + Nuevo doctor
                    </button>
                )}
            </div>

            {doctors.length === 0 ? (
                <p className="text-slate-500">No hay doctores registrados.</p>
            ) : (
                <table className="w-full bg-white rounded shadow">
                    <thead>
                        <tr className="border-b">
                            <th className="text-left p-3">Nombre</th>
                            <th className="text-left p-3">Especialidad</th>
                            <th className="text-left p-3">Matrícula</th>
                            <th className="text-left p-3">Email</th>
                        </tr>
                    </thead>
                    <tbody>
                        {doctors.map(d => (
                            <tr key={d.id} className="border-b hover:bg-slate-50">
                                <td className="p-3">{d.user.firstName} {d.user.lastName}</td>
                                <td className="p-3">{d.specialty.name}</td>
                                <td className="p-3 font-mono text-sm">{d.licenseNumber}</td>
                                <td className="p-3 text-slate-500 text-sm">{d.user.email}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            )}

            {modalOpen && (
                <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
                    <form onSubmit={handleSubmit} className="bg-white rounded-lg p-6 w-full max-w-md space-y-3">
                        <h3 className="text-lg font-bold">Nuevo doctor</h3>

                        {formError && <p className="text-red-600 text-sm">{formError}</p>}

                        <div className="flex gap-2">
                            <input name="firstName" value={form.firstName} onChange={handleChange}
                                placeholder="Nombre" required className="border rounded p-2 w-full" />
                            <input name="lastName" value={form.lastName} onChange={handleChange}
                                placeholder="Apellido" required className="border rounded p-2 w-full" />
                        </div>
                        <input name="email" type="email" value={form.email} onChange={handleChange}
                            placeholder="Correo electrónico" required className="border rounded p-2 w-full" />
                        <input name="cif" value={form.cif} onChange={handleChange}
                            placeholder="CIF / No. Registro Nacional" required className="border rounded p-2 w-full" />
                        <input name="licenseNumber" value={form.licenseNumber} onChange={handleChange}
                            placeholder="Matrícula médica (ej. MED-2024-0001)" required className="border rounded p-2 w-full" />
                        <select name="specialtyId" value={form.specialtyId} onChange={handleChange}
                            required className="border rounded p-2 w-full">
                            <option value="">— Especialidad —</option>
                            {specialties.map(s => (
                                <option key={s.id} value={s.id}>{s.name}</option>
                            ))}
                        </select>

                        <p className="text-xs text-slate-400">
                            Contraseña inicial: <span className="font-mono">Demo2026!</span> (debe cambiarla al primer ingreso)
                        </p>

                        <div className="flex gap-2 justify-end pt-2">
                            <button type="button" onClick={closeModal} className="border rounded px-4 py-2">Cancelar</button>
                            <button disabled={saving} className="bg-blue-600 text-white rounded px-4 py-2 disabled:opacity-50">
                                {saving ? 'Guardando…' : 'Guardar'}
                            </button>
                        </div>
                    </form>
                </div>
            )}
        </div>
    );
}
