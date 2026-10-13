package com.sv.grupo7.medisuite.service.port;

import com.sv.grupo7.medisuite.dto.reminder.AppointmentReminderCandidate;
import java.time.Instant;
import java.util.List;

public interface ReminderPort {
    List<AppointmentReminderCandidate> findCandidates(Long tenantId, Instant windowStart, Instant windowEnd, int limit);
    int insertIfAbsent(Long tenantId, Long appointmentId, Instant scheduledAt);
}