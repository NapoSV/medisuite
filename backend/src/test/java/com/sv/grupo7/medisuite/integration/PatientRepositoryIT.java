package com.sv.grupo7.medisuite.integration;

import com.sv.grupo7.medisuite.dao.PatientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class PatientRepositoryIT {

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

    @Autowired PatientRepository repo;

    @Test void contenedorArrastra() { assertThat(pg.isRunning()).isTrue(); }
    @Test void repositorioCarga()   { assertThat(repo).isNotNull(); }
    @Test void findAllNoExplota()   { assertThat(repo.findAll()).isNotNull(); }
}
