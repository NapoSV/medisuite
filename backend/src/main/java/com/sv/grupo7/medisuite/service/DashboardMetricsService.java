package com.sv.grupo7.medisuite.service;

import com.sv.grupo7.medisuite.dao.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class DashboardMetricsService {

    private final AppointmentRepository appointments;
    private final PatientRepository patients;
    private final PrescriptionRepository prescriptions;
    private final AuditLogRepository audit;

    public Map<String, Long> getMetrics(Long tenantId) {

        OffsetDateTime dayStart = OffsetDateTime.now(ZoneOffset.UTC).toLocalDate().atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime dayEnd = dayStart.plusDays(1);

        var f1 = CompletableFuture.supplyAsync(
                () -> appointments.countByDateAndTenant(tenantId, dayStart, dayEnd));

        var f2 = CompletableFuture.supplyAsync(
                () -> patients.countActiveByTenant(tenantId));

        var f3 = CompletableFuture.supplyAsync(
                () -> prescriptions.count());

        var f4 = CompletableFuture.supplyAsync(
                () -> audit.count());

        CompletableFuture.allOf(f1, f2, f3, f4).join();

        return Map.of(
                "citasHoy", f1.join(),
                "pacientesActivos", f2.join(),
                "recetasEmitidas", f3.join(),
                "alertas", f4.join()
        );
    }
}