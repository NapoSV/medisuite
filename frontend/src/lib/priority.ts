export const PRIORITIES = [
    { value: 'NORMAL', label: 'Normal', badge: 'bg-green-100 text-green-800' },
    { value: 'URGENTE', label: 'Urgente', badge: 'bg-yellow-100 text-yellow-800' },
    { value: 'EMERGENCIA', label: 'Emergencia', badge: 'bg-red-100 text-red-800' },
] as const

export type Priority = (typeof PRIORITIES)[number]['value']
export const PRIORITY_DEFAULT: Priority = 'NORMAL'

export const priorityLabel = (p: string) =>
    PRIORITIES.find(x => x.value === p.toUpperCase())?.label ?? p

export const priorityBadge = (p: string) =>
    PRIORITIES.find(x => x.value === p.toUpperCase())?.badge ?? 'bg-gray-100 text-gray-800'