package com.sv.grupo7.medisuite.dto.dashboard;

import java.time.OffsetDateTime;

public record WaitingRoomEntry(Long appointmentId, String patientName,
                               OffsetDateTime scheduledAt, String status) {}
