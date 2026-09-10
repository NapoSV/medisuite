package com.sv.grupo7.medisuite.dto.user;

public record UserMeResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String cif,
        String role,
        Long tenantId
) {}
