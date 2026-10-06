package com.sv.grupo7.medisuite.service;

import com.sv.grupo7.medisuite.dao.DoctorRepository;
import com.sv.grupo7.medisuite.dao.UserRepository;
import com.sv.grupo7.medisuite.dao.jdbc.DashboardMetricsReader;
import com.sv.grupo7.medisuite.dto.dashboard.DashboardQuery;
import com.sv.grupo7.medisuite.dto.dashboard.DashboardResponse;
import com.sv.grupo7.medisuite.model.medical.Doctor;
import com.sv.grupo7.medisuite.model.users.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardMetricsServiceTest {

    @Mock DashboardMetricsReader metrics;
    @Mock UserRepository users;
    @Mock DoctorRepository doctors;

    private DashboardMetricsService service;

    @BeforeEach
    void setUp() {
        // Sunday 2026-10-11, 23:30 in El Salvador; Monday in UTC.
        service = new DashboardMetricsService(metrics, users, doctors,
                Clock.fixed(Instant.parse("2026-10-12T05:30:00Z"), ZoneOffset.UTC));
    }

    @Test
    void adminUsesOnlyItsAuthenticatedTenantAndLocalWeek() {
        when(users.findById(7L)).thenReturn(Optional.of(user(1L, "ADMIN")));
        when(metrics.read(any())).thenReturn(emptyResponse());

        service.getMetrics(1L, auth(7L, "ADMIN"));

        DashboardQuery q = captureQuery();
        assertThat(q.tenantId()).isEqualTo(1L);
        assertThat(q.doctorId()).isNull();
        assertThat(q.dayStart()).hasToString("2026-10-11T06:00Z");
        assertThat(q.dayEnd()).hasToString("2026-10-12T06:00Z");
        assertThat(q.weekStart()).hasToString("2026-10-05");
        assertThat(q.weekEnd()).hasToString("2026-10-12");
    }

    @Test
    void doctorIdComesFromServerMapping() {
        when(users.findById(7L)).thenReturn(Optional.of(user(1L, "DOCTOR")));
        Doctor doctor = new Doctor();
        doctor.setId(31L);
        doctor.setTenantId(1L);
        when(doctors.findByUserId(7L)).thenReturn(Optional.of(doctor));
        when(metrics.read(any())).thenReturn(emptyResponse());

        service.getMetrics(1L, auth(7L, "DOCTOR"));

        DashboardQuery q = captureQuery();
        assertThat(q.role()).isEqualTo(DashboardQuery.Role.DOCTOR);
        assertThat(q.doctorId()).isEqualTo(31L);
    }

    @Test
    void rejectsOtherTenantAndDoesNotReadMetrics() {
        when(users.findById(7L)).thenReturn(Optional.of(user(2L, "ADMIN")));
        assertThatThrownBy(() -> service.getMetrics(1L, auth(7L, "ADMIN")))
                .isInstanceOf(AccessDeniedException.class);
        verify(metrics, never()).read(any());
    }

    @Test
    void rejectsDoctorMappingInOtherTenant() {
        when(users.findById(7L)).thenReturn(Optional.of(user(1L, "DOCTOR")));
        Doctor doctor = new Doctor();
        doctor.setId(31L);
        doctor.setTenantId(2L);
        when(doctors.findByUserId(7L)).thenReturn(Optional.of(doctor));
        assertThatThrownBy(() -> service.getMetrics(1L, auth(7L, "DOCTOR")))
                .isInstanceOf(AccessDeniedException.class);
        verify(metrics, never()).read(any());
    }

    @Test
    void rejectsStaleTokenRoleAndInactiveUser() {
        when(users.findById(7L)).thenReturn(Optional.of(user(1L, "NURSE")));
        assertThatThrownBy(() -> service.getMetrics(1L, auth(7L, "ADMIN")))
                .isInstanceOf(AccessDeniedException.class);
        User inactive = user(1L, "NURSE");
        inactive.setActive(false);
        when(users.findById(7L)).thenReturn(Optional.of(inactive));
        assertThatThrownBy(() -> service.getMetrics(1L, auth(7L, "NURSE")))
                .isInstanceOf(AccessDeniedException.class);
        verify(metrics, never()).read(any());
    }

    @Test
    void passesNurseAndReceptionistRolesWithoutDoctorScope() {
        when(users.findById(7L)).thenReturn(Optional.of(user(1L, "NURSE")));
        when(metrics.read(any())).thenReturn(emptyResponse());
        service.getMetrics(1L, auth(7L, "NURSE"));
        assertThat(captureQuery().role()).isEqualTo(DashboardQuery.Role.NURSE);

        when(users.findById(7L)).thenReturn(Optional.of(user(1L, "RECEPTIONIST")));
        service.getMetrics(1L, auth(7L, "RECEPTIONIST"));
        ArgumentCaptor<DashboardQuery> all = ArgumentCaptor.forClass(DashboardQuery.class);
        verify(metrics, org.mockito.Mockito.times(2)).read(all.capture());
        assertThat(all.getAllValues().get(1).role()).isEqualTo(DashboardQuery.Role.RECEPTIONIST);
    }

    private DashboardQuery captureQuery() {
        ArgumentCaptor<DashboardQuery> captor = ArgumentCaptor.forClass(DashboardQuery.class);
        verify(metrics).read(captor.capture());
        return captor.getValue();
    }

    private User user(Long tenantId, String role) {
        User user = new User();
        user.setTenantId(tenantId);
        user.setRole(role);
        user.setActive(true);
        return user;
    }

    private Authentication auth(Long id, String role) {
        return new UsernamePasswordAuthenticationToken(id, null,
                List.of(new SimpleGrantedAuthority("ROLE_" + role)));
    }

    private DashboardResponse emptyResponse() {
        return new DashboardResponse(0L, 0L, null, null, null, null,
                List.of(), List.of(), null);
    }
}
