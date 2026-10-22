package com.sv.grupo7.medisuite.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JdbcErrorCodeTest {

    @ParameterizedTest
    @CsvSource({
            "23505, UNIQUE_VIOLATION",
            "23503, FOREIGN_KEY_VIOLATION",
            "23502, NOT_NULL_VIOLATION",
            "23514, CHECK_VIOLATION",
            "08000, CONNECTION_LOST",
            "08001, CONNECTION_LOST",
            "08003, CONNECTION_LOST",
            "08006, CONNECTION_LOST",
            "57014, TIMEOUT",
            "40001, SERIALIZATION_FAILURE"
    })
    void mapeaSqlStateAlCodigoEsperado(String sqlState, JdbcErrorCode esperado) {
        SQLException ex = new SQLException("error", sqlState);
        assertEquals(esperado, JdbcErrorCode.from(ex));
    }

    @Test
    void nullDevuelveUnknown() {
        assertEquals(JdbcErrorCode.UNKNOWN, JdbcErrorCode.from(null));
    }

    @Test
    void excepcionSinSqlDevuelveUnknown() {
        assertEquals(JdbcErrorCode.UNKNOWN, JdbcErrorCode.from(new RuntimeException("x")));
    }

    @Test
    void sqlStateNuloDevuelveUnknown() {
        assertEquals(JdbcErrorCode.UNKNOWN, JdbcErrorCode.from(new SQLException("sin estado")));
    }

    @Test
    void sqlStateDesconocidoDevuelveUnknown() {
        assertEquals(JdbcErrorCode.UNKNOWN, JdbcErrorCode.from(new SQLException("x", "99999")));
    }

    @Test
    void sqlExceptionAnidadaComoCausa() {
        SQLException sql = new SQLException("dup", "23505");
        RuntimeException envoltura = new RuntimeException("envuelta", sql);
        assertEquals(JdbcErrorCode.UNIQUE_VIOLATION, JdbcErrorCode.from(envoltura));
    }

    @Test
    void usaGetNextException() {
        SQLException primera = new SQLException("primera");
        primera.setNextException(new SQLException("segunda", "40001"));
        assertEquals(JdbcErrorCode.SERIALIZATION_FAILURE, JdbcErrorCode.from(primera));
    }
}