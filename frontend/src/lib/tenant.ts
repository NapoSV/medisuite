export function getTenantSlug(): string {
  const hostname = window.location.hostname

  if (
    hostname === 'localhost' ||
    hostname === '127.0.0.1' ||
    hostname === '[::1]'
  ) {
    return import.meta.env.VITE_TENANT_SLUG ?? 'demo'
  }

  const parts = hostname.split('.')
  if (parts.length >= 3) {
    return parts[0]
  }

  return import.meta.env.VITE_TENANT_SLUG ?? 'demo'
}
