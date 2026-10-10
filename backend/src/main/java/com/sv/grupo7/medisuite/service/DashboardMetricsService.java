package com.sv.grupo7.medisuite.service;

import com.sv.grupo7.medisuite.dao.DoctorRepository;
import com.sv.grupo7.medisuite.dao.UserRepository;
import com.sv.grupo7.medisuite.dao.jdbc.DashboardMetricsReader;
import com.sv.grupo7.medisuite.dto.dashboard.DashboardQuery;
import com.sv.grupo7.medisuite.dto.dashboard.DashboardResponse;
import com.sv.grupo7.medisuite.model.medical.Doctor;
import com.sv.grupo7.medisuite.model.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAdjusters;

@Service
public class DashboardMetricsService {

    private static final ZoneId CLINIC_ZONE = ZoneId.of("America/El_Salvador");

    private final DashboardMetricsReader metrics;
    private final UserRepository users;
    private final DoctorRepository doctors;
    private final Clock clock;

    @Autowired
    public DashboardMetricsService(DashboardMetricsReader metrics, UserRepository users, DoctorRepository doctors) {
        this(metrics, users, doctors, Clock.systemUTC());
    }

    DashboardMetricsService(DashboardMetricsReader metrics, UserRepository users, DoctorRepository doctors, Clock clock) {
        this.metrics = metrics;
        this.users = users;
        this.doctors = doctors;
        this.clock = clock;
    }

    public DashboardResponse getMetrics(Long tenantId, Authentication authentication) {
        if (tenantId == null || authentication == null || !(authentication.getPrincipal() instanceof Long userId)) {
            throw new AccessDeniedException("Acceso al dashboard denegado");
        }

        User user = users.findById(userId)
                .filter(u -> Boolean.TRUE.equals(u.getActive()) && tenantId.equals(u.getTenantId()))
                .orElseThrow(() -> new AccessDeniedException("Acceso al dashboard denegado"));

        DashboardQuery.Role role;
        try {
            role = DashboardQuery.Role.valueOf(user.getRole());
        } catch (IllegalArgumentException | NullPointerException ex) {
            throw new AccessDeniedException("Rol no autorizado");
        }
        boolean tokenMatchesUser = authentication.getAuthorities().stream()
                .anyMatch(a -> ("ROLE_" + role.name()).equals(a.getAuthority()));
        if (!tokenMatchesUser) {
            throw new AccessDeniedException("Rol no autorizado");
        }

        Long doctorId = null;
        if (role == DashboardQuery.Role.DOCTOR) {
            Doctor doctor = doctors.findByUserId(userId)
                    .filter(d -> tenantId.equals(d.getTenantId()))
                    .orElseThrow(() -> new AccessDeniedException("Médico no autorizado"));
            doctorId = doctor.getId();
        }

        LocalDate today = LocalDate.now(clock.withZone(CLINIC_ZONE));
        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        DashboardQuery query = new DashboardQuery(
                tenantId, doctorId, role,
                today.atStartOfDay(CLINIC_ZONE).toInstant().atOffset(ZoneOffset.UTC),
                today.plusDays(1).atStartOfDay(CLINIC_ZONE).toInstant().atOffset(ZoneOffset.UTC),
                weekStart, weekStart.plusWeeks(1));
        return metrics.read(query);
    }
}
