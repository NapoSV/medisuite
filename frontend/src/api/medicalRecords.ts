import { apiFetch } from './http'

export type Receta = {
  id: number
  issuedOn: string
  doctor: { user: { firstName: string; lastName: string } }
  items: { medication: string; dosage: string }[]
}

export type SignosVitales = {
  id: number
  recordedAt: string
  temperatureC?: number
  heartRate?: number
  bloodPressure?: string
  weightKg?: number
  heightCm?: number
  symptoms?: string
  priority?: string
}

export type ExpedientePatient = {
  id: number
  firstName: string
  lastName: string
  dui: string
  birthDate?: string
  bloodType?: string
  allergies?: string
}

export type Expediente = {
  id: number
  patient: ExpedientePatient
  generalNotes?: string
  prescriptions: Receta[]
  vitalSigns: SignosVitales[]
}

export function getMedicalRecord(patientId: number) {
  return apiFetch<Expediente>(`/api/patients/${patientId}/medical-record`)
}

export type NuevoTriaje = {
  temperatureC?: number
  heartRate?: number
  bloodPressure?: string
  weightKg?: number
  heightCm?: number
  symptoms?: string
  priority: string
}

export function createVitalSign(patientId: number, data: NuevoTriaje) {
  return apiFetch<SignosVitales>(`/api/patients/${patientId}/vital-signs`, {
    method: 'POST',
    body: JSON.stringify(data),
  })
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
