import { create } from 'zustand'
import { loginRequest, type LoginResult } from '../api/auth'

interface AuthState {
  token: string | null
  user: LoginResult['user'] | null
  loading: boolean
  login: (tenantSlug: string, email: string, password: string) => Promise<void>
  logout: () => void
}

export const useAuthStore = create<AuthState>((set) => ({
  token: null,
  user: null,
  loading: false,

  login: async (tenantSlug, email, password) => {
    set({ loading: true })
    try {
      const data = await loginRequest({ tenantSlug, email, password })
      set({ token: data.accessToken, user: data.user, loading: false })
    } catch (err) {
      set({ loading: false })
      throw err
    }
  },

  logout: () => set({ token: null, user: null }),
}))
