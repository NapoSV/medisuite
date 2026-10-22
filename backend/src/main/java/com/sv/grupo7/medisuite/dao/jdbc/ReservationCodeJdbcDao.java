package com.sv.grupo7.medisuite.dao.jdbc;

import com.sv.grupo7.medisuite.exception.DataAccessException;
import com.sv.grupo7.medisuite.exception.JdbcErrorCode;
import com.sv.grupo7.medisuite.service.port.ReservationCodePort;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

    public long nextValue() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(SQL_NEXT_VALUE);
             ResultSet rs = ps.executeQuery()) {
            if (!rs.next()) {
                throw new DataAccessException("siguiente código de reserva", JdbcErrorCode.UNKNOWN, null);
            }
            return rs.getLong(1);
        } catch (SQLException e) {
            throw new DataAccessException("siguiente código de reserva", JdbcErrorCode.from(e), e);
        }
    }

    @Override
    public String nextCode(Long tenantId) {
        long value = nextValue();
        if (value < 1 || value > MAX_SEQUENCE_VALUE) {
            throw new IllegalStateException(
                    "La secuencia de códigos de reserva superó el límite de ocho dígitos");
        }
        return String.format("R-%08d", value);
    }
}
