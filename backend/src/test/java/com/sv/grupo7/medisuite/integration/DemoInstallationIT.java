package com.sv.grupo7.medisuite.integration;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.testcontainers.containers.PostgreSQLContainer;
import java.sql.DriverManager;
import static org.assertj.core.api.Assertions.assertThat;

class DemoInstallationIT {
    @Test
    void officialSchemaSeedAndMigrationsProduceUsableDemo() throws Exception {
        try (var pg = new PostgreSQLContainer<>("postgres:16-alpine").withInitScript("schema.sql")) {
            pg.start();
            try (var c = DriverManager.getConnection(pg.getJdbcUrl(), pg.getUsername(), pg.getPassword())) {
                ScriptUtils.executeSqlScript(c, new ClassPathResource("seed.sql"));
            }
            Flyway.configure().dataSource(pg.getJdbcUrl(), pg.getUsername(), pg.getPassword())
                    .baselineOnMigrate(true).baselineVersion("9").load().migrate();
            try (var c = DriverManager.getConnection(pg.getJdbcUrl(), pg.getUsername(), pg.getPassword());
                 var s = c.createStatement()) {
                try (var r = s.executeQuery("SELECT count(*) FROM patients")) {
                    r.next(); assertThat(r.getInt(1)).isEqualTo(3);
                }
                try (var r = s.executeQuery("SELECT priority FROM vital_signs ORDER BY id")) {
                    r.next(); assertThat(r.getString(1)).isEqualTo("NORMAL");
                    r.next(); assertThat(r.getString(1)).isEqualTo("URGENTE");
                }
                try (var r = s.executeQuery("SELECT count(*) FROM prescription_items")) {
                    r.next(); assertThat(r.getInt(1)).isEqualTo(1);
                }
                try (var r = s.executeQuery("SELECT password_hash FROM users")) {
                    int users = 0;
                    var encoder = new BCryptPasswordEncoder();
                    while (r.next()) { assertThat(encoder.matches("Demo2026!",r.getString(1))).isTrue(); users++; }
                    assertThat(users).isEqualTo(5);
                }
                try (var r = s.executeQuery("SELECT version FROM flyway_schema_history ORDER BY installed_rank DESC LIMIT 1")) {
                    r.next(); assertThat(r.getString(1)).isEqualTo("15");
                }
            }
        }
    }
}
