package com.sv.grupo7.medisuite.dao.jdbc;

import com.sv.grupo7.medisuite.dto.reminders.AppointmentReminder;
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
import java.sql.Types;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO JDBC de la cola persistida de recordatorios.
 *
 * <p>Contrato local B3a. Cuando B4 integre los puertos definitivos, esta clase
 * pasará a implementar {@code service.ports.ReminderPort} con las firmas
 * acordadas (enqueue / cancelStale / listPending).</p>
 *
 * <p>El worker no hereda TenantContext: cada fila lleva su {@code tenant_id}
 * explícito y cada consulta lo respeta. Todas las operaciones usan
 * PreparedStatement y try-with-resources. El UNIQUE
 * {@code (tenant_id, appointment_id, scheduled_for)} + ON CONFLICT DO NOTHING
 * garantizan idempotencia entre instancias.</p>
 */
@Repository
public class ReminderJdbcDao implements ReminderReader {

    /** Zona operativa; se documenta explícitamente en la V11. */
    public static final ZoneId DEFAULT_ZONE = ZoneId.of("America/El_Salvador");

    /** Ventana de antelación del recordatorio. */
    public static final int REMINDER_LEAD_HOURS = 24;

    private static final String SQL_ENQUEUE =
            "INSERT INTO appointment_reminders (tenant_id, appointment_id, scheduled_for, status) "
            + "SELECT a.tenant_id, a.id, a.scheduled_at - INTERVAL '"
            + REMINDER_LEAD_HOURS + " hours', 'PENDING' "
            + "FROM appointments a "
            + "WHERE a.status NOT IN ('CANCELLED', 'COMPLETED', 'NO_SHOW') "
            + "  AND a.scheduled_at > ? "
            + "  AND a.scheduled_at <= ? "
            + "ON CONFLICT (tenant_id, appointment_id, scheduled_for) DO NOTHING";

    private static final String SQL_CANCEL_STALE =
            "UPDATE appointment_reminders r "
            + "SET status = 'CANCELLED', updated_at = ?, "
            + "    processed_at = COALESCE(processed_at, ?) "
            + "WHERE r.status = 'PENDING' "
            + "  AND NOT EXISTS ( "
            + "      SELECT 1 FROM appointments a "
            + "      WHERE a.id = r.appointment_id "
            + "        AND a.tenant_id = r.tenant_id "
            + "        AND a.status NOT IN ('CANCELLED', 'COMPLETED', 'NO_SHOW') "
            + "        AND a.scheduled_at - INTERVAL '" + REMINDER_LEAD_HOURS + " hours' = r.scheduled_for "
            + "  )";

    private static final String SQL_LIST_PENDING =
            "SELECT id, tenant_id, appointment_id, scheduled_for, status "
            + "FROM appointment_reminders "
            + "WHERE tenant_id = ? AND status = 'PENDING' "
            + "ORDER BY scheduled_for";

    private final DataSource dataSource;

    @Autowired
    public ReminderJdbcDao(@Qualifier("jdbcDataSource") ObjectProvider<DataSource> dedicated,
                           DataSource primary) {
        this(dedicated.getIfAvailable(() -> primary));
    }

    /** Constructor directo usado por el test con PostgreSQL descartable. */
    public ReminderJdbcDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public int enqueueDueReminders(Clock clock, ZoneId zone) {
        OffsetDateTime now = OffsetDateTime.ofInstant(clock.instant(), zone);
        OffsetDateTime horizon = now.plusHours(REMINDER_LEAD_HOURS);
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(SQL_ENQUEUE)) {
            ps.setObject(1, now, Types.TIMESTAMP_WITH_TIMEZONE);
            ps.setObject(2, horizon, Types.TIMESTAMP_WITH_TIMEZONE);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessResourceFailureException("No se pudieron encolar recordatorios", e);
        }
    }

    @Override
    public int cancelStaleReminders(Clock clock, ZoneId zone) {
        OffsetDateTime now = OffsetDateTime.ofInstant(clock.instant(), zone);
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(SQL_CANCEL_STALE)) {
            ps.setObject(1, now, Types.TIMESTAMP_WITH_TIMEZONE);
            ps.setObject(2, now, Types.TIMESTAMP_WITH_TIMEZONE);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessResourceFailureException("No se pudieron cancelar recordatorios obsoletos", e);
        }
    }

    @Override
    public List<AppointmentReminder> listPendingByTenant(long tenantId) {
        List<AppointmentReminder> result = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(SQL_LIST_PENDING)) {
            ps.setLong(1, tenantId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new AppointmentReminder(
                            rs.getLong("id"),
                            rs.getLong("tenant_id"),
                            rs.getLong("appointment_id"),
                            rs.getObject("scheduled_for", OffsetDateTime.class),
                            rs.getString("status")));
                }
            }
            return result;
        } catch (SQLException e) {
            throw new DataAccessResourceFailureException("No se pudo leer la cola de recordatorios", e);
        }
    }
}