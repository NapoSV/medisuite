package com.sv.grupo7.medisuite.model.medical;

/**
 * Estados de una cita. La columna {@code appointments.status} guarda el nombre del enum.
 *
 * <p>Centraliza la regla "una cita cancelada o completada ya no se modifica", que antes
 * estaba repetida en cancelar, completar y reprogramar.</p>
 */
public enum AppointmentStatus {
    PENDING,
    SCHEDULED,
    CONFIRMED,
    IN_WAITING,
    IN_CONSULTATION,
    COMPLETED,
    CANCELLED,
    NO_SHOW;

    /** {@code true} si la cita está cerrada (cancelada o completada). */
    public static boolean isClosed(String status) {
        return CANCELLED.name().equals(status) || COMPLETED.name().equals(status);
    }
}
