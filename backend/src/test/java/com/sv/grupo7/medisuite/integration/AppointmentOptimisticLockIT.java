package com.sv.grupo7.medisuite.integration;

import com.sv.grupo7.medisuite.dao.AppointmentRepository;
import com.sv.grupo7.medisuite.model.medical.Appointment;
import com.sv.grupo7.medisuite.security.TenantContext;
import com.sv.grupo7.medisuite.service.AppointmentService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Bloqueo optimista de citas (V15 + {@code @Version} en {@link Appointment}).
 *
 * <p>Simula dos usuarios que editan la misma cita: el primero confirma su cambio y el
 * segundo, que leyó la versión anterior, recibe un conflicto en lugar de pisar el dato.</p>
 */
@Tag("integration")
@DisplayName("Bloqueo optimista de citas en PostgreSQL descartable")
@SpringBootTest
@Testcontainers
class AppointmentOptimisticLockIT {

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

    @Autowired AppointmentService service;
    @Autowired AppointmentRepository repo;
    @Autowired JdbcTemplate jdbc;
    @Autowired PlatformTransactionManager txManager;

    long tenantId;
    long appointmentId;

    @BeforeEach
    void seed() {
        jdbc.update("DELETE FROM appointment_reminders");
        jdbc.update("DELETE FROM appointments");
        jdbc.update("DELETE FROM doctors");
        jdbc.update("DELETE FROM patients");
        jdbc.update("DELETE FROM specialties");
        jdbc.update("DELETE FROM users");
        jdbc.update("DELETE FROM tenants");

        tenantId = jdbc.queryForObject(
                "INSERT INTO tenants (slug, commercial_name) VALUES ('lock', 'Lock') RETURNING id", Long.class);
        long userId = jdbc.queryForObject(
                "INSERT INTO users (tenant_id, first_name, last_name, cif, email, password_hash, role) "
                        + "VALUES (?, 'Doc', 'Lock', '1', 'doc@lock.local', 'x', 'DOCTOR') RETURNING id",
                Long.class, tenantId);
        long specialtyId = jdbc.queryForObject(
                "INSERT INTO specialties (tenant_id, name) VALUES (?, 'General') RETURNING id",
                Long.class, tenantId);
        long doctorId = jdbc.queryForObject(
                "INSERT INTO doctors (tenant_id, user_id, specialty_id, license_number) "
                        + "VALUES (?, ?, ?, 'LIC-LOCK') RETURNING id",
                Long.class, tenantId, userId, specialtyId);
        long patientId = jdbc.queryForObject(
                "INSERT INTO patients (tenant_id, birth_date, first_name, last_name, dui) "
                        + "VALUES (?, '1990-01-01', 'Pac', 'Lock', '00000009') RETURNING id",
                Long.class, tenantId);
        appointmentId = jdbc.queryForObject(
                "INSERT INTO appointments (tenant_id, patient_id, doctor_id, scheduled_at, status, reservation_code) "
                        + "VALUES (?, ?, ?, now() + interval '3 days', 'PENDING', 'R-90000001') RETURNING id",
                Long.class, tenantId, patientId, doctorId);

        TenantContext.set(tenantId);
    }

    @AfterEach
    void clearTenant() {
        TenantContext.clear();
    }

    @Test
    @DisplayName("Dos ediciones simultáneas: gana la primera y la segunda recibe conflicto")
    void segundaEdicionConVersionVieja_esRechazadaSinPerderLaPrimera() {
        TransactionTemplate otraSesion = new TransactionTemplate(txManager);
        otraSesion.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

        assertThatThrownBy(() -> otraSesion.executeWithoutResult(usuarioB -> {
            // Usuario B abre la cita: lee la versión 0.
            Appointment vistaDeB = repo.findById(appointmentId).orElseThrow();
            assertThat(vistaDeB.getVersion()).isZero();

            // Mientras tanto, el usuario A cancela la misma cita y confirma (versión 1).
            otraSesion.executeWithoutResult(usuarioA ->
                    service.cancel(appointmentId, "El paciente no puede asistir"));

            // Usuario B guarda su cambio sobre la versión vieja.
            vistaDeB.setStatus("COMPLETED");
        })).isInstanceOf(ObjectOptimisticLockingFailureException.class);

        assertThat(jdbc.queryForObject(
                "SELECT status FROM appointments WHERE id = ?", String.class, appointmentId))
                .isEqualTo("CANCELLED");
        assertThat(jdbc.queryForObject(
                "SELECT version FROM appointments WHERE id = ?", Long.class, appointmentId))
                .isEqualTo(1L);
    }

    @Test
    @DisplayName("Una edición normal incrementa la versión")
    void edicionNormal_incrementaVersion() {
        service.reschedule(appointmentId, java.time.OffsetDateTime.now().plusDays(5).withNano(0));

        assertThat(jdbc.queryForObject(
                "SELECT version FROM appointments WHERE id = ?", Long.class, appointmentId))
                .isEqualTo(1L);
    }
}
