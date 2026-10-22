package com.sv.grupo7.medisuite.controller.api;

import com.sv.grupo7.medisuite.config.SecurityConfig;
import com.sv.grupo7.medisuite.dao.DoctorRepository;
import com.sv.grupo7.medisuite.dao.UserRepository;
import com.sv.grupo7.medisuite.exception.BusinessException;
import com.sv.grupo7.medisuite.model.medical.Prescription;
import com.sv.grupo7.medisuite.model.users.User;
import com.sv.grupo7.medisuite.security.JwtAuthenticationFilter;
import com.sv.grupo7.medisuite.security.JwtBlacklistService;
import com.sv.grupo7.medisuite.security.JwtTokenProvider;
import com.sv.grupo7.medisuite.security.RateLimitFilter;
import com.sv.grupo7.medisuite.service.PrescriptionService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.MalformedJwtException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PrescriptionController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, RateLimitFilter.class})
class PrescriptionControllerTest {

    private static final String BODY =
            "{\"medicalRecordId\":10,\"diagnosis\":\"Gripe\",\"notes\":\"Reposo\",\"items\":[{}]}";

    @Autowired MockMvc mvc;
    @MockBean PrescriptionService service;
    @MockBean UserRepository users;
    @MockBean DoctorRepository doctors;
    @MockBean JwtTokenProvider tokenProvider;
    @MockBean JwtBlacklistService blacklist;

    @Test
    void createWithValidJwtReturns201AndUsesAuthenticatedUser() throws Exception {
        jwt(7L, "DOCTOR", 1L);
        when(users.findById(7L)).thenReturn(Optional.of(user(1L, "DOCTOR")));
        when(service.create(eq(10L), eq(7L), eq("Gripe"), eq("Reposo"), anyList()))
                .thenReturn(new Prescription());

        mvc.perform(post("/api/prescriptions")
                        .header("Authorization", "Bearer valid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY))
                .andExpect(status().isCreated());

        verify(service).create(eq(10L), eq(7L), eq("Gripe"), eq("Reposo"), anyList());
    }

    @Test
    void createWithoutOrInvalidJwtNeverReachesService() throws Exception {
        mvc.perform(post("/api/prescriptions")
                        .contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isUnauthorized());
        when(tokenProvider.parse("invalid")).thenThrow(new MalformedJwtException("invalid token"));
        mvc.perform(post("/api/prescriptions")
                        .header("Authorization", "Bearer invalid")
                        .contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(service);
    }

    @Test
    void createRejectedByServiceReturnsClientError() throws Exception {
        jwt(7L, "DOCTOR", 1L);
        when(users.findById(7L)).thenReturn(Optional.of(user(1L, "DOCTOR")));
        when(service.create(any(), any(), any(), any(), any()))
                .thenThrow(new BusinessException("La receta requiere al menos un medicamento"));

        mvc.perform(post("/api/prescriptions")
                        .header("Authorization", "Bearer valid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"medicalRecordId\":10,\"diagnosis\":\"Gripe\",\"items\":[]}"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void getExistingReturns200() throws Exception {
        jwt(7L, "DOCTOR", 1L);
        when(users.findById(7L)).thenReturn(Optional.of(user(1L, "DOCTOR")));
        when(service.findById(7L)).thenReturn(new Prescription());

        mvc.perform(get("/api/prescriptions/7").header("Authorization", "Bearer valid"))
                .andExpect(status().isOk());
    }

    @Test
    void getPrescriptionOfOtherTenantIsNotReturned() throws Exception {
        jwt(7L, "DOCTOR", 1L);
        when(users.findById(7L)).thenReturn(Optional.of(user(1L, "DOCTOR")));
        when(service.findById(8L)).thenThrow(new BusinessException("Receta 8 no existe"));

        mvc.perform(get("/api/prescriptions/8").header("Authorization", "Bearer valid"))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void getWithoutJwtNeverReachesService() throws Exception {
        mvc.perform(get("/api/prescriptions/7")).andExpect(status().isUnauthorized());
        verifyNoInteractions(service);
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