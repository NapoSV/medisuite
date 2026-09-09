import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../auth/useAuth';

export default function Login() {
  const nav = useNavigate();
  const { login } = useAuth();
  const [tenantSlug, setSlug] = useState('demo');
  const [email, setEmail] = useState('');
  const [password, setPwd] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true); setError('');
    try {
      await login(tenantSlug, email, password);
      nav('/dashboard');
    } catch (err: any) {
      setError(err.response?.data?.error ?? 'Credenciales invalidas');
    } finally { setLoading(false); }
  };

  return (
      <div className="min-h-screen flex items-center justify-center bg-slate-100">
        <form onSubmit={submit} className="bg-white p-8 rounded-lg shadow w-full max-w-sm space-y-4">
          <h1 className="text-2xl font-bold text-center">MediSuite</h1>
          <p className="text-center text-slate-600 text-sm">Inicia sesion</p>

          <label className="block">
            <span className="text-sm">Clinica</span>
            <input value={tenantSlug} onChange={e => setSlug(e.target.value)}
                   className="mt-1 w-full border rounded px-3 py-2" required />
          </label>

          <label className="block">
            <span className="text-sm">Correo</span>
            <input type="email" value={email} onChange={e => setEmail(e.target.value)}
                   className="mt-1 w-full border rounded px-3 py-2" required autoFocus />
          </label>

          <label className="block">
            <span className="text-sm">Contrasena</span>
            <input type="password" value={password} onChange={e => setPwd(e.target.value)}
                   className="mt-1 w-full border rounded px-3 py-2" required />
          </label>

          {error && <p className="text-red-600 text-sm">{error}</p>}

          <button disabled={loading}
                  className="w-full bg-blue-600 text-white py-2 rounded disabled:opacity-50">
            {loading ? 'Ingresando...' : 'Entrar'}
          </button>
        </form>
      </div>
  );
}