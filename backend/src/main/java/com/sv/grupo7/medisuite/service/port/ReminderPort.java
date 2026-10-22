package com.sv.grupo7.medisuite.service.port;

import com.sv.grupo7.medisuite.dto.reminders.AppointmentReminder;

import java.time.Clock;
import java.time.ZoneId;
import java.util.List;

/**
 * Contrato de la cola de recordatorios (B3a).
 *
 * <p>El worker {@code AppointmentReminderService} depende de esta interfaz y no
 * de la implementación JDBC; en las pruebas se sustituye por un doble en memoria.</p>
 */
public interface ReminderPort {

    /** Candidatos activos a los que corresponde insertar recordatorio ahora. */
    int enqueueDueReminders(Clock clock, ZoneId zone);

    /** Marca como CANCELLED los recordatorios cuya cita ya no es válida. */
    int cancelStaleReminders(Clock clock, ZoneId zone);

    /** Cola PENDING de un tenant, para evidencia o pantalla administrativa. */
    List<AppointmentReminder> listPendingByTenant(long tenantId);
}
