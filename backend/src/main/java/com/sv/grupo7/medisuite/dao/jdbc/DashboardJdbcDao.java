package com.sv.grupo7.medisuite.dao.jdbc;

import com.sv.grupo7.medisuite.dto.dashboard.CriticalAlert;
import com.sv.grupo7.medisuite.dto.dashboard.DashboardQuery;
import com.sv.grupo7.medisuite.dto.dashboard.DashboardResponse;
import com.sv.grupo7.medisuite.dto.dashboard.OccupancySlot;
import com.sv.grupo7.medisuite.dto.dashboard.WaitingRoomEntry;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/** PostgreSQL read model for the authenticated dashboard. Every query is tenant-scoped. */
@Repository
public class DashboardJdbcDao implements DashboardMetricsReader {

    private static final String APPOINTMENT_TENANT_JOINS =
            " JOIN patients p ON p.id = a.patient_id AND p.tenant_id = a.tenant_id "
            + "JOIN doctors d ON d.id = a.doctor_id AND d.tenant_id = a.tenant_id ";

    private final DataSource dataSource;

    @Autowired
    public DashboardJdbcDao(@Qualifier("jdbcDataSource") ObjectProvider<DataSource> dedicated,
                            DataSource primary) {
        this(dedicated.getIfAvailable(() -> primary));
    }

    /** Direct constructor used by the disposable-PostgreSQL integration test. */
    public DashboardJdbcDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public DashboardResponse read(DashboardQuery q) {
        try (Connection connection = dataSource.getConnection()) {
            long appointments = countAppointments(connection, q);
            Long patients = q.role() == DashboardQuery.Role.DOCTOR ? null : countPatients(connection, q);
            Long prescriptions = q.role() == DashboardQuery.Role.ADMIN || q.role() == DashboardQuery.Role.DOCTOR
                    ? countPrescriptions(connection, q) : null;
            Long alerts = q.role() == DashboardQuery.Role.RECEPTIONIST ? null : countCriticalAlerts(connection, q);
            List<WaitingRoomEntry> waiting = findWaitingRoom(connection, q);
            List<OccupancySlot> occupancy = q.role() == DashboardQuery.Role.NURSE
                    ? null : findOccupancy(connection, q);
            List<CriticalAlert> clinical = q.role() == DashboardQuery.Role.DOCTOR || q.role() == DashboardQuery.Role.NURSE
                    ? findCriticalAlerts(connection, q) : null;

            // The first four keys preserve the current frontend contract until B6 is integrated.
            // activePatients means registered patients: patients has no active flag.
            return new DashboardResponse(appointments, patients, prescriptions, alerts,
                    prescriptions, alerts, waiting, occupancy, clinical);
        } catch (SQLException ex) {
            throw new DataAccessResourceFailureException("No se pudieron consultar las métricas", ex);
        }
    }

    private long countAppointments(Connection c, DashboardQuery q) throws SQLException {
        String sql = "SELECT COUNT(*) FROM appointments a" + APPOINTMENT_TENANT_JOINS + "WHERE a.tenant_id = ? "
                + "AND a.scheduled_at >= ? AND a.scheduled_at < ?" + doctorFilter(q, "a");
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, q.tenantId());
            ps.setObject(2, q.dayStart());
            ps.setObject(3, q.dayEnd());
            bindDoctor(ps, 4, q);
            return count(ps);
        }
    }

    private long countPatients(Connection c, DashboardQuery q) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM patients WHERE tenant_id = ?")) {
            ps.setLong(1, q.tenantId());
            return count(ps);
        }
    }

    private long countPrescriptions(Connection c, DashboardQuery q) throws SQLException {
        String sql = "SELECT COUNT(*) FROM prescriptions rx "
                + "JOIN medical_records mr ON mr.id = rx.medical_record_id AND mr.tenant_id = rx.tenant_id "
                + "JOIN patients p ON p.id = mr.patient_id AND p.tenant_id = mr.tenant_id "
                + "JOIN doctors d ON d.id = rx.doctor_id AND d.tenant_id = rx.tenant_id "
                + "WHERE rx.tenant_id = ? AND rx.issued_on >= ? AND rx.issued_on < ?"
                + doctorFilter(q, "rx");
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, q.tenantId());
            ps.setObject(2, q.weekStart());
            ps.setObject(3, q.weekEnd());
            bindDoctor(ps, 4, q);
            return count(ps);
        }
    }

    private String latestVitalSignsSql(DashboardQuery q) {
        return "WITH latest AS (SELECT vs.id, mr.patient_id, vs.priority, vs.recorded_at, "
                + "ROW_NUMBER() OVER (PARTITION BY vs.medical_record_id "
                + "ORDER BY vs.recorded_at DESC, vs.id DESC) AS rn "
                + "FROM vital_signs vs JOIN medical_records mr ON mr.id = vs.medical_record_id "
                + "AND mr.tenant_id = vs.tenant_id "
                + "JOIN patients p ON p.id = mr.patient_id AND p.tenant_id = mr.tenant_id "
                + "WHERE vs.tenant_id = ? AND mr.tenant_id = ?"
                + (q.role() == DashboardQuery.Role.DOCTOR
                    ? " AND EXISTS (SELECT 1 FROM appointments a WHERE a.tenant_id = ? "
                      + "AND a.patient_id = mr.patient_id AND a.doctor_id = ?)" : "")
                + ") ";
    }

    private void bindVitalScope(PreparedStatement ps, DashboardQuery q) throws SQLException {
        ps.setLong(1, q.tenantId());
        ps.setLong(2, q.tenantId());
        if (q.role() == DashboardQuery.Role.DOCTOR) {
            ps.setLong(3, q.tenantId());
            ps.setLong(4, q.doctorId());
        }
    }

    private long countCriticalAlerts(Connection c, DashboardQuery q) throws SQLException {
        String sql = latestVitalSignsSql(q) + "SELECT COUNT(*) FROM latest WHERE rn = 1 AND priority = 'EMERGENCIA'";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            bindVitalScope(ps, q);
            return count(ps);
        }
    }

    private List<CriticalAlert> findCriticalAlerts(Connection c, DashboardQuery q) throws SQLException {
        String sql = latestVitalSignsSql(q) + "SELECT id, patient_id, priority, recorded_at FROM latest "
                + "WHERE rn = 1 AND priority = 'EMERGENCIA' ORDER BY recorded_at DESC, id DESC LIMIT 20";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            bindVitalScope(ps, q);
            try (ResultSet rs = ps.executeQuery()) {
                List<CriticalAlert> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(new CriticalAlert(rs.getLong("id"), rs.getLong("patient_id"),
                            rs.getString("priority"), rs.getObject("recorded_at", OffsetDateTime.class)));
                }
                return result;
            }
        }
    }

    private List<WaitingRoomEntry> findWaitingRoom(Connection c, DashboardQuery q) throws SQLException {
        String sql = "SELECT a.id, p.first_name, p.last_name, a.scheduled_at, a.status "
                + "FROM appointments a" + APPOINTMENT_TENANT_JOINS
                + "WHERE a.tenant_id = ? AND a.scheduled_at >= ? AND a.scheduled_at < ? "
                + "AND a.status IN ('WAITING', 'IN_WAITING')" + doctorFilter(q, "a")
                + " ORDER BY a.scheduled_at, a.id LIMIT 20";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, q.tenantId());
            ps.setObject(2, q.dayStart());
            ps.setObject(3, q.dayEnd());
            bindDoctor(ps, 4, q);
            try (ResultSet rs = ps.executeQuery()) {
                List<WaitingRoomEntry> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(new WaitingRoomEntry(rs.getLong("id"),
                            rs.getString("first_name") + " " + rs.getString("last_name"),
                            rs.getObject("scheduled_at", OffsetDateTime.class), rs.getString("status")));
                }
                return result;
            }
        }
    }

    private List<OccupancySlot> findOccupancy(Connection c, DashboardQuery q) throws SQLException {
        String sql = "SELECT EXTRACT(HOUR FROM a.scheduled_at AT TIME ZONE 'America/El_Salvador')::int AS hour, "
                + "COUNT(*) AS appointments FROM appointments a" + APPOINTMENT_TENANT_JOINS
                + "WHERE a.tenant_id = ? "
                + "AND a.scheduled_at >= ? AND a.scheduled_at < ? "
                + "AND a.status NOT IN ('CANCELLED', 'NO_SHOW')" + doctorFilter(q, "a")
                + " GROUP BY hour ORDER BY hour";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, q.tenantId());
            ps.setObject(2, q.dayStart());
            ps.setObject(3, q.dayEnd());
            bindDoctor(ps, 4, q);
            try (ResultSet rs = ps.executeQuery()) {
                List<OccupancySlot> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(new OccupancySlot(rs.getInt("hour"), rs.getLong("appointments")));
                }
                return result;
            }
        }
    }

    private String doctorFilter(DashboardQuery q, String alias) {
        return q.role() == DashboardQuery.Role.DOCTOR ? " AND " + alias + ".doctor_id = ?" : "";
    }

    private void bindDoctor(PreparedStatement ps, int parameter, DashboardQuery q) throws SQLException {
        if (q.role() == DashboardQuery.Role.DOCTOR) {
            ps.setLong(parameter, q.doctorId());
        }
    }

    private long count(PreparedStatement ps) throws SQLException {
        try (ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getLong(1);
        }
    }
}
