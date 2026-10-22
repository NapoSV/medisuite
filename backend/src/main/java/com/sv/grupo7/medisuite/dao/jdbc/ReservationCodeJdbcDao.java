package com.sv.grupo7.medisuite.dao.jdbc;

import com.sv.grupo7.medisuite.exception.DataAccessException;
import com.sv.grupo7.medisuite.exception.JdbcErrorCode;
import com.sv.grupo7.medisuite.service.port.ReservationCodePort;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

/**
 * DAO JDBC de la secuencia de códigos de reserva (V12).
 *
 * <p>Extiende {@link BaseJdbcDao} e implementa el puerto {@link ReservationCodePort} (B4).
 * La unicidad la garantiza PostgreSQL: {@code nextval()} es atómico entre hilos y
 * entre instancias de la aplicación. Usa el pool secundario {@code jdbcDataSource} (B2).</p>
 */
@Repository
public class ReservationCodeJdbcDao extends BaseJdbcDao implements ReservationCodePort {

    static final long MAX_SEQUENCE_VALUE = 99_999_999L;
    private static final String SQL_NEXT_VALUE = "SELECT nextval('reservation_code_seq')";

    @Autowired
    public ReservationCodeJdbcDao(@Qualifier("jdbcDataSource") ObjectProvider<DataSource> dedicated,
                                  DataSource primary) {
        super(dedicated.getIfAvailable(() -> primary));
    }

    /** Constructor directo usado por pruebas de integración con PostgreSQL descartable. */
    public ReservationCodeJdbcDao(DataSource dataSource) {
        super(dataSource);
    }

    /** {@code nextval()} es atómico entre hilos e instancias: nunca repite un valor. */
    public long nextValue() {
        return queryOne(SQL_NEXT_VALUE, rs -> rs.getLong(1))
                .orElseThrow(() -> new DataAccessException(
                        "siguiente código de reserva", JdbcErrorCode.UNKNOWN, null));
    }

    @Override
    public String nextCode() {
        long value = nextValue();
        if (value < 1 || value > MAX_SEQUENCE_VALUE) {
            throw new IllegalStateException(
                    "La secuencia de códigos de reserva superó el límite de ocho dígitos");
        }
        return String.format("R-%08d", value);
    }
}
