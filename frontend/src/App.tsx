import LoginPage from './pages/LoginPage'
import DashboardPage from './pages/DashboardPage'
import { useAuthStore } from './store/authStore'

export default function App() {
  const user = useAuthStore((s) => s.user)
  return user ? <DashboardPage /> : <LoginPage />
}
