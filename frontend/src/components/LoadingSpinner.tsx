interface LoadingSpinnerProps {
    /** Texto opcional que se muestra debajo del spinner (ej. "Cargando citas...") */
    label?: string;
    /** Tamaño del spinner: sm | md | lg */
    size?: "sm" | "md" | "lg";
    /** Si es true, ocupa toda la altura disponible y centra el spinner */
    fullScreen?: boolean;
}

const sizeMap = {
    sm: "h-4 w-4 border-2",
    md: "h-8 w-8 border-2",
    lg: "h-12 w-12 border-4",
};

export default function LoadingSpinner({
                                           label = "Cargando...",
                                           size = "md",
                                           fullScreen = false,
                                       }: LoadingSpinnerProps) {
    const containerClass = fullScreen
        ? "flex flex-col items-center justify-center min-h-[200px] w-full"
        : "flex flex-col items-center justify-center py-6";

    return (
        <div className={containerClass} role="status" aria-live="polite">
            <div
                className={`${sizeMap[size]} animate-spin rounded-full border-blue-600 border-t-transparent`}
            />
            {label && (
                <p className="mt-3 text-sm text-gray-500">{label}</p>
            )}
        </div>
    );
}