package com.sv.grupo7.medisuite.dto.reminder;

import java.time.Instant;

public record AppointmentReminderCandidate(
        Long tenantId,
        Long appointmentId,
        Instant scheduledAt,
        String patientPhone
) {
}