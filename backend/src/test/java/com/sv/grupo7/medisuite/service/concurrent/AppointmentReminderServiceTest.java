package com.sv.grupo7.medisuite.service.concurrent;

import com.sv.grupo7.medisuite.dao.jdbc.ReminderJdbcDao;
import com.sv.grupo7.medisuite.dao.jdbc.ReminderReader;
import com.sv.grupo7.medisuite.dto.reminders.AppointmentReminder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.postgresql.ds.PGSimpleDataSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * B3a — tests del worker concurrente y del DAO JDBC de recordatorios.
 *
 * <p>Los tres primeros tests ejercitan el servicio con un puerto simulado y
 * no necesitan Docker. El resto corren contra PostgreSQL descartable con
 * Testcontainers, ejercitando el UNIQUE compuesto, la idempotencia bajo
 * carrera, el aislamiento por tenant y el cálculo cerca de medianoche local
 * (America/El_Salvador).</p>
 */
@Testcontainers
class AppointmentReminderServiceTest {

    static final ZoneId SV = ZoneId.of("America/El_Salvador");
    /** 2026-10-15 08:00 America/El_Salvador (UTC-6) — día fijo para no depender de hoy. */
    static final Instant FIXED_INSTANT = Instant.parse("2026-10-15T14:00:00Z");
    static final Clock FIXED_CLOCK = Clock.fixed(FIXED_INSTANT, SV);

    // =========================================================================
    // SERVICIO con puerto simulado (no requiere Docker)
    // =========================================================================

    static final class FakeReminderReader implements ReminderReader {
        int cancelCalls;
        int enqueueCalls;
        RuntimeException cancelThrow;
        RuntimeException enqueueThrow;

        @Override
        public int enqueueDueReminders(Clock clock, ZoneId zone) {
            enqueueCalls++;
            if (enqueueThrow != null) {
                throw enqueueThrow;
            }
            return 1;
        }

        @Override
        public int cancelStaleReminders(Clock clock, ZoneId zone) {
            cancelCalls++;
            if (cancelThrow != null) {
                throw cancelThrow;
            }
            return 2;
        }

        @Override
        public List<AppointmentReminder> listPendingByTenant(long tenantId) {
            return List.of();
        }
    }

    @Test
    void service_cycleCallsCancelThenEnqueue() {
        FakeReminderReader port = new FakeReminderReader();
        AppointmentReminderService svc = new AppointmentReminderService(port, FIXED_CLOCK);

        svc.runOnce();

        assertThat(port.cancelCalls).isEqualTo(1);
        assertThat(port.enqueueCalls).isEqualTo(1);
    }

    @Test
    void service_exceptionIsCaught_andNextCycleStillRuns() {
        FakeReminderReader port = new FakeReminderReader();
        port.enqueueThrow = new RuntimeException("boom");
        AppointmentReminderService svc = new AppointmentReminderService(port, FIXED_CLOCK);

        assertThatCode(svc::triggerNow).doesNotThrowAnyException();

        port.enqueueThrow = null;
        assertThatCode(svc::triggerNow).doesNotThrowAnyException();

        assertThat(port.enqueueCalls).isEqualTo(2);
    }

    @Test
    void service_shutdownClosesExecutor() {
        FakeReminderReader port = new FakeReminderReader();
        AppointmentReminderService svc = new AppointmentReminderService(port, FIXED_CLOCK);

        svc.start();
        assertThat(svc.isRunning()).isTrue();

        svc.stop();
        assertThat(svc.isRunning()).isFalse();
    }

    // =========================================================================
    // DAO con PostgreSQL descartable (Testcontainers)
    // =========================================================================

    @Container
    static final PostgreSQLContainer<?> PG = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("medisuite_test")
            .withUsername("test")
            .withPassword("test");

    static DataSource ds;
    static ReminderJdbcDao dao;
    static long tenantAId;
    static long tenantBId;
    static long patientAId;
    static long doctorAId;

    @BeforeAll
    static void initDatabase() throws Exception {
        PGSimpleDataSource pds = new PGSimpleDataSource();
        pds.setUrl(PG.getJdbcUrl());
        pds.setUser(PG.getUsername());
        pds.setPassword(PG.getPassword());
        ds = pds;

        try (Connection c = ds.getConnection(); Statement s = c.createStatement()) {
            s.execute("CREATE TABLE tenants (id BIGSERIAL PRIMARY KEY, name VARCHAR(255))");
            s.execute("CREATE TABLE patients (id BIGSERIAL PRIMARY KEY, tenant_id BIGINT, first_name VARCHAR(100))");
            s.execute("CREATE TABLE doctors (id BIGSERIAL PRIMARY KEY, tenant_id BIGINT, first_name VARCHAR(100))");
            s.execute("CREATE TABLE appointments ("
                    + "id BIGSERIAL PRIMARY KEY, "
                    + "tenant_id BIGINT NOT NULL, "
                    + "patient_id BIGINT NOT NULL, "
                    + "doctor_id BIGINT NOT NULL, "
                    + "scheduled_at TIMESTAMPTZ NOT NULL, "
                    + "status VARCHAR(20) NOT NULL)");
            s.execute("CREATE TABLE appointment_reminders ("
                    + "id BIGSERIAL PRIMARY KEY, "
                    + "tenant_id BIGINT NOT NULL REFERENCES tenants(id), "
                    + "appointment_id BIGINT NOT NULL REFERENCES appointments(id) ON DELETE CASCADE, "
                    + "scheduled_for TIMESTAMPTZ NOT NULL, "
                    + "status VARCHAR(20) NOT NULL DEFAULT 'PENDING', "
                    + "created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), "
                    + "updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), "
                    + "processed_at TIMESTAMPTZ, "
                    + "CONSTRAINT appointment_reminders_status_check CHECK (status IN ('PENDING','CANCELLED')), "
                    + "CONSTRAINT ux_appt_reminder_tenant_appt_when "
                    + "  UNIQUE (tenant_id, appointment_id, scheduled_for))");
            s.execute("CREATE INDEX idx_appt_reminders_pending "
                    + "ON appointment_reminders (tenant_id, status, scheduled_for)");

            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO tenants(name) VALUES (?) RETURNING id")) {
                ps.setString(1, "Clinica A");
                try (ResultSet rs = ps.executeQuery()) { rs.next(); tenantAId = rs.getLong(1); }
            }
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO tenants(name) VALUES (?) RETURNING id")) {
                ps.setString(1, "Clinica B");
                try (ResultSet rs = ps.executeQuery()) { rs.next(); tenantBId = rs.getLong(1); }
            }
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO patients(tenant_id, first_name) VALUES (?,?) RETURNING id")) {
                ps.setLong(1, tenantAId);
                ps.setString(2, "Paciente A");
                try (ResultSet rs = ps.executeQuery()) { rs.next(); patientAId = rs.getLong(1); }
            }
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO doctors(tenant_id, first_name) VALUES (?,?) RETURNING id")) {
                ps.setLong(1, tenantAId);
                ps.setString(2, "Doctor A");
                try (ResultSet rs = ps.executeQuery()) { rs.next(); doctorAId = rs.getLong(1); }
            }
        }

        dao = new ReminderJdbcDao(ds);
    }

    @BeforeEach
    void clearTables() throws SQLException {
        try (Connection c = ds.getConnection(); Statement s = c.createStatement()) {
            s.execute("DELETE FROM appointment_reminders");
            s.execute("DELETE FROM appointments");
        }
    }

    private long insertAppointment(long tenantId, OffsetDateTime when, String status) throws SQLException {
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO appointments(tenant_id, patient_id, doctor_id, scheduled_at, status) "
                             + "VALUES (?,?,?,?,?) RETURNING id")) {
            ps.setLong(1, tenantId);
            ps.setLong(2, patientAId);
            ps.setLong(3, doctorAId);
            ps.setObject(4, when);
            ps.setString(5, status);
            try (ResultSet rs = ps.executeQuery()) { rs.next(); return rs.getLong(1); }
        }
    }

    private int countByStatus(String status) throws SQLException {
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT COUNT(*) FROM appointment_reminders WHERE status = ?")) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) { rs.next(); return rs.getInt(1); }
        }
    }

    @Test
    void dao_enqueue_isIdempotent_twiceProducesOneRow() throws Exception {
        OffsetDateTime appt = OffsetDateTime.ofInstant(FIXED_INSTANT, SV).plusHours(23);
        insertAppointment(tenantAId, appt, "SCHEDULED");

        int first = dao.enqueueDueReminders(FIXED_CLOCK, SV);
        int second = dao.enqueueDueReminders(FIXED_CLOCK, SV);

        assertThat(first).isEqualTo(1);
        assertThat(second).isZero();
        assertThat(countByStatus("PENDING")).isEqualTo(1);
    }

    @Test
    void dao_enqueue_skipsCancelledAppointment() throws Exception {
        OffsetDateTime appt = OffsetDateTime.ofInstant(FIXED_INSTANT, SV).plusHours(23);
        insertAppointment(tenantAId, appt, "CANCELLED");

        int inserted = dao.enqueueDueReminders(FIXED_CLOCK, SV);

        assertThat(inserted).isZero();
        assertThat(countByStatus("PENDING")).isZero();
    }

    @Test
    void dao_cancelStale_cancelsAfterAppointmentCancelled() throws Exception {
        OffsetDateTime appt = OffsetDateTime.ofInstant(FIXED_INSTANT, SV).plusHours(23);
        long apptId = insertAppointment(tenantAId, appt, "SCHEDULED");
        dao.enqueueDueReminders(FIXED_CLOCK, SV);

        try (Connection c = ds.getConnection(); PreparedStatement ps = c.prepareStatement(
                "UPDATE appointments SET status='CANCELLED' WHERE id=?")) {
            ps.setLong(1, apptId);
            ps.executeUpdate();
        }

        int cancelled = dao.cancelStaleReminders(FIXED_CLOCK, SV);

        assertThat(cancelled).isEqualTo(1);
        assertThat(countByStatus("CANCELLED")).isEqualTo(1);
        assertThat(countByStatus("PENDING")).isZero();
    }

    @Test
    void dao_cancelStale_cancelsAfterReschedule_andNewEnqueueCreatesNewRow() throws Exception {
        OffsetDateTime firstTime = OffsetDateTime.ofInstant(FIXED_INSTANT, SV).plusHours(23);
        long apptId = insertAppointment(tenantAId, firstTime, "SCHEDULED");
        dao.enqueueDueReminders(FIXED_CLOCK, SV);

        OffsetDateTime newTime = OffsetDateTime.ofInstant(FIXED_INSTANT, SV).plusHours(22);
        try (Connection c = ds.getConnection(); PreparedStatement ps = c.prepareStatement(
                "UPDATE appointments SET scheduled_at=? WHERE id=?")) {
            ps.setObject(1, newTime);
            ps.setLong(2, apptId);
            ps.executeUpdate();
        }

        int cancelled = dao.cancelStaleReminders(FIXED_CLOCK, SV);
        int inserted = dao.enqueueDueReminders(FIXED_CLOCK, SV);

        assertThat(cancelled).isEqualTo(1);
        assertThat(inserted).isEqualTo(1);
        assertThat(countByStatus("CANCELLED")).isEqualTo(1);
        assertThat(countByStatus("PENDING")).isEqualTo(1);
    }

    @Test
    void dao_isolatesTenants() throws Exception {
        OffsetDateTime appt = OffsetDateTime.ofInstant(FIXED_INSTANT, SV).plusHours(23);
        insertAppointment(tenantAId, appt, "SCHEDULED");
        insertAppointment(tenantBId, appt, "SCHEDULED");

        dao.enqueueDueReminders(FIXED_CLOCK, SV);

        List<AppointmentReminder> a = dao.listPendingByTenant(tenantAId);
        List<AppointmentReminder> b = dao.listPendingByTenant(tenantBId);

        assertThat(a).hasSize(1);
        assertThat(b).hasSize(1);
        assertThat(a.get(0).tenantId()).isEqualTo(tenantAId);
        assertThat(b.get(0).tenantId()).isEqualTo(tenantBId);
        assertThat(a.get(0).id()).isNotEqualTo(b.get(0).id());
    }

    @Test
    void dao_nearMidnightLocal_computesScheduledForCorrectly() throws Exception {
        // 2026-10-15 23:30 America/El_Salvador = 2026-10-16T05:30Z
        Instant nearMidnight = Instant.parse("2026-10-16T05:30:00Z");
        Clock clock = Clock.fixed(nearMidnight, SV);

        OffsetDateTime appt = OffsetDateTime.ofInstant(nearMidnight, SV).plusHours(23);
        insertAppointment(tenantAId, appt, "SCHEDULED");

        int inserted = dao.enqueueDueReminders(clock, SV);
        assertThat(inserted).isEqualTo(1);

        try (Connection c = ds.getConnection(); PreparedStatement ps = c.prepareStatement(
                "SELECT scheduled_for FROM appointment_reminders LIMIT 1");
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            OffsetDateTime scheduledFor = rs.getObject(1, OffsetDateTime.class);
            assertThat(scheduledFor.toInstant()).isEqualTo(appt.minusHours(24).toInstant());
        }
    }

    @Test
    void dao_twoParallelEnqueues_singleRowPerAppointmentHour() throws Exception {
        OffsetDateTime appt = OffsetDateTime.ofInstant(FIXED_INSTANT, SV).plusHours(23);
        insertAppointment(tenantAId, appt, "SCHEDULED");

        CountDownLatch start = new CountDownLatch(1);
        AtomicInteger totalInserted = new AtomicInteger();
        AtomicReference<Throwable> failure = new AtomicReference<>();

        Runnable worker = () -> {
            try {
                start.await();
                totalInserted.addAndGet(dao.enqueueDueReminders(FIXED_CLOCK, SV));
            } catch (Throwable t) {
                failure.compareAndSet(null, t);
            }
        };

        Thread t1 = new Thread(worker, "enqueue-1");
        Thread t2 = new Thread(worker, "enqueue-2");
        t1.start();
        t2.start();
        start.countDown();
        t1.join(TimeUnit.SECONDS.toMillis(30));
        t2.join(TimeUnit.SECONDS.toMillis(30));

        assertThat(failure.get()).isNull();
        assertThat(totalInserted.get()).isEqualTo(1);
        assertThat(countByStatus("PENDING")).isEqualTo(1);
    }
}