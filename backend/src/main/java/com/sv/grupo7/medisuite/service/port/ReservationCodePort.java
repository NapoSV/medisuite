package com.sv.grupo7.medisuite.service.port;

public interface ReservationCodePort {
    String nextCode(Long tenantId);
}
