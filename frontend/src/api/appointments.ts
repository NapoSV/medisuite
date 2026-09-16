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
