import { useForm } from 'react-hook-form';
import { z } from 'zod';
import { zodResolver } from '@hookform/resolvers/zod';
import { api } from '../../api/client';

const schema = z.object({
    firstName:  z.string().min(2, 'Nombre requerido'),
    lastName:   z.string().min(2, 'Apellido requerido'),
    dui:        z.string().regex(/^\d{8}-\d$/, 'DUI: formato 00000000-0'),
    phone:      z.string().optional(),
    address:    z.string().optional(),
});
type PatientForm = z.infer<typeof schema>;

interface Props { patientId?: number; onClose: () => void; onSaved: () => void; }

export default function PatientModal({ patientId, onClose, onSaved }: Props) {
    const { register, handleSubmit, formState: { errors } } = useForm<PatientForm>({
        resolver: zodResolver(schema),
    });

    const submit = async (data: PatientForm) => {
        if (patientId) await api.put(`/patients/${patientId}`, data);
        else           await api.post('/patients', data);
        onSaved();
        onClose();
    };

    return (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
            <form onSubmit={handleSubmit(submit)} className="bg-white rounded-lg p-6 w-full max-w-md space-y-3">
                <h3 className="text-lg font-bold">{patientId ? 'Editar' : 'Nuevo'} paciente</h3>
                <input {...register('firstName')} placeholder="Nombre" className="border rounded p-2 w-full"/>
                {errors.firstName && <p className="text-red-500 text-sm">{errors.firstName.message}</p>}
                <input {...register('lastName')} placeholder="Apellido" className="border rounded p-2 w-full"/>
                {errors.lastName && <p className="text-red-500 text-sm">{errors.lastName.message}</p>}
                <input {...register('dui')} placeholder="DUI (00000000-0)" className="border rounded p-2 w-full"/>
                {errors.dui && <p className="text-red-500 text-sm">{errors.dui.message}</p>}
                <input {...register('phone')} placeholder="Teléfono (opcional)" className="border rounded p-2 w-full"/>
                <input {...register('address')} placeholder="Dirección (opcional)" className="border rounded p-2 w-full"/>
                <div className="flex gap-2 justify-end">
                    <button type="button" onClick={onClose} className="border rounded px-4 py-2">Cancelar</button>
                    <button className="bg-blue-600 text-white rounded px-4 py-2">Guardar</button>
                </div>
            </form>
        </div>
    );
}