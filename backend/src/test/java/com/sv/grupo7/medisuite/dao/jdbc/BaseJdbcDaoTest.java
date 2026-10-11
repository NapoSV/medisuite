package com.sv.grupo7.medisuite.dao.jdbc;

import com.sv.grupo7.medisuite.exception.DataAccessException;
import com.sv.grupo7.medisuite.exception.JdbcErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BaseJdbcDaoTest {

    @Mock DataSource dataSource;
    @Mock Connection connection;
    @Mock PreparedStatement statement;
    @Mock ResultSet resultSet;

    BaseJdbcDao dao;

    @BeforeEach
    void setUp() throws Exception {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        dao = new BaseJdbcDao(dataSource) {};
    }

    @Test
    void queryDevuelveDosObjetosEnElMismoOrdenDeFilas() throws Exception {
        when(statement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getInt(1)).thenReturn(10, 20);

        List<Integer> rows = dao.query("SELECT id FROM probe ORDER BY id", rs -> rs.getInt(1));

        assertThat(rows).containsExactly(10, 20);
        verify(statement).executeQuery();
    }

    @Test
    void queryOneDevuelveVacioCuandoNoHayFilas() throws Exception {
        when(statement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        Optional<Integer> row = dao.queryOne("SELECT id FROM probe", rs -> rs.getInt(1));

        assertThat(row).isEmpty();
    }

    @Test
    void updateDevuelveFilasAfectadasIncluyendoCeroYParametriza() throws Exception {
        when(statement.executeUpdate()).thenReturn(2, 0);

        assertThat(dao.update("UPDATE probe SET name = ? WHERE tenant_id = ?", "nuevo", 5L)).isEqualTo(2);
        assertThat(dao.update("UPDATE probe SET name = ? WHERE tenant_id = ?", "nuevo", 99L)).isEqualTo(0);

        verify(statement).setObject(1, "nuevo");
        verify(statement).setObject(2, 5L);
    }

    @Test
    void terceraConsultaFallidaPreservaSqlExceptionYCodigo() throws Exception {
        when(resultSet.next()).thenReturn(false);
        when(statement.executeQuery())
                .thenReturn(resultSet, resultSet)
                .thenThrow(new SQLException("valor duplicado", "23505"));

        dao.queryOne("SELECT 1 FROM probe", rs -> rs.getInt(1));
        dao.queryOne("SELECT 1 FROM probe", rs -> rs.getInt(1));

        DataAccessException ex = org.junit.jupiter.api.Assertions.assertThrows(
                DataAccessException.class,
                () -> dao.queryOne("SELECT 1 FROM probe", rs -> rs.getInt(1)));

        assertThat(ex.getCode()).isEqualTo(JdbcErrorCode.UNIQUE_VIOLATION);
        assertThat(ex.getCause()).isInstanceOf(SQLException.class);
        assertThat(ex.getCause()).hasMessage("valor duplicado");
    }

    @Test
    void seCierranResultSetStatementYConnectionEnExitoYError() throws Exception {
        when(statement.executeQuery()).thenReturn(resultSet);

        dao.query("SELECT 1 FROM probe", rs -> rs.getInt(1));
        verify(resultSet).close();
        verify(statement).close();
        verify(connection).close();
    }

    @Test
    void seCierranStatementYConnectionEnErrorSinSqlEnRespuesta() throws Exception {
        when(statement.executeQuery())
                .thenThrow(new SQLException("SELECT * FROM patients WHERE dui='SECRETO'", "23514"));

        DataAccessException ex = org.junit.jupiter.api.Assertions.assertThrows(
                DataAccessException.class,
                () -> dao.query("SELECT 1 FROM probe", rs -> rs.getInt(1)));

        verify(statement).close();
        verify(connection).close();
        assertThat(ex.getMessage())
                .contains("query")
                .doesNotContain("SELECT", "patients", "dui", "SECRETO");
    }
}