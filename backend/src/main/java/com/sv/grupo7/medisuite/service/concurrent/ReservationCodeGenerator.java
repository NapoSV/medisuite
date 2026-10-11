package com.sv.grupo7.medisuite.service.concurrent;

import com.sv.grupo7.medisuite.dao.jdbc.ReservationCodeJdbcDao;
import org.springframework.stereotype.Component;

/**
 * Genera códigos de reserva de 10 caracteres: {@code R-} + 8 dígitos.
 * La fuente de verdad es la secuencia de PostgreSQL, no un contador en memoria.
 */
@Component
public class ReservationCodeGenerator {

    static final long MAX_SEQUENCE_VALUE = 99_999_999L;

    private final ReservationCodeJdbcDao dao;

    public ReservationCodeGenerator(ReservationCodeJdbcDao dao) {
        this.dao = dao;
    }

    public String next() {
        long value = dao.nextValue();
        if (value < 1 || value > MAX_SEQUENCE_VALUE) {
            throw new IllegalStateException(
                    "La secuencia de códigos de reserva superó el límite de ocho dígitos");
        }
        return String.format("R-%08d", value);
    }
}
