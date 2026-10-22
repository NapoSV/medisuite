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

import com.sv.grupo7.medisuite.model.medical.Patient;
import com.sv.grupo7.medisuite.model.tenant.Tenant;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import com.sv.grupo7.medisuite.dao.TenantRepository;

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
    @Autowired
    TenantRepository tenantRepo;

    @Test void contenedorArrastra() { assertThat(pg.isRunning()).isTrue(); }
    @Test void repositorioCarga()   { assertThat(repo).isNotNull(); }
    @Test void findAllNoExplota()   { assertThat(repo.findAll()).isNotNull(); }

    @Test
    @Transactional
    void pacientesDeUnaClinicaNoAparecenEnOtra() {
        Tenant clinicaA = tenantRepo.save(crearTenant("clinica-a"));
        Tenant clinicaB = tenantRepo.save(crearTenant("clinica-b"));

        Patient pacienteA = crearPatient(clinicaA, "Ana", "Lopez", "01234567-8");
        Patient pacienteB = crearPatient(clinicaB, "Maria", "Perez", "98765432-1");

        repo.save(pacienteA);
        repo.save(pacienteB);
        repo.flush();

        List<Patient> pacientesA = repo.findByTenantId(clinicaA.getId());
        List<Patient> pacientesB = repo.findByTenantId(clinicaB.getId());

        assertThat(pacientesA)
                .extracting(Patient::getDui)
                .containsExactly("01234567-8")
                .doesNotContain("98765432-1");

        assertThat(pacientesB)
                .extracting(Patient::getDui)
                .containsExactly("98765432-1")
                .doesNotContain("01234567-8");
    }


    @Test
    @Transactional
    void busquedaPorDuiONombreNoDevuelvePacientesDeOtraClinica() {
        Tenant clinicaA = tenantRepo.save(crearTenant("clinica-busqueda-a"));
        Tenant clinicaB = tenantRepo.save(crearTenant("clinica-busqueda-b"));

        Patient pacienteA = crearPatient(
                clinicaA, "Ana", "Lopez", "01234567-8");
        Patient pacienteB = crearPatient(
                clinicaB, "Ana", "Martinez", "98765432-1");

        repo.save(pacienteA);
        repo.save(pacienteB);
        repo.flush();

        Page<Patient> resultados = repo.searchByDuiOrName(
                clinicaA.getId(),
                "ana",
                PageRequest.of(0, 10)
        );

        assertThat(resultados.getContent())
                .extracting(Patient::getDui)
                .containsExactly("01234567-8")
                .doesNotContain("98765432-1");
    }


    private Tenant crearTenant(String slug) {
        Tenant tenant = new Tenant();
        tenant.setSlug(slug);
        tenant.setCommercialName("Clínica " + slug);
        tenant.setTimezone("America/El_Salvador");
        tenant.setDefaultLanguage("es");
        tenant.setPlan("FREE");
        tenant.setStatus("TRIAL");
        tenant.setMaxUsers(10);
        tenant.setMaxPatients(500);
        tenant.setMaxDoctors(10);
        return tenant;
    }

    private Patient crearPatient(
            Tenant tenant, String nombre, String apellido, String dui) {
        Patient patient = new Patient();
        patient.setTenant(tenant);
        patient.setFirstName(nombre);
        patient.setLastName(apellido);
        patient.setDui(dui);
        patient.setBirthDate(LocalDate.of(2000, 1, 1));
        return patient;
    }



}