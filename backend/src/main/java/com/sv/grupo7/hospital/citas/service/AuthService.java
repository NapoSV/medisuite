package com.sv.grupo7.hospital.citas.service;

import com.sv.grupo7.hospital.citas.dao.TenantRepository;
import com.sv.grupo7.hospital.citas.dao.UserRepository;
import com.sv.grupo7.hospital.citas.dto.auth.LoginRequest;
import com.sv.grupo7.hospital.citas.dto.auth.LoginResponse;
import com.sv.grupo7.hospital.citas.exception.BusinessException;
import com.sv.grupo7.hospital.citas.model.tenant.Tenant;
import com.sv.grupo7.hospital.citas.model.users.User;
import com.sv.grupo7.hospital.citas.security.JwtTokenProvider;
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
            registerFailedAttempt(user);
            throw new BadCredentialsException("Credenciales inválidas");
        }

        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);

        String token = jwtTokenProvider.generateToken(user.getId(), user.getRole(), user.getTenantId());
        long expiresIn = jwtTokenProvider.getExpirationSeconds();
        String fullName = user.getFirstName() + " " + user.getLastName();

        return new LoginResponse(token, "Bearer", expiresIn,
                new LoginResponse.UserInfo(user.getId(), fullName, user.getRole(), user.getTenantId()));
    }

    private void registerFailedAttempt(User user) {
        int attempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(attempts);
        if (attempts >= MAX_ATTEMPTS) {
            user.setLockedUntil(OffsetDateTime.now().plusMinutes(LOCK_MINUTES));
        }
    }
}