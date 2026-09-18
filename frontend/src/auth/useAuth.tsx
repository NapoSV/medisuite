import { createContext, useContext, useEffect, useState } from 'react';
import { api } from '../api/client';

type User = { id: number; fullName: string; role: string; tenantId: number };
type AuthCtx = {
  user: User | null;
  loading: boolean;
  login: (t: string, u: string, p: string) => Promise<void>;
  logout: () => void;
};

const Ctx = createContext<AuthCtx>({} as AuthCtx);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem('token');
    const raw = localStorage.getItem('user');
    if (token && raw) setUser(JSON.parse(raw));
    setLoading(false);
  }, []);

  const login = async (tenantSlug: string, email: string, password: string) => {
    const { data } = await api.post('/api/auth/login', { tenantSlug, email, password });
    localStorage.setItem('token', data.accessToken);
    localStorage.setItem('user', JSON.stringify(data.user));
    setUser(data.user);
  };

  const logout = () => {
    api.post('/auth/logout').catch(() => {});
    localStorage.clear();
    setUser(null);
  };

  return <Ctx.Provider value={{ user, loading, login, logout }}>{children}</Ctx.Provider>;
}

export const useAuth = () => useContext(Ctx);
