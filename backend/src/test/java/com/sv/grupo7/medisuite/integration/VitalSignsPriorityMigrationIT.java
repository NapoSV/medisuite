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
@DisplayName("Validación de V14 (escala de prioridad de triaje) en PostgreSQL descartable")
class VitalSignsPriorityMigrationIT {

    @Test
    @DisplayName("V13 a V14: convierte LOW/MEDIUM/HIGH/CRITICAL y solo acepta NORMAL/URGENTE/EMERGENCIA")
    void desdeV13_debeConvertirPrioridadesYReemplazarRestriccion() throws Exception {
        try (PostgreSQLContainer<?> pg = new PostgreSQLContainer<>("postgres:16-alpine")
                .withInitScript("schema.sql")) {
            pg.start();

            Flyway hastaV13 = migrador(pg, "13");
            hastaV13.migrate();
            assertEquals("13", hastaV13.info().current().getVersion().toString());

            try (Connection connection = conectar(pg)) {
                crearDatosSinteticos(connection);
                ejecutar(connection, """
                        INSERT INTO vital_signs (id, tenant_id, medical_record_id, priority)
                        VALUES (1, 1, 1, 'LOW'), (2, 1, 1, 'MEDIUM'), (3, 1, 1, 'HIGH'),
                               (4, 1, 1, 'CRITICAL'), (5, 1, 1, NULL)
                        """);
            }

            Flyway hastaV14 = migrador(pg, "14");
            assertEquals(1, hastaV14.migrate().migrationsExecuted);
            assertEquals("14", hastaV14.info().current().getVersion().toString());

            try (Connection connection = conectar(pg)) {
                assertEquals("NORMAL", prioridad(connection, 1));
                assertEquals("URGENTE", prioridad(connection, 2));
                assertEquals("URGENTE", prioridad(connection, 3));
                assertEquals("EMERGENCIA", prioridad(connection, 4));
                assertNull(prioridad(connection, 5));

                ejecutar(connection, """
                        INSERT INTO vital_signs (id, tenant_id, medical_record_id, priority)
                        VALUES (6, 1, 1, 'EMERGENCIA')
                        """);

                SQLException rechazada = assertThrows(SQLException.class, () -> ejecutar(connection, """
                        INSERT INTO vital_signs (id, tenant_id, medical_record_id, priority)
                        VALUES (7, 1, 1, 'CRITICAL')
                        """));
                assertEquals("23514", rechazada.getSQLState());
            }

            assertEquals(0, hastaV14.migrate().migrationsExecuted);
        }
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

    private void crearDatosSinteticos(Connection connection) throws SQLException {
        ejecutar(connection, """
                INSERT INTO tenants (id, slug, commercial_name)
                VALUES (1, 'v14-qa', 'Clinica sintetica V14')
                """);
        ejecutar(connection, """
                INSERT INTO patients (id, tenant_id, birth_date, first_name, last_name, dui)
                VALUES (1, 1, '1990-01-01', 'Paciente', 'Prueba', '00000000-0')
                """);
        ejecutar(connection, """
                INSERT INTO medical_records (id, tenant_id, patient_id)
                VALUES (1, 1, 1)
                """);
    }

    private String prioridad(Connection connection, long id) throws SQLException {
        try (Statement statement = connection.createStatement();
             var result = statement.executeQuery("SELECT priority FROM vital_signs WHERE id = " + id)) {
            assertTrue(result.next());
            return result.getString(1);
        }
    }

    private void ejecutar(Connection connection, String sql) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }
}
