package com.sv.grupo7.medisuite.controller.api;

import com.sv.grupo7.medisuite.exception.DataAccessException;
import com.sv.grupo7.medisuite.exception.JdbcErrorCode;
import com.sv.grupo7.medisuite.model.medical.Appointment;
import com.sv.grupo7.medisuite.security.TenantContext;
import com.sv.grupo7.medisuite.service.concurrent.ReservationCodeGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * B3b — política de reintentos de {@code AppointmentController.create()}.
 *
 * <p>Fuerza colisiones de código con un generador espiado sobre PostgreSQL real
 * para comprobar que el error de unicidad llega al controlador y que cada
 * reintento corre en una transacción nueva.</p>
 */
@SpringBootTest
@Testcontainers
class AppointmentControllerRetryTest {

    private static final String COLLIDING_CODE = "R-00000001";

    @Container
    static PostgreSQLContainer<?> pg = new PostgreSQLContainer<>("postgres:16-alpine")
            .withInitScript("schema.sql");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", pg::getJdbcUrl);
        r.add("spring.datasource.username", pg::getUsername);
        r.add("spring.datasource.password", pg::getPassword);
        r.add("spring.flyway.url", pg::getJdbcUrl);
        r.add("spring.flyway.user", pg::getUsername);
        r.add("spring.flyway.password", pg::getPassword);
    }

    @Autowired AppointmentController controller;
    @Autowired JdbcTemplate jdbc;
    @SpyBean ReservationCodeGenerator generator;

    long tenantId;
    long doctorId;
    long patientId;

    @BeforeEach
    void seed() {
        reset(generator);
        // La secuencia real arrancaría en 1 y chocaría con el código sembrado como colisión.
        jdbc.queryForObject("SELECT setval('reservation_code_seq', 100)", Long.class);
        jdbc.update("DELETE FROM appointments");
        jdbc.update("DELETE FROM doctors");
        jdbc.update("DELETE FROM patients");
        jdbc.update("DELETE FROM specialties");
        jdbc.update("DELETE FROM users");
        jdbc.update("DELETE FROM tenants");

        tenantId = jdbc.queryForObject(
                "INSERT INTO tenants (slug, commercial_name) VALUES ('t1', 'T1') RETURNING id", Long.class);
        long userId = jdbc.queryForObject(
                "INSERT INTO users (tenant_id, first_name, last_name, cif, email, password_hash, role) "
                        + "VALUES (?, 'Doc', 'Test', '1', 'doc@test.local', 'x', 'DOCTOR') RETURNING id",
                Long.class, tenantId);
        long specialtyId = jdbc.queryForObject(
                "INSERT INTO specialties (tenant_id, name) VALUES (?, 'General') RETURNING id",
                Long.class, tenantId);
        doctorId = jdbc.queryForObject(
                "INSERT INTO doctors (tenant_id, user_id, specialty_id, license_number) "
                        + "VALUES (?, ?, ?, 'LIC-1') RETURNING id",
                Long.class, tenantId, userId, specialtyId);
        patientId = jdbc.queryForObject(
                "INSERT INTO patients (tenant_id, birth_date, first_name, last_name, dui) "
                        + "VALUES (?, '1990-01-01', 'Pac', 'Test', '00000001') RETURNING id",
                Long.class, tenantId);

        jdbc.update("INSERT INTO appointments (tenant_id, patient_id, doctor_id, scheduled_at, status, reservation_code) "
                + "VALUES (?, ?, ?, now() + interval '1 day', 'PENDING', ?)",
                tenantId, patientId, doctorId, COLLIDING_CODE);

        TenantContext.set(tenantId);
    }

    @AfterEach
    void clearTenant() {
        TenantContext.clear();
    }

    @Test
    void create_reintentaTrasColisionesYTerminaConCodigoNuevo() {
        doReturn(COLLIDING_CODE).doReturn(COLLIDING_CODE).doCallRealMethod().when(generator).next();

        ResponseEntity<Appointment> response = controller.create(
                new AppointmentController.CreateRequest(
                        patientId, doctorId, OffsetDateTime.now().plusDays(2), "Consulta"));

        assertThat(response.getStatusCode().value()).isEqualTo(201);
        assertThat(response.getBody().getReservationCode())
                .matches("R-\\d{8}")
                .isNotEqualTo(COLLIDING_CODE);
        verify(generator, times(3)).next();
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM appointments", Long.class)).isEqualTo(2L);
    }

    @Test
    void create_alCuartoIntentoFallidoPropagaDataAccessExceptionUniqueViolation() {
        doReturn(COLLIDING_CODE).when(generator).next();

        assertThatThrownBy(() -> controller.create(
                new AppointmentController.CreateRequest(
                        patientId, doctorId, OffsetDateTime.now().plusDays(2), "Consulta")))
                .isInstanceOfSatisfying(DataAccessException.class,
                        e -> assertThat(e.getCode()).isEqualTo(JdbcErrorCode.UNIQUE_VIOLATION));

        verify(generator, times(4)).next();
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM appointments", Long.class)).isEqualTo(1L);
    }
}
