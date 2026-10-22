package com.sv.grupo7.medisuite.service.concurrent;

import com.sv.grupo7.medisuite.service.port.ReservationCodePort;
import org.springframework.stereotype.Component;

/**
 * Componente de servicio que entrega códigos de reserva de 10 caracteres
 * ({@code R-} + 8 dígitos) a {@code AppointmentService}.
 *
 * <p>Depende del puerto {@link ReservationCodePort}, no de la implementación JDBC.
 * El formato y el límite de ocho dígitos viven en un solo lugar
 * ({@code ReservationCodeJdbcDao.nextCode}); la fuente de verdad es la secuencia
 * de PostgreSQL, no un contador en memoria.</p>
 */
@Component
public class ReservationCodeGenerator {

    private final ReservationCodePort port;

    public ReservationCodeGenerator(ReservationCodePort port) {
        this.port = port;
    }

    public String next() {
        return port.nextCode();
    }
}
