package com.sv.grupo7.medisuite.integration;

import com.sv.grupo7.medisuite.dto.auth.LoginRequest;
import com.sv.grupo7.medisuite.exception.BusinessException;
import com.sv.grupo7.medisuite.security.JwtTokenProvider;
import com.sv.grupo7.medisuite.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Regression through HTTP and committed PostgreSQL state, not repository mocks. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
class FunctionalRegressionIT {
    @Container
    static PostgreSQLContainer<?> pg = new PostgreSQLContainer<>("postgres:16-alpine")
            .withInitScript("schema.sql");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", pg::getJdbcUrl);
        r.add("spring.datasource.username", pg::getUsername);
        r.add("spring.datasource.password", pg::getPassword);
        r.add("spring.flyway.url", pg::getJdbcUrl);
        r.add("spring.flyway.user", pg::getUsername);
        r.add("spring.flyway.password", pg::getPassword);
    }

    @Autowired MockMvc mvc;
    @Autowired JdbcTemplate jdbc;
    @Autowired PasswordEncoder encoder;
    @Autowired JwtTokenProvider tokens;
    @Autowired AuthService auth;
    @LocalServerPort int port;

    @BeforeEach
    void seed() {
        jdbc.execute("TRUNCATE tenants RESTART IDENTITY CASCADE");
        jdbc.update("INSERT INTO tenants (slug, commercial_name) VALUES ('qa-a','QA A'),('qa-b','QA B')");
        String hash = encoder.encode("TestingOnly2026!");
        String[] roles = {"DOCTOR", "RECEPTIONIST", "ADMIN", "DOCTOR", "NURSE"};
        for (int i = 0; i < roles.length; i++) {
            jdbc.update("INSERT INTO users (tenant_id,first_name,last_name,cif,email,password_hash,role) "
                    + "VALUES (1,'Ana','QA',?,?,?,?)", "QA-"+i, "user"+i+"@qa.test", hash, roles[i]);
        }
        jdbc.update("INSERT INTO specialties (tenant_id,name) VALUES (1,'General'),(2,'Otra clinica')");
        jdbc.update("INSERT INTO doctors (tenant_id,user_id,specialty_id,license_number) VALUES "
                + "(1,1,1,'QA-1'),(1,4,1,'QA-2')");
        jdbc.update("INSERT INTO patients (tenant_id,first_name,last_name,dui,birth_date) VALUES "
                + "(1,'Paciente','Uno','11111111-1','1990-01-01'),"
                + "(2,'Paciente','Dos','22222222-2','1991-01-01')");
    }

    private String bearer(long userId, String role) {
        return "Bearer " + tokens.generateToken(userId, role, 1L);
    }

    @Test
    void createCannotOverwritePatientFromAnotherClinic() throws Exception {
        mvc.perform(post("/api/patients").header("Authorization",bearer(1,"DOCTOR"))
                .contentType(MediaType.APPLICATION_JSON).content("""
                {"id":2,"firstName":"Alterado","lastName":"QA","dui":"22222222-2","birthDate":"1991-01-01"}
                """)).andExpect(status().is4xxClientError());
        assertThat(jdbc.queryForObject("SELECT tenant_id FROM patients WHERE id=2",Long.class)).isEqualTo(2L);
        assertThat(jdbc.queryForObject("SELECT first_name FROM patients WHERE id=2",String.class)).isEqualTo("Paciente");
    }

    @Test
    void patientUpdatePersistsBirthDateAndRejectsInvalidInput() throws Exception {
        mvc.perform(put("/api/patients/1").header("Authorization",bearer(2,"RECEPTIONIST"))
                .contentType(MediaType.APPLICATION_JSON).content("""
                {"firstName":"Paciente","lastName":"Uno","dui":"11111111-1","birthDate":"2000-03-03"}
                """)).andExpect(status().isOk()).andExpect(jsonPath("$.birthDate").value("2000-03-03"));
        assertThat(jdbc.queryForObject("SELECT birth_date::text FROM patients WHERE id=1",String.class))
                .isEqualTo("2000-03-03");
        mvc.perform(post("/api/patients").header("Authorization",bearer(2,"RECEPTIONIST"))
                .contentType(MediaType.APPLICATION_JSON).content("""
                {"firstName":"","lastName":"Uno","dui":"no-es-dui","birthDate":"2999-01-01"}
                """)).andExpect(status().isBadRequest());
    }

    private String doctorBody(int specialtyId) {
        return """
                {"firstName":"Nuevo","lastName":"Medico","email":"nuevo@qa.test","cif":"NUEVO",
                 "specialtyId":%d,"licenseNumber":"QA-NUEVO","initialPassword":"InitialOnly2026!"}
                """.formatted(specialtyId);
    }

    @Test
    void patientSearchAcceptsFullNameAndStaysWithinClinic() throws Exception {
        mvc.perform(get("/api/patients").param("search", "  paciente uno  ")
                .header("Authorization", bearer(2,"RECEPTIONIST")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].id").value(1));
        mvc.perform(get("/api/patients").param("search", "paciente dos")
                .header("Authorization", bearer(2,"RECEPTIONIST")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void onlyAdminCanCreateDoctorsAndSpecialtyMustBelongToClinic() throws Exception {
        mvc.perform(post("/api/doctors").header("Authorization",bearer(2,"RECEPTIONIST"))
                .contentType(MediaType.APPLICATION_JSON).content(doctorBody(1)))
                .andExpect(status().isForbidden());
        mvc.perform(post("/api/doctors").header("Authorization",bearer(3,"ADMIN"))
                .contentType(MediaType.APPLICATION_JSON).content(doctorBody(2)))
                .andExpect(status().isUnprocessableEntity());
        mvc.perform(post("/api/doctors").header("Authorization",bearer(3,"ADMIN"))
                .contentType(MediaType.APPLICATION_JSON).content(doctorBody(1)))
                .andExpect(status().isCreated());
        assertThat(auth.login(new LoginRequest("qa-a","nuevo@qa.test","InitialOnly2026!")).accessToken())
                .isNotBlank();
    }

    @Test
    void failedLoginsCommitAndLockAccountThenSuccessfulLoginResetsIt() {
        LoginRequest wrong = new LoginRequest("qa-a","user0@qa.test","WrongPassword!");
        for (int attempt=1; attempt<=5; attempt++) {
            assertThatThrownBy(() -> auth.login(wrong)).isInstanceOf(BadCredentialsException.class);
            assertThat(jdbc.queryForObject("SELECT failed_login_attempts FROM users WHERE id=1",Integer.class))
                    .isEqualTo(attempt);
        }
        LoginRequest correct = new LoginRequest("qa-a","user0@qa.test","TestingOnly2026!");
        assertThatThrownBy(() -> auth.login(correct)).isInstanceOf(BusinessException.class);
        jdbc.update("UPDATE users SET locked_until=now()-interval '1 minute' WHERE id=1");
        assertThat(auth.login(correct).accessToken()).isNotBlank();
        assertThat(jdbc.queryForObject("SELECT failed_login_attempts FROM users WHERE id=1",Integer.class)).isZero();
    }

    @Test
    void printablePrescriptionIncludesPatientDateAndMedication() throws Exception {
        jdbc.update("INSERT INTO medical_records (tenant_id,patient_id) VALUES (1,1)");
        jdbc.update("INSERT INTO prescriptions (tenant_id,medical_record_id,doctor_id,issued_on,instructions) "
                + "VALUES (1,1,1,'2026-10-25','Indicaciones QA')");
        jdbc.update("INSERT INTO prescription_items (prescription_id,medication,dose,frequency,duration_days) "
                + "VALUES (1,'Medicamento QA','Dosis QA','Frecuencia QA',3)");
        mvc.perform(get("/api/prescriptions/1/print").header("Authorization",bearer(1,"DOCTOR")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.issuedOn").value("2026-10-25"))
                .andExpect(jsonPath("$.patient.firstName").value("Paciente"))
                .andExpect(jsonPath("$.patient.dui").value("11111111-1"))
                .andExpect(jsonPath("$.items[0].dosage").value("Dosis QA"));
        mvc.perform(get("/api/prescriptions/1/print").header("Authorization",
                "Bearer "+tokens.generateToken(99L,"DOCTOR",2L)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void appointmentListUsesDoctorIdAndIncludesEarlierToday() throws Exception {
        jdbc.update("INSERT INTO appointments (tenant_id,patient_id,doctor_id,scheduled_at,reservation_code) VALUES "
                + "(1,1,1,date_trunc('day',now() AT TIME ZONE 'America/El_Salvador') AT TIME ZONE 'America/El_Salvador','QA0001'),"
                + "(1,1,2,now()+interval '2 days','QA0002')");
        mvc.perform(get("/api/appointments?upcoming=true").header("Authorization",bearer(1,"DOCTOR")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].reservationCode").value("QA0001"));
        mvc.perform(post("/api/appointments/2/complete").header("Authorization",bearer(1,"DOCTOR")))
                .andExpect(status().isForbidden());
    }

    @Test
    void receptionCannotWriteClinicalData() throws Exception {
        mvc.perform(post("/api/patients/1/vital-signs").header("Authorization",bearer(2,"RECEPTIONIST"))
                .contentType(MediaType.APPLICATION_JSON).content("{\"priority\":\"NORMAL\"}"))
                .andExpect(status().isForbidden());
        mvc.perform(post("/api/prescriptions").header("Authorization",bearer(2,"RECEPTIONIST"))
                .contentType(MediaType.APPLICATION_JSON).content("{\"medicalRecordId\":1,\"items\":[]}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void realHttpPermissionDenialRemains403InsteadOfExpiringTheSession() throws Exception {
        var request = java.net.http.HttpRequest.newBuilder(
                        java.net.URI.create("http://127.0.0.1:"+port+"/api/patients/1/vital-signs"))
                .header("Authorization", bearer(2,"RECEPTIONIST"))
                .header("Content-Type", "application/json")
                .POST(java.net.http.HttpRequest.BodyPublishers.ofString("{\"priority\":\"NORMAL\"}"))
                .build();
        var response = java.net.http.HttpClient.newHttpClient().send(request,
                java.net.http.HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(403);
        assertThat(response.body()).contains("No tienes permiso");
    }
}
