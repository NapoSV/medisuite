import { Loader2 } from 'lucide-react';

type LoadingSpinnerProps = {
  label?: string;
  size?: number;
  fullScreen?: boolean;
  className?: string;
};

export default function LoadingSpinner({
  label,
  size = 32,
  fullScreen = false,
  className = '',
}: LoadingSpinnerProps) {
  return (
    <div
      role="status"
      aria-live="polite"
      className={[
        'flex flex-col items-center justify-center gap-3 text-muted',
        fullScreen ? 'min-h-screen w-full' : 'py-12',
        className,
      ].join(' ')}
    >
      <Loader2
        className="animate-spin text-primary"
        width={size}
        height={size}
        aria-hidden="true"
      />
      {label && <p className="text-sm font-medium text-muted">{label}</p>}
      <span className="sr-only">{label ?? 'Cargando'}</span>
    </div>
  );
}
