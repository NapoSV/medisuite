package com.sv.grupo7.medisuite.dao.jdbc;

import com.sv.grupo7.medisuite.dto.reminders.AppointmentReminder;

import java.time.Clock;
import java.time.ZoneId;
import java.util.List;

/**
 * Contrato local del DAO de recordatorios B3a.
 *
 * <p>Cuando B4 integre el DAO abstracto y los puertos definitivos, este
 * contrato se sustituye por {@code service.ports.ReminderPort}. Firmas
 * acordadas: enqueue, cancelStale, listPending.</p>
 */
public interface ReminderReader {

    /** Candidatos activos a los que corresponde insertar recordatorio ahora. */
    int enqueueDueReminders(Clock clock, ZoneId zone);

    /** Marca como CANCELLED los recordatorios cuya cita ya no es válida. */
    int cancelStaleReminders(Clock clock, ZoneId zone);

    /** Cola PENDING de un tenant, para evidencia o pantalla administrativa. */
    List<AppointmentReminder> listPendingByTenant(long tenantId);
}