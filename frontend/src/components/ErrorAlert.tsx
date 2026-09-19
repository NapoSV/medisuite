import { AlertTriangle } from 'lucide-react';

type ErrorAlertProps = {
  message: string;
  detail?: string | null;
  onRetry?: () => void;
  retryLabel?: string;
  className?: string;
};

export default function ErrorAlert({
  message,
  detail,
  onRetry,
  retryLabel = 'Reintentar',
  className = '',
}: ErrorAlertProps) {
  return (
    <div
      role="alert"
      className={[
        'flex items-start gap-3 rounded-md border border-[#991B1B]/20 bg-[#991B1B]/5 p-4',
        className,
      ].join(' ')}
    >
      <AlertTriangle
        className="mt-0.5 h-5 w-5 flex-shrink-0 text-[#991B1B]"
        aria-hidden="true"
      />
      <div className="flex-1 min-w-0">
        <p className="text-sm font-medium text-[#991B1B]">{message}</p>
        {detail && (
          <p className="text-sm text-[#991B1B]/80 mt-0.5">{detail}</p>
        )}
      </div>
      {onRetry && (
        <button
          type="button"
          onClick={onRetry}
          className="text-sm font-medium text-[#991B1B] hover:underline flex-shrink-0"
        >
          {retryLabel}
        </button>
      )}
    </div>
  );
}
