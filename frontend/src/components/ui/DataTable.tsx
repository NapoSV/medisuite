type Col<T> = { key: keyof T; label: string; render?: (row: T) => React.ReactNode };

export default function DataTable<T extends { id: number }>({ cols, rows }:
        { cols: Col<T>[]; rows: T[] }) {
  return (
    <table className="w-full bg-white rounded shadow">
      <thead>
        <tr className="border-b">
          {cols.map(c => <th key={String(c.key)} className="text-left p-3">{c.label}</th>)}
        </tr>
      </thead>
      <tbody>
        {rows.map(r => (
          <tr key={r.id} className="border-b hover:bg-slate-50">
            {cols.map(c => (
              <td key={String(c.key)} className="p-3">
                {c.render ? c.render(r) : String(r[c.key] ?? '')}
              </td>
            ))}
          </tr>
        ))}
      </tbody>
    </table>
  );
}