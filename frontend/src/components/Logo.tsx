import iconLight from '../assets/logo/icon-light.svg'
import iconDark from '../assets/logo/icon-dark.svg'
import iconOnPrimary from '../assets/logo/icon-on-primary.svg'

type LogoVariant = 'vertical' | 'horizontal' | 'icon-only' | 'icon-on-primary'
type LogoMode = 'light' | 'dark'

interface LogoProps {
  variant?: LogoVariant
  mode?: LogoMode
  className?: string
}

const SIZE: Record<LogoVariant, number> = {
  vertical: 64,
  horizontal: 40,
  'icon-only': 32,
  'icon-on-primary': 48,
}

export default function Logo({ variant = 'horizontal', mode = 'light', className }: LogoProps) {
  const size = SIZE[variant]

  const src =
    variant === 'icon-on-primary'
      ? iconOnPrimary
      : mode === 'dark'
        ? iconDark
        : iconLight

  if (variant === 'icon-only' || variant === 'icon-on-primary') {
    return <img src={src} alt="MediSuite" width={size} height={size} className={className} />
  }

  const textColor = mode === 'dark' ? '#F8FAFC' : '#0F172A'

  if (variant === 'vertical') {
    return (
      <div className={`flex flex-col items-center gap-3 ${className ?? ''}`}>
        <img src={src} alt="" width={size} height={size} aria-hidden="true" />
        <div className="flex flex-col items-center gap-1">
          <span style={{ fontSize: 20, fontWeight: 700, color: textColor, letterSpacing: '-0.01em', lineHeight: 1 }}>
            MediSuite
          </span>
          <span style={{ fontSize: 10, fontWeight: 500, color: '#64748B', letterSpacing: '0.08em', textTransform: 'uppercase' }}>
            Medical SaaS
          </span>
        </div>
      </div>
    )
  }

  // horizontal
  return (
    <div className={`flex items-center gap-3 ${className ?? ''}`}>
      <img src={src} alt="" width={size} height={size} aria-hidden="true" />
      <div className="flex flex-col gap-0.5">
        <span style={{ fontSize: 18, fontWeight: 700, color: textColor, letterSpacing: '-0.01em', lineHeight: 1 }}>
          MediSuite
        </span>
        <span style={{ fontSize: 10, fontWeight: 500, color: '#64748B', letterSpacing: '0.08em', textTransform: 'uppercase' }}>
          Medical SaaS
        </span>
      </div>
    </div>
  )
}
