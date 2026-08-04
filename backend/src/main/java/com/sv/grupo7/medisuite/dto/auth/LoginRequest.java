package com.sv.grupo7.medisuite.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "{auth.tenant.required}") String tenantSlug,
        @Email(message = "{auth.email.invalid}") @NotBlank String email,
        @NotBlank(message = "{auth.password.required}") String password
) {}