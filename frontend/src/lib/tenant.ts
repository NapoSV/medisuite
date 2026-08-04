export function getTenantSlug(): string {
  const parts = window.location.hostname.split('.')
  if (parts.length >= 3) {
    return parts[0]
  }
  return import.meta.env.VITE_TENANT_SLUG ?? 'demo'
}
