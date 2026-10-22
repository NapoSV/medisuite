package com.sv.grupo7.medisuite.dao.jdbc;

import com.sv.grupo7.medisuite.dto.reminders.AppointmentReminder;
import com.sv.grupo7.medisuite.service.port.ReminderPort;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

/**
 * DAO JDBC de la cola persistida de recordatorios (B3a).
 *
 * <p>Implementa {@link ReminderPort} y reutiliza las operaciones de
 * {@link BaseJdbcDao}: PreparedStatement, try-with-resources y traducción de
 * {@code SQLException} a {@code DataAccessException} con su {@code JdbcErrorCode}.</p>
 *
 * <p>El worker no hereda TenantContext: cada fila lleva su {@code tenant_id}
 * explícito y cada consulta lo respeta. El UNIQUE
 * {@code (tenant_id, appointment_id, scheduled_for)} + ON CONFLICT DO NOTHING
 * garantizan idempotencia entre hilos e instancias.</p>
 */
@Repository
public class ReminderJdbcDao extends BaseJdbcDao implements ReminderPort {

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

    @Autowired
    public ReminderJdbcDao(@Qualifier("jdbcDataSource") ObjectProvider<DataSource> dedicated,
                           DataSource primary) {
        super(dedicated.getIfAvailable(() -> primary));
    }

    /** Constructor directo usado por el test con PostgreSQL descartable. */
    public ReminderJdbcDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public int enqueueDueReminders(Clock clock, ZoneId zone) {
        OffsetDateTime now = OffsetDateTime.ofInstant(clock.instant(), zone);
        return update(SQL_ENQUEUE, now, now.plusHours(REMINDER_LEAD_HOURS));
    }

    @Override
    public int cancelStaleReminders(Clock clock, ZoneId zone) {
        OffsetDateTime now = OffsetDateTime.ofInstant(clock.instant(), zone);
        return update(SQL_CANCEL_STALE, now, now);
    }

    @Override
    public List<AppointmentReminder> listPendingByTenant(long tenantId) {
        return query(SQL_LIST_PENDING, rs -> new AppointmentReminder(
                rs.getLong("id"),
                rs.getLong("tenant_id"),
                rs.getLong("appointment_id"),
                rs.getObject("scheduled_for", OffsetDateTime.class),
                rs.getString("status")), tenantId);
    }
}
