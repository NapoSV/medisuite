package com.sv.grupo7.medisuite.dao.jdbc;

import com.sv.grupo7.medisuite.exception.DataAccessException;
import com.sv.grupo7.medisuite.exception.JdbcErrorCode;
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
 * <p>La unicidad la garantiza PostgreSQL: nextval() es atómico entre hilos y
 * entre instancias de la aplicación. Usa el pool dedicado {@code jdbcDataSource} (B2).
 * Cuando B4 integre los puertos definitivos, esta clase pasará a implementarlos.</p>
 */
@Repository
public class ReservationCodeJdbcDao {

    private static final String SQL_NEXT_VALUE = "SELECT nextval('reservation_code_seq')";

    private final DataSource dataSource;

    public ReservationCodeJdbcDao(@Qualifier("jdbcDataSource") DataSource dataSource) {
        this.dataSource = dataSource;
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
}
