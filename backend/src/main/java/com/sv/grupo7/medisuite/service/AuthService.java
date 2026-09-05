package com.sv.grupo7.medisuite.service;

import com.sv.grupo7.medisuite.dao.TenantRepository;
import com.sv.grupo7.medisuite.dao.UserRepository;
import com.sv.grupo7.medisuite.dto.auth.LoginRequest;
import com.sv.grupo7.medisuite.dto.auth.LoginResponse;
import com.sv.grupo7.medisuite.exception.BusinessException;
import com.sv.grupo7.medisuite.model.tenant.Tenant;
import com.sv.grupo7.medisuite.model.users.User;
import com.sv.grupo7.medisuite.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final int MAX_ATTEMPTS = 5;
    private static final int LOCK_MINUTES = 15;

    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public LoginResponse login(LoginRequest req) {
        Tenant tenant = tenantRepository.findBySlug(req.tenantSlug())
                .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));

        User user = userRepository.findByEmailAndTenantId(req.email(), tenant.getId())
                .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));

        if (Boolean.FALSE.equals(user.getActive())) {
            throw new BusinessException("La cuenta está inactiva. Contacte al administrador.");
        }
        if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(OffsetDateTime.now())) {
            throw new BusinessException("Cuenta bloqueada temporalmente. Intente más tarde.");
        }

        if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            userRepository.incrementFailedAttempts(
                    user.getId(),
                    MAX_ATTEMPTS,
                    OffsetDateTime.now().plusMinutes(LOCK_MINUTES));
            throw new BadCredentialsException("Credenciales inválidas");
        }

        userRepository.resetFailedAttempts(user.getId());

        String token = jwtTokenProvider.generateToken(user.getId(), user.getRole(), user.getTenantId());
        long expiresIn = jwtTokenProvider.getExpirationSeconds();
        String fullName = user.getFirstName() + " " + user.getLastName();

        return new LoginResponse(token, "Bearer", expiresIn,
                new LoginResponse.UserInfo(user.getId(), fullName, user.getRole(), user.getTenantId()));
    }
}