import { apiFetch } from './http'

export interface DashboardMetrics {
  totalPatients: number
  appointmentsToday: number
  pendingAppointments: number
  activeDoctors: number
}

export function fetchDashboardMetrics(): Promise<DashboardMetrics> {
  return apiFetch<DashboardMetrics>('/api/dashboard/metrics')
}
