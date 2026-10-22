package com.sv.grupo7.medisuite.dto.dashboard;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Objects;

/** Internal, server-derived scope. Never bind this record from an HTTP request. */
public record DashboardQuery(
        Long tenantId,
        Long doctorId,
        Role role,
        OffsetDateTime dayStart,
        OffsetDateTime dayEnd,
        LocalDate weekStart,
        LocalDate weekEnd
) {
    public enum Role { ADMIN, DOCTOR, NURSE, RECEPTIONIST }

    public DashboardQuery {
        Objects.requireNonNull(tenantId);
        Objects.requireNonNull(role);
        Objects.requireNonNull(dayStart);
        Objects.requireNonNull(dayEnd);
        Objects.requireNonNull(weekStart);
        Objects.requireNonNull(weekEnd);
        if (role == Role.DOCTOR && doctorId == null) {
            throw new IllegalArgumentException("El médico debe tener doctorId");
        }
    }
}
