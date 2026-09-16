import { useState, type FormEvent } from 'react'
import { useNavigate } from 'react-router-dom'
import { Mail, Lock, Eye, EyeOff } from 'lucide-react'
import { useAuth } from '../auth/useAuth'
import { getTenantSlug } from '../lib/tenant'
import Logo from '../components/Logo'

export default function LoginPage() {
  const [form, setForm] = useState({ email: '', password: '' })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const [showPass, setShowPass] = useState(false)
  const { login } = useAuth()
  const navigate = useNavigate()

  const handle = async (e: FormEvent) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      await login(getTenantSlug(), form.email.trim(), form.password)
      navigate('/dashboard', { replace: true })
    } catch (err: unknown) {
      const msg = (err as any)?.response?.data?.message ?? (err as Error)?.message ?? 'Error al iniciar sesión'
      setError(msg)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen bg-background flex items-center justify-center px-4">
      <div className="w-full max-w-md">

        {/* Brand */}
        <div className="flex justify-center mb-8">
          <Logo variant="vertical" mode="light" />
        </div>

        {/* Card */}
        <div className="bg-surface rounded-[20px] shadow-card border border-border p-8">
          <h2 className="text-lg font-semibold text-text mb-6">Iniciar sesión</h2>

          <form onSubmit={handle} noValidate className="space-y-4">

            {/* Correo */}
            <div>
              <label htmlFor="email" className="block text-sm font-medium text-text mb-1.5">
                Correo electrónico
              </label>
              <div className="relative">
                <Mail className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted pointer-events-none" />
                <input
                  id="email"
                  type="email"
                  autoComplete="email"
                  placeholder="usuario@clinica.com"
                  value={form.email}
                  onChange={e => setForm(f => ({ ...f, email: e.target.value }))}
                  className="w-full pl-10 pr-4 py-2.5 bg-surface border border-border rounded-[10px] text-sm text-text placeholder:text-muted focus:outline-none focus:border-primary focus:ring-[3px] focus:ring-blue-500/10 transition-all"
                  required
                />
              </div>
            </div>

            {/* Contraseña */}
            <div>
              <label htmlFor="password" className="block text-sm font-medium text-text mb-1.5">
                Contraseña
              </label>
              <div className="relative">
                <Lock className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted pointer-events-none" />
                <input
                  id="password"
                  type={showPass ? 'text' : 'password'}
                  autoComplete="current-password"
                  placeholder="••••••••"
                  value={form.password}
                  onChange={e => setForm(f => ({ ...f, password: e.target.value }))}
                  className="w-full pl-10 pr-10 py-2.5 bg-surface border border-border rounded-[10px] text-sm text-text placeholder:text-muted focus:outline-none focus:border-primary focus:ring-[3px] focus:ring-blue-500/10 transition-all"
                  required
                />
                <button
                  type="button"
                  onClick={() => setShowPass(v => !v)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-muted hover:text-text transition-colors"
                  aria-label={showPass ? 'Ocultar contraseña' : 'Mostrar contraseña'}
                >
                  {showPass ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
                </button>
              </div>
            </div>

            {/* Error */}
            {error && (
              <div className="flex items-start gap-2.5 p-3 bg-[#FEE2E2] border border-[#FCA5A5] rounded-[10px]" role="alert">
                <div className="w-4 h-4 rounded-full bg-[#DC2626] flex items-center justify-center flex-shrink-0 mt-0.5">
                  <span className="text-white text-[10px] font-bold leading-none">!</span>
                </div>
                <p className="text-sm text-[#991B1B]">{error}</p>
              </div>
            )}

            {/* Botón */}
            <button
              type="submit"
              disabled={loading}
              className="w-full bg-primary hover:bg-primary-dark active:translate-y-px disabled:opacity-50 disabled:cursor-not-allowed text-white font-medium py-2.5 px-4 rounded-[10px] shadow-primaryBtn transition-all text-sm mt-2"
            >
              {loading ? (
                <span className="flex items-center justify-center gap-2">
                  <svg className="animate-spin w-4 h-4" viewBox="0 0 24 24" fill="none">
                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
                  </svg>
                  Iniciando sesión...
                </span>
              ) : (
                'Iniciar sesión'
              )}
            </button>
          </form>
        </div>

        <p className="text-center text-xs text-muted mt-6">
          MediSuite &copy; {new Date().getFullYear()} &mdash; Acceso autorizado
        </p>
      </div>
    </div>
  )
}
