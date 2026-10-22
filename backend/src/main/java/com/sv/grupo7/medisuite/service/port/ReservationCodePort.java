package com.sv.grupo7.medisuite.service.port;

/**
 * Contrato del generador de códigos de reserva (B3b).
 *
 * <p>La implementación usa una secuencia de PostgreSQL, por lo que el código es
 * único entre hilos e instancias sin depender del tenant.</p>
 */
public interface ReservationCodePort {

    /** Siguiente código con formato {@code R-} + 8 dígitos (10 caracteres). */
    String nextCode();
}
