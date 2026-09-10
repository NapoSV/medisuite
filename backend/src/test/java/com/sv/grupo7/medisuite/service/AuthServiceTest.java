package com.sv.grupo7.medisuite.service;

import com.sv.grupo7.medisuite.dao.TenantRepository;
import com.sv.grupo7.medisuite.dao.UserRepository;
import com.sv.grupo7.medisuite.dto.auth.LoginRequest;
import com.sv.grupo7.medisuite.exception.BusinessException;
import com.sv.grupo7.medisuite.model.tenant.Tenant;
import com.sv.grupo7.medisuite.model.users.User;
import com.sv.grupo7.medisuite.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest{

    @Mock
    private UserRepository userRepository;

    @Mock
    private TenantRepository tenantRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    @Test
    void loginConPasswordIncorrecta_incrementaIntentosYLanzaExcepcion() {

        Tenant tenant = new Tenant();
        tenant.setId(1L);

        User user = new User();
        user.setId(10L);
        user.setActive(true);
        user.setPasswordHash("password-hash");
        user.setLockedUntil(null);

        LoginRequest request = new LoginRequest(
                "clinica-demo",
                "usuario@medisuite.com",
                "password-incorrecta"
        );

        when(tenantRepository.findBySlug("clinica-demo"))
                .thenReturn(Optional.of(tenant));

        when(userRepository.findByEmailAndTenantId(
                "usuario@medisuite.com", 1L))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "password-incorrecta", "password-hash"))
                .thenReturn(false);

        assertThrows(
                BadCredentialsException.class,
                () -> authService.login(request)
        );

        verify(userRepository).incrementFailedAttempts(
                eq(10L),
                eq(5),
                any(OffsetDateTime.class)
        );

        verify(jwtTokenProvider, never())
                .generateToken(any(), any(), any());
    }


    @Test
    void loginConCuentaBloqueada_lanzaBusinessException() {

        Tenant tenant = new Tenant();
        tenant.setId(1L);

        User user = new User();
        user.setId(10L);
        user.setActive(true);
        user.setLockedUntil(OffsetDateTime.now().plusMinutes(10));

        LoginRequest request = new LoginRequest(
                "clinica-demo",
                "usuario@medisuite.com",
                "password"
        );

        when(tenantRepository.findBySlug("clinica-demo"))
                .thenReturn(Optional.of(tenant));

        when(userRepository.findByEmailAndTenantId(
                "usuario@medisuite.com", 1L))
                .thenReturn(Optional.of(user));

        assertThrows(
                BusinessException.class,
                () -> authService.login(request)
        );

        verify(passwordEncoder, never())
                .matches(anyString(), anyString());

        verify(jwtTokenProvider, never())
                .generateToken(any(), any(), any());
    }


    @Test
    void loginCorrecto_reiniciaIntentosYGeneraToken(){

        Tenant tenant = new Tenant();
        tenant.setId(1L);

        User user = new User();
        user.setId(10L);
        user.setTenantId(1L);
        user.setFirstName("Fatima");
        user.setLastName("Bosch");
        user.setRole("ADMIN");
        user.setActive(true);
        user.setPasswordHash("password-hash");
        user.setLockedUntil(null);

        LoginRequest request = new LoginRequest(
                "clinica-demo",
                "usuario@medisuite.com",
                "password-correcta"
        );

        when(tenantRepository.findBySlug("clinica-demo"))
                .thenReturn(Optional.of(tenant));

        when(userRepository.findByEmailAndTenantId(
                "usuario@medisuite.com", 1L))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "password-correcta" , "password-hash"))
                .thenReturn(true);

        when(jwtTokenProvider.generateToken(
                10L, "ADMIN", 1L))
                .thenReturn("token-prueba");

        when(jwtTokenProvider.getExpirationSeconds())
                .thenReturn(3600L);

        var reponse = assertDoesNotThrow(
                () -> authService.login(request)
        );

        assertNotNull(reponse);

        verify(userRepository).resetFailedAttempts(10L);

        verify(jwtTokenProvider).generateToken(10L, "ADMIN", 1L);

        verify(userRepository, never()).incrementFailedAttempts(any(),anyInt(),any(OffsetDateTime.class));

    }


    @Test
    void loginConUsuarioInactivo_lanzaBusinessException() {

        Tenant tenant = new Tenant();
        tenant.setId(1L);

        User user = new User();
        user.setId(10L);
        user.setActive(false);
        user.setLockedUntil(null);

        LoginRequest request = new LoginRequest(
                "clinica-demo",
                "usuario@medsuite.com",
                "password"
        );

        when(tenantRepository.findBySlug("clinica-demo"))
                .thenReturn(Optional.of(tenant));

        when(userRepository.findByEmailAndTenantId(
                "usuario@medsuite.com" , 1L))
                .thenReturn(Optional.of(user));

        assertThrows(
                BusinessException.class,
                () -> authService.login(request)
        );

        verify(passwordEncoder, never())
                .matches(anyString(), anyString());

        verify(jwtTokenProvider, never())
                .generateToken(any(), any(), any());
    }



    @Test
    void loginConTenantInexistente_lanzaBadCredentialsException() {

        LoginRequest request = new LoginRequest(
                "clinica-inexistente",
                "usuario@medisuite.com",
                "password"
        );

        when(tenantRepository.findBySlug("clinica-inexistente"))
                .thenReturn(Optional.empty());

        assertThrows(
                BadCredentialsException.class,
                () -> authService.login(request)
        );

        verify(userRepository, never())
                .findByEmailAndTenantId(anyString(), anyLong());

        verify(passwordEncoder, never())
                .matches(anyString(), anyString());

        verify(jwtTokenProvider, never())
                .generateToken(any(), any(), any());
    }


}
