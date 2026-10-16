package com.sv.grupo7.medisuite.service.concurrent;

import com.sv.grupo7.medisuite.dao.AppointmentRepository;
import com.sv.grupo7.medisuite.dao.DoctorRepository;
import com.sv.grupo7.medisuite.dao.PatientRepository;
import com.sv.grupo7.medisuite.dao.jdbc.ReservationCodeJdbcDao;
import com.sv.grupo7.medisuite.exception.DataAccessException;
import com.sv.grupo7.medisuite.exception.JdbcErrorCode;
import com.sv.grupo7.medisuite.model.medical.Appointment;
import com.sv.grupo7.medisuite.model.medical.Doctor;
import com.sv.grupo7.medisuite.model.medical.Patient;
import com.sv.grupo7.medisuite.model.tenant.Tenant;
import com.sv.grupo7.medisuite.security.TenantContext;
import com.sv.grupo7.medisuite.service.AppointmentService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.postgresql.ds.PGSimpleDataSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * B3b — códigos de reserva únicos bajo concurrencia.
 *
 * <p>Un contenedor PostgreSQL descartable por suite, con el esquema base y la
 * migración V12 real. Cada test limpia {@code appointments} antes de correr,
 * así que ninguna inserción queda permanente.</p>
 */
@Testcontainers
class ReservationCodeGeneratorTest {

    private static final Long TENANT_ID = 1L;
    private static final String SEQUENCE_STATE_SQL =
            "SELECT last_value, is_called FROM reservation_code_seq";

    @Container
    static final PostgreSQLContainer<?> PG = new PostgreSQLContainer<>("postgres:16-alpine")
            .withInitScript("schema.sql");

    static DataSource ds;
    static ReservationCodeGenerator generator;
    static long tenantId;
    static long otherTenantId;
    static long patientId;
    static long doctorId;

    static final AtomicInteger SLOT = new AtomicInteger();

    @BeforeAll
    static void initDatabase() throws Exception {
        ds = newDataSource();
        generator = new ReservationCodeGenerator(new ReservationCodeJdbcDao(ds));

        try (Connection c = ds.getConnection(); Statement s = c.createStatement()) {
            s.execute(readMigration());
            s.execute("INSERT INTO tenants (slug, commercial_name) VALUES ('t1','T1'), ('t2','T2')");
            tenantId = queryLong(s, "SELECT id FROM tenants WHERE slug = 't1'");
            otherTenantId = queryLong(s, "SELECT id FROM tenants WHERE slug = 't2'");
            s.execute("INSERT INTO users (tenant_id, first_name, last_name, cif, email, password_hash, role) "
                    + "VALUES (" + tenantId + ", 'Doc', 'Test', '1', 'doc@test.local', 'x', 'DOCTOR')");
            s.execute("INSERT INTO specialties (tenant_id, name) VALUES (" + tenantId + ", 'General')");
            s.execute("INSERT INTO doctors (tenant_id, user_id, specialty_id, license_number) VALUES ("
                    + tenantId + ", (SELECT id FROM users WHERE email = 'doc@test.local'), "
                    + "(SELECT id FROM specialties WHERE name = 'General'), 'LIC-1')");
            s.execute("INSERT INTO patients (tenant_id, birth_date, first_name, last_name, dui) VALUES ("
                    + tenantId + ", '1990-01-01', 'Pac', 'Test', '00000001')");
            doctorId = queryLong(s, "SELECT id FROM doctors WHERE license_number = 'LIC-1'");
            patientId = queryLong(s, "SELECT id FROM patients WHERE dui = '00000001'");
        }
    }

    @BeforeEach
    void clearAppointments() throws SQLException {
        try (Connection c = ds.getConnection(); Statement s = c.createStatement()) {
            s.execute("DELETE FROM appointments");
        }
    }

    // -------------------------------------------------------------------------
    // Generador contra la secuencia real
    // -------------------------------------------------------------------------

    @Test
    void next_formatoRMasOchoDigitosConLongitudDiez() {
        String code = generator.next();

        assertThat(code).matches("R-\\d{8}");
        assertThat(code).hasSize(10);
    }

    @Test
    void next_cienTareasConcurrentesDanCienCodigosDistintos() {
        List<String> codes = generateConcurrently(100);

        assertThat(codes).hasSize(100);
        assertThat(new HashSet<>(codes)).hasSize(100);
        assertThat(codes).allMatch(code -> code.length() <= 10);
    }

    @Test
    void next_rondasRepetidasNoGeneranColisiones() {
        Set<String> acumulados = new HashSet<>();
        for (int ronda = 0; ronda < 3; ronda++) {
            acumulados.addAll(generateConcurrently(100));
        }

        assertThat(acumulados).hasSize(300);
    }

    @Test
    void next_codigosGeneradosSePersistenConElConstraintReal() throws Exception {
        List<String> codes = generateConcurrently(100);

        for (String code : codes) {
            insertAppointment(tenantId, code);
        }

        try (Connection c = ds.getConnection(); Statement s = c.createStatement()) {
            assertThat(queryLong(s, "SELECT COUNT(DISTINCT reservation_code) FROM appointments"))
                    .isEqualTo(100L);
        }
    }

    @Test
    void constraint_rechazaCodigoDuplicadoEnElMismoTenant() throws Exception {
        insertAppointment(tenantId, "R-00000001");

        assertThatThrownBy(() -> insertAppointment(tenantId, "R-00000001"))
                .isInstanceOfSatisfying(SQLException.class, e -> {
                    assertThat(e.getSQLState()).isEqualTo("23505");
                    assertThat(JdbcErrorCode.from(e)).isEqualTo(JdbcErrorCode.UNIQUE_VIOLATION);
                });
    }

    @Test
    void constraint_permiteElMismoCodigoEnOtroTenant() throws Exception {
        insertAppointment(tenantId, "R-00000001");

        insertAppointment(otherTenantId, "R-00000001");
    }

    @Test
    void next_unaNuevaInstanciaDelDaoNoReutilizaValores() throws Exception {
        String antes = generator.next();

        ReservationCodeGenerator reiniciado =
                new ReservationCodeGenerator(new ReservationCodeJdbcDao(newDataSource()));
        String despues = reiniciado.next();

        assertThat(numeric(despues)).isGreaterThan(numeric(antes));
    }

    @Test
    void next_limiteDeOchoDigitosFallaDeFormaExplicitaEnLaSecuenciaReal() throws Exception {
        long lastValue;
        boolean isCalled;
        try (Connection c = ds.getConnection(); Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(SEQUENCE_STATE_SQL)) {
            rs.next();
            lastValue = rs.getLong(1);
            isCalled = rs.getBoolean(2);
        }

        try (Connection c = ds.getConnection(); Statement s = c.createStatement()) {
            s.execute("SELECT setval('reservation_code_seq', 99999998)");

            assertThat(generator.next()).isEqualTo("R-99999999");
            assertThatThrownBy(generator::next).isInstanceOf(DataAccessException.class);
        } finally {
            try (Connection c = ds.getConnection(); Statement s = c.createStatement()) {
                s.execute("SELECT setval('reservation_code_seq', " + lastValue + ", " + isCalled + ")");
            }
        }
    }

    @Test
    void citasHistoricasCodConservanSuCodigoYSiguenConsultables() throws Exception {
        insertAppointment(tenantId, "COD-0042");
        insertAppointment(tenantId, generator.next());

        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT status FROM appointments WHERE tenant_id = ? AND reservation_code = ?")) {
            ps.setLong(1, tenantId);
            ps.setString(2, "COD-0042");
            try (ResultSet rs = ps.executeQuery()) {
                assertThat(rs.next()).isTrue();
                assertThat(rs.getString(1)).isEqualTo("PENDING");
            }
        }
    }

    // -------------------------------------------------------------------------
    // Sin Docker: límite del formato y fallos de BD
    // -------------------------------------------------------------------------

    @Test
    void next_valorFueraDelFormatoLanzaErrorExplicitoSinTruncar() {
        ReservationCodeJdbcDao dao = mock(ReservationCodeJdbcDao.class);
        when(dao.nextValue()).thenReturn(100_000_000L);

        assertThatThrownBy(() -> new ReservationCodeGenerator(dao).next())
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void dao_falloDeConexionSePropagaComoDataAccessExceptionSinCodigo() throws Exception {
        DataSource caida = mock(DataSource.class);
        when(caida.getConnection()).thenThrow(new SQLException("sin conexion", "08006"));

        assertThatThrownBy(() -> new ReservationCodeGenerator(new ReservationCodeJdbcDao(caida)).next())
                .isInstanceOfSatisfying(DataAccessException.class,
                        e -> assertThat(e.getCode()).isEqualTo(JdbcErrorCode.CONNECTION_LOST));
    }

    // -------------------------------------------------------------------------
    // AppointmentService con el generador simulado
    // -------------------------------------------------------------------------

    @Test
    void servicio_mantieneFechaStatusTenantYPersisteElCodigo() {
        AppointmentRepository appointmentRepo = mock(AppointmentRepository.class);
        PatientRepository patientRepo = mock(PatientRepository.class);
        DoctorRepository doctorRepo = mock(DoctorRepository.class);
        ReservationCodeGenerator codeGenerator = mock(ReservationCodeGenerator.class);

        Tenant tenant = new Tenant();
        Doctor doctor = new Doctor();
        doctor.setTenant(tenant);
        Patient patient = new Patient();
        OffsetDateTime fecha = OffsetDateTime.now().plusDays(2);

        when(doctorRepo.findByIdAndTenantId(1L, TENANT_ID)).thenReturn(Optional.of(doctor));
        when(patientRepo.findByIdAndTenantId(1L, TENANT_ID)).thenReturn(Optional.of(patient));
        when(appointmentRepo.existsByDoctorIdAndScheduledAtAndStatusNot(1L, fecha, "CANCELLED"))
                .thenReturn(false);
        when(appointmentRepo.save(any(Appointment.class))).thenAnswer(i -> i.getArgument(0));
        when(codeGenerator.next()).thenReturn("R-00000001");

        AppointmentService service =
                new AppointmentService(appointmentRepo, patientRepo, doctorRepo, codeGenerator);

        try (MockedStatic<TenantContext> ctx = mockStatic(TenantContext.class)) {
            ctx.when(TenantContext::currentTenantId).thenReturn(TENANT_ID);

            service.create(1L, 1L, fecha, "Consulta general");
        }

        ArgumentCaptor<Appointment> captor = ArgumentCaptor.forClass(Appointment.class);
        verify(appointmentRepo).save(captor.capture());
        Appointment guardada = captor.getValue();
        assertThat(guardada.getReservationCode()).isEqualTo("R-00000001");
        assertThat(guardada.getStatus()).isEqualTo("PENDING");
        assertThat(guardada.getScheduledAt()).isEqualTo(fecha);
        assertThat(guardada.getTenant()).isSameAs(tenant);
    }

    @Test
    void servicio_siFallaElGeneradorNoSeGuardaNingunaCita() {
        AppointmentRepository appointmentRepo = mock(AppointmentRepository.class);
        PatientRepository patientRepo = mock(PatientRepository.class);
        DoctorRepository doctorRepo = mock(DoctorRepository.class);
        ReservationCodeGenerator codeGenerator = mock(ReservationCodeGenerator.class);

        Doctor doctor = new Doctor();
        doctor.setTenant(new Tenant());
        OffsetDateTime fecha = OffsetDateTime.now().plusDays(2);

        when(doctorRepo.findByIdAndTenantId(1L, TENANT_ID)).thenReturn(Optional.of(doctor));
        when(patientRepo.findByIdAndTenantId(1L, TENANT_ID)).thenReturn(Optional.of(new Patient()));
        when(codeGenerator.next())
                .thenThrow(new DataAccessException("siguiente código de reserva",
                        JdbcErrorCode.CONNECTION_LOST, null));

        AppointmentService service =
                new AppointmentService(appointmentRepo, patientRepo, doctorRepo, codeGenerator);

        try (MockedStatic<TenantContext> ctx = mockStatic(TenantContext.class)) {
            ctx.when(TenantContext::currentTenantId).thenReturn(TENANT_ID);

            assertThatThrownBy(() -> service.create(1L, 1L, fecha, "Consulta general"))
                    .isInstanceOf(DataAccessException.class);
        }

        verify(appointmentRepo, never()).save(any(Appointment.class));
    }

    // -------------------------------------------------------------------------
    // Utilidades
    // -------------------------------------------------------------------------

    private static List<String> generateConcurrently(int tasks) {
        ExecutorService pool = Executors.newFixedThreadPool(4);
        try {
            List<CompletableFuture<String>> futures = new ArrayList<>();
            for (int i = 0; i < tasks; i++) {
                futures.add(CompletableFuture.supplyAsync(generator::next, pool));
            }
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            return futures.stream().map(CompletableFuture::join).toList();
        } finally {
            pool.shutdown();
        }
    }

    private static void insertAppointment(long tenant, String code) throws SQLException {
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO appointments (tenant_id, patient_id, doctor_id, scheduled_at, status, reservation_code) "
                             + "VALUES (?, ?, ?, now() + (? * interval '1 minute'), 'PENDING', ?)")) {
            ps.setLong(1, tenant);
            ps.setLong(2, patientId);
            ps.setLong(3, doctorId);
            ps.setInt(4, SLOT.incrementAndGet());
            ps.setString(5, code);
            ps.executeUpdate();
        }
    }

    private static long numeric(String code) {
        return Long.parseLong(code.substring(2));
    }

    private static long queryLong(Statement s, String sql) throws SQLException {
        try (ResultSet rs = s.executeQuery(sql)) {
            rs.next();
            return rs.getLong(1);
        }
    }

    private static DataSource newDataSource() {
        PGSimpleDataSource pds = new PGSimpleDataSource();
        pds.setUrl(PG.getJdbcUrl());
        pds.setUser(PG.getUsername());
        pds.setPassword(PG.getPassword());
        return pds;
    }

    private static String readMigration() throws Exception {
        try (InputStream in = ReservationCodeGeneratorTest.class.getClassLoader()
                .getResourceAsStream("db/migration/V12__reservation_codes.sql")) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
