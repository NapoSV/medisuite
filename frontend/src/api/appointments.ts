import { apiFetch } from './http'

export type Cita = {
  id: number
  scheduledAt: string
  patient: { firstName: string; lastName: string }
  doctor: { user: { firstName: string; lastName: string } }
  status: string
  reservationCode: string
}

export function listUpcoming() {
  return apiFetch<Cita[]>('/api/appointments?upcoming=true')
}

export function cancelAppointment(id: number) {
  return apiFetch<void>(`/api/appointments/${id}/cancel`, { method: 'POST' })
}

export type Doctor = {
  id: number
  user: { firstName: string; lastName: string }
  specialty?: { name: string }
}

export type Paciente = {
  id: number
  firstName: string
  lastName: string
  dui: string
}

export type NuevaCita = {
  patientId: number
  doctorId: number
  scheduledAt: string
  reason: string
}

export function listDoctors() {
  return apiFetch<Doctor[]>('/api/doctors')
}

export function listPatients() {
  return apiFetch<Paciente[]>('/api/patients?size=100')
}

export function listSlots(doctorId: number, date: string) {
  return apiFetch<string[]>(`/api/appointments/doctors/${doctorId}/slots?date=${date}`)
}

export function createAppointment(data: NuevaCita) {
  return apiFetch<Cita>('/api/appointments', {
    method: 'POST',
    body: JSON.stringify(data),
  })
}