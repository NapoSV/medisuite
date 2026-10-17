import { useState, type ReactNode } from 'react';
import { api } from '../api/client';
import { AuthContext, type User } from './authContext';

function readStoredUser(): User | null {
  try {
    const token = localStorage.getItem('token');
    const raw = localStorage.getItem('user');
    return token && raw ? JSON.parse(raw) : null;
  } catch {
    return null;
  }
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(readStoredUser);

  const login = async (
    tenantSlug: string,
    email: string,
    password: string,
  ) => {
    const { data } = await api.post('/api/auth/login', {
      tenantSlug,
      email,
      password,
    });
    localStorage.setItem('token', data.accessToken);
    localStorage.setItem('user', JSON.stringify(data.user));
    setUser(data.user);
  };

  const logout = () => {
    api.post('/api/auth/logout').catch(() => {});
    localStorage.clear();
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, loading: false, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}