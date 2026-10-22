package com.sv.grupo7.medisuite.exception;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.CannotCreateTransactionException;

import java.sql.SQLException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void uniqueViolationDevuelve409SinExponerSql() {
        SQLException sql = new SQLException(
                "duplicate key value violates unique constraint \"uk_reserva_codigo\"", "23505");
        DataAccessException ex = new DataAccessException(
                "reserva.crear", JdbcErrorCode.from(sql), sql);

        ResponseEntity<Map<String, Object>> resp = handler.handleDataAccess(ex);

        assertEquals(HttpStatus.CONFLICT, resp.getStatusCode());
        assertNotNull(resp.getBody());
        assertEquals("data_access_unique_violation", resp.getBody().get("error"));
        String cuerpo = resp.getBody().toString();
        assertFalse(cuerpo.contains("uk_reserva_codigo"));
        assertFalse(cuerpo.contains("duplicate key"));
    }

    @Test
    void connectionLostDevuelve503() {
        SQLException sql = new SQLException("connection failure", "08006");
        DataAccessException ex = new DataAccessException(
                "dashboard.metrics", JdbcErrorCode.from(sql), sql);

        ResponseEntity<Map<String, Object>> resp = handler.handleDataAccess(ex);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, resp.getStatusCode());
        assertNotNull(resp.getBody());
        assertEquals("data_access_connection_lost", resp.getBody().get("error"));
    }

    @Test
    void errorDesconocidoDevuelve500() {
        DataAccessException ex = new DataAccessException(
                "dashboard.metrics", JdbcErrorCode.UNKNOWN, new RuntimeException("raro"));

        ResponseEntity<Map<String, Object>> resp = handler.handleDataAccess(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
        assertNotNull(resp.getBody());
        assertFalse(resp.getBody().toString().contains("raro"));
    }

    @Test
    void businessExceptionSigueDevolviendo422() {
        ResponseEntity<Map<String, String>> resp =
                handler.handleBusiness(new BusinessException("regla de negocio"));

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, resp.getStatusCode());
        assertNotNull(resp.getBody());
        assertEquals("business_rule", resp.getBody().get("error"));
    }

    @Test
    void bdCaidaAlAbrirTransaccionJpaDevuelve503() {
        // Así llega la BD caída por JPA/Hikari: la SQLException original va como causa.
        SQLException sql = new SQLException("Connection to db:5432 refused", "08001");
        CannotCreateTransactionException ex = new CannotCreateTransactionException(
                "Could not open JPA EntityManager for transaction", sql);

        ResponseEntity<Map<String, Object>> resp = handler.handleSpringDataAccess(ex);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, resp.getStatusCode());
        assertNotNull(resp.getBody());
        assertEquals("data_access_connection_lost", resp.getBody().get("error"));
        assertFalse(resp.getBody().toString().contains("db:5432"));
    }

    @Test
    void excepcionDeSpringSinSqlStateDevuelve500Generico() {
        DataAccessResourceFailureException ex = new DataAccessResourceFailureException(
                "detalle interno", new SQLException("private SQL details"));

        ResponseEntity<Map<String, Object>> resp = handler.handleSpringDataAccess(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
        assertNotNull(resp.getBody());
        assertFalse(resp.getBody().toString().contains("private SQL details"));
    }

    @Test
    void duplicadoGuardadoPorJpaDevuelve409() {
        SQLException sql = new SQLException("duplicate key value \"ux_patients_dui_tenant\"", "23505");
        DataIntegrityViolationException ex = new DataIntegrityViolationException("could not execute statement", sql);

        ResponseEntity<Map<String, Object>> resp = handler.handleSpringDataAccess(ex);

        assertEquals(HttpStatus.CONFLICT, resp.getStatusCode());
        assertNotNull(resp.getBody());
        assertFalse(resp.getBody().toString().contains("ux_patients_dui_tenant"));
    }

    @Test
    void conflictoDeVersionDevuelve409ConMensajeClaro() {
        ObjectOptimisticLockingFailureException ex =
                new ObjectOptimisticLockingFailureException("Appointment", 15L);

        ResponseEntity<Map<String, Object>> resp = handler.handleOptimisticLock(ex);

        assertEquals(HttpStatus.CONFLICT, resp.getStatusCode());
        assertNotNull(resp.getBody());
        assertEquals("concurrent_modification", resp.getBody().get("error"));
    }
}
