export interface KpiCardProps {
  label: string
  value: number | null
  icon: React.ReactNode
  accent: string
  loading: boolean
}

export default function KpiCard({ label, value, icon, accent, loading }: KpiCardProps) {
  return (
    <div className="bg-white rounded-2xl shadow-sm border border-slate-200 p-6 flex items-start gap-4">
      <div className={`w-11 h-11 rounded-xl flex items-center justify-center flex-shrink-0 ${accent}`}>
        {icon}
      </div>
      <div className="flex-1 min-w-0">
        <p className="text-xs font-medium uppercase tracking-wide text-slate-500">{label}</p>
        {loading ? (
          <div className="h-8 w-16 mt-1 rounded-md bg-slate-200 animate-pulse" />
        ) : (
          <p className="text-3xl font-semibold text-slate-800 mt-1 tabular-nums">
            {value !== null ? value.toLocaleString('es-SV') : '-'}
          </p>
        )}
      </div>
    </div>
  )
}