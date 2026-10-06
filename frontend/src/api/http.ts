import { useAuthStore } from '../store/authStore'

export const API_BASE = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8097'

export class ApiError extends Error {
  constructor(public status: number, message: string) {
    super(message)
    this.name = 'ApiError'
  }
}

// Redirige al login cuando el token expira o el backend rechaza por auth.
// Guard: solo redirige si NO estamos ya en /login (evita loops).
function expireSessionAndRedirect(motivo: 'expired' | 'forbidden') {
  try { useAuthStore.getState().logout() } catch { /* store opcional */ }
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  if (typeof window !== 'undefined' && !window.location.pathname.startsWith('/login')) {
    const next = encodeURIComponent(window.location.pathname + window.location.search)
    window.location.href = `/login?reason=${motivo}&next=${next}`
  }
}

export async function apiFetch<T>(path: string, init: RequestInit = {}): Promise<T> {
  const token = localStorage.getItem('token') ?? useAuthStore.getState().token
  const headers = new Headers(init.headers)
  headers.set('Accept', 'application/json')
  if (init.body && !headers.has('Content-Type')) {
    headers.set('Content-Type', 'application/json')
  }
  if (token) {
    headers.set('Authorization', `Bearer ${token}`)
  }

  const res = await fetch(`${API_BASE}${path}`, { ...init, headers })

  if (res.status === 401) {
    expireSessionAndRedirect('expired')
    throw new ApiError(401, 'Sesión expirada. Vuelva a iniciar sesión.')
  }

  // 403 sin token (o token invalido) tambien se trata como sesion caducada.
  // Si hay token valido pero rol insuficiente, mostramos el error sin cerrar sesion.
  if (res.status === 403 && !token) {
    expireSessionAndRedirect('forbidden')
    throw new ApiError(403, 'Sesión no válida. Vuelva a iniciar sesión.')
  }

  if (!res.ok) {
    const body = await res.json().catch(() => ({}))
    throw new ApiError(res.status, body.message ?? `Error ${res.status}`)
  }

  if (res.status === 204) return undefined as T
  return res.json() as Promise<T>
}
