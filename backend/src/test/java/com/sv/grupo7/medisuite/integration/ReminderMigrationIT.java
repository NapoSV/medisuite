package com.sv.grupo7.medisuite.integration;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

@Tag("integration")
@DisplayName("Validación de V11 en PostgreSQL descartable")
class ReminderMigrationIT {

    @Test
    @DisplayName("Esquema nuevo inicializado con schema.sql: baseline V9 hasta V11")
    void esquemaNuevo_debeMigrarHastaV11() throws Exception {
        try (PostgreSQLContainer<?> pg = nuevoContenedor()) {
            pg.start();

            Flyway flyway = migrador(pg, "11");
            flyway.migrate();

            assertEquals("11", flyway.info().current().getVersion().toString());

            try (Connection connection = conectar(pg)) {
                verificarV11(connection);
            }

            // Una segunda ejecución no debe repetir migraciones.
            assertEquals(0, flyway.migrate().migrationsExecuted);
        }
    }

    @Test
    @DisplayName("Esquema en V10: actualizar a V11 conserva datos y aplica restricciones")
    void desdeV10_debeMigrarHastaV11() throws Exception {
        try (PostgreSQLContainer<?> pg = nuevoContenedor()) {
            pg.start();

            Flyway hastaV10 = migrador(pg, "10");
            hastaV10.migrate();

            assertEquals("10", hastaV10.info().current().getVersion().toString());

            try (Connection connection = conectar(pg)) {
                assertEquals(0, contar(connection, """
                        SELECT COUNT(*)
                        FROM information_schema.tables
                        WHERE table_schema = 'public'
                          AND table_name = 'appointment_reminders'
                        """));

                crearDatosSinteticos(connection);
            }

            Flyway hastaV11 = migrador(pg, "11");
            hastaV11.migrate();

            assertEquals("11", hastaV11.info().current().getVersion().toString());

            try (Connection connection = conectar(pg)) {
                assertEquals(1, contar(connection,
                        "SELECT COUNT(*) FROM appointments WHERE id = 1"));
                verificarV11(connection);
            }
        }
    }

    private PostgreSQLContainer<?> nuevoContenedor() {
        return new PostgreSQLContainer<>("postgres:16-alpine")
                .withInitScript("schema.sql");
    }

    private Flyway migrador(PostgreSQLContainer<?> pg, String target) {
        return Flyway.configure()
                .dataSource(pg.getJdbcUrl(), pg.getUsername(), pg.getPassword())
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .baselineVersion("9")
                .target(target)
                .load();
    }

    private Connection conectar(PostgreSQLContainer<?> pg) throws SQLException {
        return DriverManager.getConnection(
                pg.getJdbcUrl(), pg.getUsername(), pg.getPassword());
    }

    private void verificarV11(Connection connection) throws SQLException {
        crearDatosSinteticos(connection);

        assertEquals(1, contar(connection, """
                SELECT COUNT(*)
                FROM pg_indexes
                WHERE schemaname = 'public'
                  AND tablename = 'appointment_reminders'
                  AND indexname = 'idx_appt_reminders_pending'
                  AND indexdef LIKE '%(tenant_id, status, scheduled_for)%'
                """));

        ejecutar(connection, """
                INSERT INTO appointment_reminders
                    (tenant_id, appointment_id, scheduled_for)
                VALUES (1, 1, '2030-01-01 08:00:00-06')
                """);

        assertEquals(1, contar(connection, """
                SELECT COUNT(*) FROM appointment_reminders
                WHERE tenant_id = 1 AND appointment_id = 1
                  AND status = 'PENDING'
                  AND created_at IS NOT NULL
                  AND updated_at IS NOT NULL
                """));

        SQLException duplicado = assertThrows(SQLException.class,
                () -> ejecutar(connection, """
                        INSERT INTO appointment_reminders
                            (tenant_id, appointment_id, scheduled_for)
                        VALUES (1, 1, '2030-01-01 08:00:00-06')
                        """));
        assertEquals("23505", duplicado.getSQLState());
        assertTrue(duplicado.getMessage()
                .contains("ux_appt_reminder_tenant_appt_when"));

        SQLException tenantInexistente = assertThrows(SQLException.class,
                () -> ejecutar(connection, """
                        INSERT INTO appointment_reminders
                            (tenant_id, appointment_id, scheduled_for)
                        VALUES (999, 1, '2030-01-02 08:00:00-06')
                        """));
        assertEquals("23503", tenantInexistente.getSQLState());

        SQLException citaInexistente = assertThrows(SQLException.class,
                () -> ejecutar(connection, """
                        INSERT INTO appointment_reminders
                            (tenant_id, appointment_id, scheduled_for)
                        VALUES (1, 999, '2030-01-02 08:00:00-06')
                        """));
        assertEquals("23503", citaInexistente.getSQLState());

        SQLException estadoInvalido = assertThrows(SQLException.class,
                () -> ejecutar(connection, """
                        INSERT INTO appointment_reminders
                            (tenant_id, appointment_id, scheduled_for, status)
                        VALUES (1, 1, '2030-01-03 08:00:00-06', 'SENT')
                        """));
        assertEquals("23514", estadoInvalido.getSQLState());
        assertTrue(estadoInvalido.getMessage()
                .contains("appointment_reminders_status_check"));

        ejecutar(connection, """
                UPDATE appointment_reminders SET status = 'CANCELLED'
                WHERE appointment_id = 1
                """);
        assertEquals(1, contar(connection, """
                SELECT COUNT(*) FROM appointment_reminders
                WHERE appointment_id = 1 AND status = 'CANCELLED'
                """));

        ejecutar(connection, "DELETE FROM appointments WHERE id = 1");
        assertEquals(0, contar(connection,
                "SELECT COUNT(*) FROM appointment_reminders"));
    }

    private void crearDatosSinteticos(Connection connection) throws SQLException {
        ejecutar(connection, """
                INSERT INTO tenants (id, slug, commercial_name)
                VALUES (1, 'b8-qa', 'Clinica sintetica B8')
                ON CONFLICT (id) DO NOTHING
                """);

        ejecutar(connection, """
                INSERT INTO users
                    (id, tenant_id, first_name, last_name, cif,
                     email, password_hash, role)
                VALUES (1, 1, 'Doctor', 'Prueba', 'B8-DOCTOR',
                        'doctor@b8.test', 'hash-sintetico', 'DOCTOR')
                ON CONFLICT (id) DO NOTHING
                """);

        ejecutar(connection, """
                INSERT INTO specialties (id, tenant_id, name)
                VALUES (1, 1, 'Especialidad de prueba')
                ON CONFLICT (id) DO NOTHING
                """);

        ejecutar(connection, """
                INSERT INTO doctors
                    (id, tenant_id, user_id, specialty_id, license_number)
                VALUES (1, 1, 1, 1, 'B8-LICENCIA')
                ON CONFLICT (id) DO NOTHING
                """);

        ejecutar(connection, """
                INSERT INTO patients
                    (id, tenant_id, birth_date, first_name, last_name, dui)
                VALUES (1, 1, '1990-01-01', 'Paciente', 'Prueba', '00000000-0')
                ON CONFLICT (id) DO NOTHING
                """);

        ejecutar(connection, """
                INSERT INTO appointments
                    (id, tenant_id, patient_id, doctor_id,
                     scheduled_at, status, reservation_code)
                VALUES (1, 1, 1, 1, '2030-01-01 09:00:00-06',
                        'PENDING', 'B8-0001')
                ON CONFLICT (id) DO NOTHING
                """);
    }

    private void ejecutar(Connection connection, String sql) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    private long contar(Connection connection, String sql) throws SQLException {
        try (Statement statement = connection.createStatement();
             var result = statement.executeQuery(sql)) {
            assertTrue(result.next());
            return result.getLong(1);
        }
    }
}