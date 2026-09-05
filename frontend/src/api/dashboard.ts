import { apiFetch } from './http'

export interface DashboardMetrics {
  appointmentsToday: number
  activePatients: number
  alerts: number
  prescriptionsIssued: number
}

export function fetchDashboardMetrics(): Promise<DashboardMetrics> {
  return apiFetch<DashboardMetrics>('/api/dashboard/metrics')
}
