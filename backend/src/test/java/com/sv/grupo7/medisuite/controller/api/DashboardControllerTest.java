package com.sv.grupo7.medisuite.controller.api;

import com.sv.grupo7.medisuite.config.SecurityConfig;
import com.sv.grupo7.medisuite.dao.DoctorRepository;
import com.sv.grupo7.medisuite.dao.UserRepository;
import com.sv.grupo7.medisuite.dao.jdbc.DashboardMetricsReader;
import com.sv.grupo7.medisuite.dto.dashboard.DashboardQuery;
import com.sv.grupo7.medisuite.dto.dashboard.DashboardResponse;
import com.sv.grupo7.medisuite.model.users.User;
import com.sv.grupo7.medisuite.security.JwtAuthenticationFilter;
import com.sv.grupo7.medisuite.security.JwtBlacklistService;
import com.sv.grupo7.medisuite.security.JwtTokenProvider;
import com.sv.grupo7.medisuite.security.RateLimitFilter;
import com.sv.grupo7.medisuite.service.DashboardMetricsService;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DashboardController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, RateLimitFilter.class,
        DashboardMetricsService.class})
class DashboardControllerTest {

    @Autowired MockMvc mvc;
    @MockBean DashboardMetricsReader metrics;
    @MockBean UserRepository users;
    @MockBean DoctorRepository doctors;
    @MockBean JwtTokenProvider tokenProvider;
    @MockBean JwtBlacklistService blacklist;

    @Test
    void authenticatedTenantIsUsedEvenIfRequestTriesAnotherTenant() throws Exception {
        jwt(7L, "ADMIN", 1L);
        when(users.findById(7L)).thenReturn(Optional.of(user(1L, "ADMIN")));
        when(metrics.read(any())).thenReturn(new DashboardResponse(
                3L, 2L, 1L, 0L, 1L, 0L, List.of(), List.of(), null));

        mvc.perform(get("/api/dashboard/metrics?tenantId=2")
                        .header("Authorization", "Bearer valid"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.appointmentsToday").value(3))
                .andExpect(jsonPath("$.waitingRoom").isArray());

        ArgumentCaptor<DashboardQuery> scope = ArgumentCaptor.forClass(DashboardQuery.class);
        verify(metrics).read(scope.capture());
        assertThat(scope.getValue().tenantId()).isEqualTo(1L);
    }

    @Test
    void mismatchedRoleGetsForbiddenAndNoMetrics() throws Exception {
        jwt(7L, "DOCTOR", 1L);
        when(users.findById(7L)).thenReturn(Optional.of(user(1L, "RECEPTIONIST")));

        mvc.perform(get("/api/dashboard/metrics").header("Authorization", "Bearer valid"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("forbidden"));
        org.mockito.Mockito.verifyNoInteractions(metrics);
    }

    @Test
    void jdbcFailureReturnsSafeErrorWithoutSqlDetails() throws Exception {
        jwt(7L, "ADMIN", 1L);
        when(users.findById(7L)).thenReturn(Optional.of(user(1L, "ADMIN")));
        when(metrics.read(any())).thenThrow(new DataAccessResourceFailureException(
                "No se pudieron consultar las métricas", new SQLException("private SQL details")));

        mvc.perform(get("/api/dashboard/metrics").header("Authorization", "Bearer valid"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Error interno del servidor"))
                .andExpect(result -> assertThat(result.getResponse().getContentAsString())
                        .doesNotContain("private SQL details"));
    }

    private void jwt(Long userId, String role, Long tenantId) {
        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn(userId.toString());
        when(claims.get("role", String.class)).thenReturn(role);
        when(claims.get("tenant_id", Long.class)).thenReturn(tenantId);
        when(tokenProvider.parse("valid")).thenReturn(claims);
    }

    private User user(Long tenantId, String role) {
        User user = new User();
        user.setTenantId(tenantId);
        user.setActive(true);
        user.setRole(role);
        return user;
    }
}
