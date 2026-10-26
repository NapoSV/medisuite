const CLINIC_ZONE = 'America/El_Salvador'

/** Calendar dates must not be parsed as UTC instants. */
export function formatDateOnly(value: string): string {
  const [year, month, day] = value.slice(0, 10).split('-')
  return `${day}/${month}/${year}`
}

export function clinicToday(now = new Date()): string {
  const parts = new Intl.DateTimeFormat('en-CA', {
    timeZone: CLINIC_ZONE, year: 'numeric', month: '2-digit', day: '2-digit',
  }).formatToParts(now)
  const part = (name: string) => parts.find(p => p.type === name)!.value
  return `${part('year')}-${part('month')}-${part('day')}`
}

export function ageOn(birthDate: string, today = clinicToday()): number {
  const years = Number(today.slice(0, 4)) - Number(birthDate.slice(0, 4))
  return years - (today.slice(5, 10) < birthDate.slice(5, 10) ? 1 : 0)
}
