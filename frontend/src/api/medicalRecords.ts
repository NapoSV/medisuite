import { apiFetch } from './http'

export type Receta = {
  id: number
  emittedAt: string
  doctor: { user: { firstName: string; lastName: string } }
  items: { medication: string; dosage: string }[]
}

export type SignosVitales = {
  id: number
  recordedAt: string
  temperature: number
  heartRate: number
  bloodPressure?: string
}

export type Expediente = {
  id: number
  patient: { firstName: string; lastName: string; dui: string; birthDate?: string }
  bloodType?: string
  allergies?: string
  prescriptions: Receta[]
  vitalSigns: SignosVitales[]
}

export function getMedicalRecord(patientId: number) {
  return apiFetch<Expediente>(`/api/patients/${patientId}/medical-record`)
}

export type NuevaReceta = {
  medicalRecordId: number
  diagnosis: string
  notes?: string
  items: { medication: string; dosage: string; frequency: string; durationDays: number }[]
}

export function createPrescription(data: NuevaReceta) {
  return apiFetch<Receta>('/api/prescriptions', {
    method: 'POST',
    body: JSON.stringify(data),
  })
}