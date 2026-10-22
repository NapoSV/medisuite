import { apiFetch } from './http'

/** Prioridad clínica vigente (escala en español, resuelta 09/10). */
export type ClinicalPriority = 'NORMAL' | 'URGENTE' | 'EMERGENCIA'

export interface WaitingRoomEntry {
  appointmentId: number
  patientName: string
  /** ISO con offset; formatear en America/El_Salvador. */
  scheduledAt: string
  status: string
}

export interface OccupancySlot {
  /** Hora local 0-23. No es porcentaje de ocupación. */
  hour: number
  appointments: number
}

export interface CriticalAlert {
  vitalSignId: number
  patientId: number
  priority: ClinicalPriority
  recordedAt: string
}

/**
 * Todos los campos son opcionales por diseño: el backend omite del JSON
 * los que el rol no tiene autorizados.
 *   undefined -> no autorizado (no renderizar)
 *   []        -> autorizado sin datos (estado vacío)
 */
export interface DashboardResponse {
  appointmentsToday?: number
  activePatients?: number
  prescriptionsThisWeek?: number
  criticalAlerts?: number
  waitingRoom?: WaitingRoomEntry[]
  occupancyByHour?: OccupancySlot[]
  clinicalAlerts?: CriticalAlert[]

  /** @deprecated alias de compatibilidad — usar prescriptionsThisWeek */
  prescriptionsIssued?: number
  /** @deprecated alias de compatibilidad — usar criticalAlerts */
  alerts?: number
}

export function fetchDashboardMetrics(): Promise<DashboardResponse> {
  return apiFetch<DashboardResponse>('/api/dashboard/metrics')
}