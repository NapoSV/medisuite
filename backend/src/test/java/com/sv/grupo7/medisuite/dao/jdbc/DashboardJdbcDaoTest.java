package com.sv.grupo7.medisuite.dao.jdbc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sv.grupo7.medisuite.dto.dashboard.DashboardQuery;
import com.sv.grupo7.medisuite.dto.dashboard.DashboardResponse;
import org.junit.jupiter.api.Test;
import org.postgresql.ds.PGSimpleDataSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers(disabledWithoutDocker = true)
class DashboardJdbcDaoTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Test
    void postgresqlQueriesRespectTenantDoctorRoleAndEmptyLists() throws Exception {
        PGSimpleDataSource source = new PGSimpleDataSource();
        source.setUrl(postgres.getJdbcUrl());
        source.setUser(postgres.getUsername());
        source.setPassword(postgres.getPassword());
        try (Connection c = source.getConnection(); Statement s = c.createStatement()) {
            s.execute("CREATE TABLE patients (id bigint primary key, tenant_id bigint not null, first_name text, last_name text)");
            s.execute("CREATE TABLE doctors (id bigint primary key, tenant_id bigint not null)");
            s.execute("CREATE TABLE appointments (id bigint primary key, tenant_id bigint not null, patient_id bigint, doctor_id bigint, scheduled_at timestamptz, status text)");
            s.execute("CREATE TABLE prescriptions (id bigint primary key, tenant_id bigint not null, medical_record_id bigint, doctor_id bigint, issued_on date)");
            s.execute("CREATE TABLE medical_records (id bigint primary key, tenant_id bigint not null, patient_id bigint)");
            s.execute("CREATE TABLE vital_signs (id bigint primary key, tenant_id bigint not null, medical_record_id bigint, priority text, recorded_at timestamptz)");

            s.execute("INSERT INTO patients VALUES (1,1,'Ana','Uno'),(2,1,'Beto','Dos'),(3,2,'Cora','Tres')");
            s.execute("INSERT INTO doctors VALUES (11,1),(12,1),(21,2)");
            s.execute("INSERT INTO appointments VALUES "
                    + "(1,1,1,11,'2026-10-12 06:00:00+00','IN_WAITING'),"
                    + "(2,1,2,12,'2026-10-12 16:00:00+00','CONFIRMED'),"
                    + "(3,2,3,21,'2026-10-12 15:00:00+00','WAITING'),"
                    // FKs by ID alone allow these tenant-inconsistent appointments.
                    + "(4,1,3,11,'2026-10-12 17:00:00+00','WAITING'),"
                    + "(5,1,1,21,'2026-10-12 18:00:00+00','WAITING'),"
                    // Local day starts at 06:00Z; the end is exclusive.
                    + "(6,1,1,11,'2026-10-12 05:59:59+00','WAITING'),"
                    + "(7,1,1,11,'2026-10-13 06:00:00+00','WAITING')");
            // The database FK allows a mismatched tenant/patient pair; dashboard reads must reject it.
            s.execute("INSERT INTO medical_records VALUES (1,1,1),(2,1,2),(3,2,3),(4,1,3)");
            s.execute("INSERT INTO prescriptions VALUES "
                    + "(1,1,1,11,'2026-10-12'),(2,1,2,12,'2026-10-12'),"
                    + "(3,2,3,21,'2026-10-12'),(4,1,1,11,'2026-10-11'),"
                    // Each row has a mismatched medical record, doctor, or patient tenant.
                    + "(5,1,3,11,'2026-10-12'),(6,1,1,21,'2026-10-12'),"
                    + "(7,1,4,11,'2026-10-12'),"
                    // The following Monday is the exclusive end of the selected week.
                    + "(8,1,1,11,'2026-10-19')");
            s.execute("INSERT INTO vital_signs VALUES "
                    + "(1,1,1,'NORMAL','2026-10-12 10:00:00+00'),"
                    + "(2,1,1,'EMERGENCIA','2026-10-12 11:00:00+00'),"
                    + "(3,1,2,'EMERGENCIA','2026-10-12 11:00:00+00'),"
                    + "(4,2,3,'EMERGENCIA','2026-10-12 11:00:00+00'),"
                    + "(5,1,4,'EMERGENCIA','2026-10-12 12:00:00+00')");
        }

        DashboardJdbcDao dao = new DashboardJdbcDao(source);
        DashboardResponse admin = dao.read(query(1L, null, DashboardQuery.Role.ADMIN));
        assertThat(admin.appointmentsToday()).isEqualTo(2);
        assertThat(admin.activePatients()).isEqualTo(2);
        assertThat(admin.prescriptionsThisWeek()).isEqualTo(2);
        assertThat(admin.criticalAlerts()).isEqualTo(2);
        assertThat(admin.waitingRoom()).extracting("patientName").containsExactly("Ana Uno");
        assertThat(admin.occupancyByHour()).extracting("hour").containsExactly(0, 10);
        assertThat(admin.clinicalAlerts()).isNull();

        DashboardResponse doctor = dao.read(query(1L, 11L, DashboardQuery.Role.DOCTOR));
        assertThat(doctor.appointmentsToday()).isEqualTo(1);
        assertThat(doctor.activePatients()).isNull();
        assertThat(doctor.prescriptionsThisWeek()).isEqualTo(1);
        assertThat(doctor.criticalAlerts()).isEqualTo(1);
        assertThat(doctor.waitingRoom()).hasSize(1);
        assertThat(doctor.clinicalAlerts()).extracting("patientId").containsExactly(1L);

        DashboardResponse nurse = dao.read(query(1L, null, DashboardQuery.Role.NURSE));
        assertThat(nurse.prescriptionsIssued()).isNull();
        assertThat(nurse.occupancyByHour()).isNull();
        assertThat(nurse.clinicalAlerts()).hasSize(2);

        DashboardResponse receptionist = dao.read(query(1L, null, DashboardQuery.Role.RECEPTIONIST));
        assertThat(receptionist.alerts()).isNull();
        assertThat(receptionist.clinicalAlerts()).isNull();
        assertThat(receptionist.prescriptionsIssued()).isNull();
        String receptionistJson = new ObjectMapper().findAndRegisterModules().writeValueAsString(receptionist);
        assertThat(receptionistJson).doesNotContain("\"alerts\"", "\"clinicalAlerts\"", "\"prescriptionsIssued\"");

        DashboardResponse empty = dao.read(query(99L, null, DashboardQuery.Role.ADMIN));
        assertThat(empty.appointmentsToday()).isZero();
        assertThat(empty.waitingRoom()).isEmpty();
        assertThat(empty.occupancyByHour()).isEmpty();
        assertThat(new ObjectMapper().findAndRegisterModules().writeValueAsString(empty))
                .contains("\"waitingRoom\":[]", "\"occupancyByHour\":[]");
    }

    private DashboardQuery query(Long tenantId, Long doctorId, DashboardQuery.Role role) {
        return new DashboardQuery(tenantId, doctorId, role,
                OffsetDateTime.of(2026, 10, 12, 6, 0, 0, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2026, 10, 13, 6, 0, 0, 0, ZoneOffset.UTC),
                LocalDate.of(2026, 10, 12), LocalDate.of(2026, 10, 19));
    }
}
