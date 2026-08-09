const API_BASE = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8097'

export interface LoginPayload {
  tenantSlug: string
  email: string
  password: string
}

export interface LoginResult {
  accessToken: string
  tokenType: string
  expiresIn: number
  user: {
    id: number
    fullName: string
    role: string
    tenantId: number
  }
}

export async function loginRequest(payload: LoginPayload): Promise<LoginResult> {
  const res = await fetch(`${API_BASE}/api/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  })

  if (!res.ok) {
    const body = await res.json().catch(() => ({}))
    throw new Error(body.message ?? 'Credenciales inválidas')
  }

  return res.json()
}
