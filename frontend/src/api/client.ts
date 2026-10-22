import axios from 'axios';

export const api = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8097'
});

api.interceptors.request.use(cfg => {
    const t = localStorage.getItem('token');
    if (t) cfg.headers.Authorization = `Bearer ${t}`;
    return cfg;
});

// Mismo contrato que apiFetch (http.ts): 401 siempre expira sesion;
// 403 solo expira cuando no hay token (sesion no valida vs rol insuficiente).
api.interceptors.response.use(
    r => r,
    err => {
        const status = err.response?.status;
        const hadToken = !!localStorage.getItem('token');
        const onLoginPage = window.location.pathname.startsWith('/login');
        if (!onLoginPage && (status === 401 || (status === 403 && !hadToken))) {
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            const reason = status === 401 ? 'expired' : 'forbidden';
            const next = encodeURIComponent(window.location.pathname + window.location.search);
            window.location.href = `/login?reason=${reason}&next=${next}`;
        }
        return Promise.reject(err);
    }
);