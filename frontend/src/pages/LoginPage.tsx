import { useState, type FormEvent } from 'react'
import { useAuthStore } from '../store/authStore'

export default function LoginPage() {
  const [form, setForm] = useState({ tenantSlug: '', email: '', password: '' })
  const [error, setError] = useState('')
  const [showPass, setShowPass] = useState(false)
  const { login, loading, user } = useAuthStore()

  const handle = (e: FormEvent) => {
    e.preventDefault()
    setError('')
    login(form.tenantSlug.trim(), form.email.trim(), form.password).catch((err: Error) =>
      setError(err.message)
    )
  }

  if (user) {
    return (
      <div className="ms-success">
        <div className="ms-success-card">
          <div className="ms-check">✓</div>
          <h2>Bienvenido, {user.fullName}</h2>
          <p className="ms-role">{user.role}</p>
          <p className="ms-hint">Dashboard en construcción — Avance 2</p>
        </div>
      </div>
    )
  }

  return (
    <div className="ms-root">
      {/* Background grid */}
      <div className="ms-grid" aria-hidden="true" />

      {/* Glow orb */}
      <div className="ms-orb" aria-hidden="true" />

      <main className="ms-center">
        {/* ECG decorative line */}
        <div className="ms-ecg-wrap" aria-hidden="true">
          <svg viewBox="0 0 320 32" fill="none" xmlns="http://www.w3.org/2000/svg" className="ms-ecg">
            <path
              d="M0 16 H60 L70 16 L78 4 L86 28 L94 10 L100 16 H140 L150 16 L158 4 L166 28 L174 10 L180 16 H320"
              stroke="#0ecfc3"
              strokeWidth="1.5"
              strokeLinecap="round"
              strokeLinejoin="round"
            />
          </svg>
        </div>

        {/* Card */}
        <div className="ms-card">
          {/* Brand */}
          <div className="ms-brand">
            <span className="ms-brand-dot" aria-hidden="true" />
            <span className="ms-brand-name">Medi<span>Suite</span></span>
          </div>
          <p className="ms-tagline">Plataforma médica multi-clínica</p>

          {/* Form */}
          <form onSubmit={handle} noValidate className="ms-form">
            <div className="ms-field">
              <label htmlFor="tenantSlug" className="ms-label">Código de clínica</label>
              <input
                id="tenantSlug"
                type="text"
                autoComplete="organization"
                placeholder="ej. clinica-san-rafael"
                value={form.tenantSlug}
                onChange={e => setForm(f => ({ ...f, tenantSlug: e.target.value }))}
                className="ms-input"
                required
              />
            </div>

            <div className="ms-field">
              <label htmlFor="email" className="ms-label">Correo electrónico</label>
              <input
                id="email"
                type="email"
                autoComplete="email"
                placeholder="usuario@clinica.com"
                value={form.email}
                onChange={e => setForm(f => ({ ...f, email: e.target.value }))}
                className="ms-input"
                required
              />
            </div>

            <div className="ms-field">
              <label htmlFor="password" className="ms-label">Contraseña</label>
              <div className="ms-pass-wrap">
                <input
                  id="password"
                  type={showPass ? 'text' : 'password'}
                  autoComplete="current-password"
                  placeholder="••••••••"
                  value={form.password}
                  onChange={e => setForm(f => ({ ...f, password: e.target.value }))}
                  className="ms-input"
                  required
                />
                <button
                  type="button"
                  onClick={() => setShowPass(v => !v)}
                  className="ms-toggle-pass"
                  aria-label={showPass ? 'Ocultar contraseña' : 'Mostrar contraseña'}
                >
                  {showPass ? '●' : '○'}
                </button>
              </div>
            </div>

            {error && (
              <div className="ms-error" role="alert">
                <span className="ms-error-icon">!</span>
                {error}
              </div>
            )}

            <button type="submit" disabled={loading} className="ms-btn">
              {loading ? (
                <span className="ms-spinner" aria-label="Cargando" />
              ) : (
                'Iniciar sesión'
              )}
            </button>
          </form>

          <p className="ms-footer">
            MediSuite &copy; {new Date().getFullYear()} &mdash; Acceso autorizado
          </p>
        </div>
      </main>
    </div>
  )
}
