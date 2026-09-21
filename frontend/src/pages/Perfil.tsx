import { useEffect, useState } from 'react';
import type { FormEvent } from 'react';
import { api } from '../api/client';
import { useAuth } from '../auth/useAuth';
import LoadingSpinner from '../components/LoadingSpinner';
import ErrorAlert from '../components/ErrorAlert';

type ProfileUser = {
  id: number;
  fullName: string;
  email: string;
  role: string;
  tenantId: number;
};

export default function Perfil() {
  const { user: authUser } = useAuth();

  // --- Datos del perfil (GET /api/users/me) ---
  const [profile, setProfile] = useState<ProfileUser | null>(null);
  const [loadingProfile, setLoadingProfile] = useState(true);
  const [profileError, setProfileError] = useState<string | null>(null);

  const loadProfile = () => {
    setLoadingProfile(true);
    setProfileError(null);
    api
      .get<ProfileUser>('/api/users/me')
      .then((res) => setProfile(res.data))
      .catch((err) =>
        setProfileError(
          err.response?.data?.error ?? 'No se pudo cargar el perfil'
        )
      )
      .finally(() => setLoadingProfile(false));
  };

  useEffect(() => {
    loadProfile();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // --- Formulario de cambio de contraseña ---
  const [currentPassword, setCurrentPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [formError, setFormError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setFormError(null);
    setSuccessMessage(null);

    if (newPassword.length < 8) {
      setFormError('La nueva contraseña debe tener al menos 8 caracteres.');
      return;
    }
    if (newPassword !== confirmPassword) {
      setFormError('La nueva contraseña y su confirmación no coinciden.');
      return;
    }

    setSubmitting(true);
    try {
      await api.post('/api/auth/change-password', {
        currentPassword,
        newPassword,
      });
      setSuccessMessage('Contraseña actualizada correctamente.');
      setCurrentPassword('');
      setNewPassword('');
      setConfirmPassword('');
    } catch (err: any) {
      setFormError(
        err.response?.data?.error ?? 'No se pudo actualizar la contraseña.'
      );
    } finally {
      setSubmitting(false);
    }
  };

  // Mientras carga /me, usamos lo que ya tengamos en el contexto de auth como respaldo
  const displayUser = profile ?? authUser;

  return (
    <div className="min-h-screen bg-background">
      <main className="max-w-2xl mx-auto px-6 py-10">
        <h1 className="text-2xl font-semibold text-text mb-6">Perfil</h1>

        {/* Tarjeta de datos del usuario */}
        <section className="bg-surface border border-border rounded-lg p-6 mb-6 shadow-card">
          {loadingProfile ? (
            <LoadingSpinner label="Cargando perfil..." />
          ) : profileError ? (
            <ErrorAlert
              message="No se pudo cargar la información del perfil"
              detail={profileError}
              onRetry={loadProfile}
            />
          ) : (
            <div className="flex items-center gap-4">
              <div className="h-14 w-14 rounded-full bg-primary-light flex items-center justify-center text-primary font-semibold text-xl flex-shrink-0">
                {displayUser?.fullName?.charAt(0).toUpperCase() ?? '?'}
              </div>
              <div className="min-w-0">
                <p className="text-lg font-semibold text-text truncate">
                  {displayUser?.fullName}
                </p>
                <p className="text-sm text-muted uppercase tracking-wide">
                  {displayUser?.role}
                </p>
                {profile?.email && (
                  <p className="text-sm text-muted mt-0.5 truncate">
                    {profile.email}
                  </p>
                )}
              </div>
            </div>
          )}
        </section>

        {/* Formulario de cambio de contraseña */}
        <section className="bg-surface border border-border rounded-lg p-6 shadow-card">
          <h2 className="text-lg font-semibold text-text mb-1">
            Cambiar contraseña
          </h2>
          <p className="text-sm text-muted mb-4">
            Usa una contraseña segura que no compartas con nadie más.
          </p>

          <form onSubmit={handleSubmit} className="flex flex-col gap-4">
            <div className="flex flex-col gap-1">
              <label htmlFor="currentPassword" className="text-sm font-medium text-text">
                Contraseña actual
              </label>
              <input
                id="currentPassword"
                type="password"
                required
                autoComplete="current-password"
                value={currentPassword}
                onChange={(e) => setCurrentPassword(e.target.value)}
                className="border border-border rounded-md p-2 text-text focus:outline-none focus:ring-2 focus:ring-primary"
              />
            </div>

            <div className="flex flex-col gap-1">
              <label htmlFor="newPassword" className="text-sm font-medium text-text">
                Nueva contraseña
              </label>
              <input
                id="newPassword"
                type="password"
                required
                minLength={8}
                autoComplete="new-password"
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
                className="border border-border rounded-md p-2 text-text focus:outline-none focus:ring-2 focus:ring-primary"
              />
            </div>

            <div className="flex flex-col gap-1">
              <label htmlFor="confirmPassword" className="text-sm font-medium text-text">
                Confirmar nueva contraseña
              </label>
              <input
                id="confirmPassword"
                type="password"
                required
                minLength={8}
                autoComplete="new-password"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                className="border border-border rounded-md p-2 text-text focus:outline-none focus:ring-2 focus:ring-primary"
              />
            </div>

            {formError && <ErrorAlert message={formError} />}
            {successMessage && (
              <p className="text-sm font-medium text-[#16A34A]">{successMessage}</p>
            )}

            <button
              type="submit"
              disabled={submitting}
              className="bg-primary text-white py-2 rounded-md font-medium hover:bg-primary-dark disabled:opacity-60 disabled:cursor-not-allowed transition-colors"
            >
              {submitting ? 'Guardando...' : 'Cambiar contraseña'}
            </button>
          </form>
        </section>
      </main>
    </div>
  );
}

