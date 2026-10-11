package com.sv.grupo7.medisuite.dto.reminders;

import java.time.OffsetDateTime;

public record AppointmentReminder(
        long id,
        long tenantId,
        long appointmentId,
        OffsetDateTime scheduledFor,
        String status
) {
}