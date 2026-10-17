import { createContext } from 'react';

export type User = {
  id: number;
  fullName: string;
  role: string;
  tenantId: number;
};

type AuthCtx = {
  user: User | null;
  loading: boolean;
  login: (tenantSlug: string, email: string, password: string) => Promise<void>;
  logout: () => void;
};

export const AuthContext = createContext<AuthCtx | null>(null);