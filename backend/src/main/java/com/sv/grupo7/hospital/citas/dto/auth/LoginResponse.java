package com.sv.grupo7.hospital.citas.dto.auth;

public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        UserInfo user
) {
    public record UserInfo(Long id, String fullName, String role, Long tenantId) {}
}