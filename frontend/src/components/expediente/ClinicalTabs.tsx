import { useRef, type KeyboardEvent } from 'react'

import { tabId, panelId, type TabId } from './tabIds'

const TABS: { id: TabId; label: string }[] = [
    { id: 'resumen', label: 'Resumen' },
    { id: 'historial', label: 'Historial' },
    { id: 'signos', label: 'Signos' },
    { id: 'recetas', label: 'Recetas' },
    { id: 'documentos', label: 'Documentos' },
]


type Props = { value: TabId; onChange: (id: TabId) => void }

export default function ClinicalTabs({ value, onChange }: Props) {
    const refs = useRef<Partial<Record<TabId, HTMLButtonElement | null>>>({})

    const onKeyDown = (e: KeyboardEvent<HTMLButtonElement>, index: number) => {
        let next: number
        if (e.key === 'ArrowRight') next = (index + 1) % TABS.length
        else if (e.key === 'ArrowLeft') next = (index - 1 + TABS.length) % TABS.length
        else if (e.key === 'Home') next = 0
        else if (e.key === 'End') next = TABS.length - 1
        else return
        e.preventDefault()
        const target = TABS[next].id
        onChange(target)
        refs.current[target]?.focus()
    }

    return (
        <div role="tablist" aria-label="Secciones del expediente"
             className="flex overflow-x-auto border-b border-slate-200">
            {TABS.map((t, i) => {
                const selected = t.id === value
                return (
                    <button
                        key={t.id}
                        ref={el => { refs.current[t.id] = el }}
                        type="button"
                        role="tab"
                        id={tabId(t.id)}
                        aria-selected={selected}
                        aria-controls={panelId(t.id)}
                        tabIndex={selected ? 0 : -1}
                        onClick={() => onChange(t.id)}
                        onKeyDown={e => onKeyDown(e, i)}
                        className={`whitespace-nowrap px-4 py-2 text-sm -mb-px border-b-2 focus:outline-none
              focus-visible:ring-2 focus-visible:ring-blue-400
              ${selected
                            ? 'border-blue-600 text-blue-700 font-medium'
                            : 'border-transparent text-slate-600 hover:text-slate-900'}`}
                    >
                        {t.label}
                    </button>
                )
            })}
        </div>
    )
}