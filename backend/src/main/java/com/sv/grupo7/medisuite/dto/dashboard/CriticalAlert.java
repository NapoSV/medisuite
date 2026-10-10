package com.sv.grupo7.medisuite.dto.dashboard;

import java.time.OffsetDateTime;

public record CriticalAlert(Long vitalSignId, Long patientId,
                            String priority, OffsetDateTime recordedAt) {}
