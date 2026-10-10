package com.sv.grupo7.medisuite.controller.api;

import com.sv.grupo7.medisuite.dto.dashboard.DashboardResponse;
import com.sv.grupo7.medisuite.security.TenantContext;
import com.sv.grupo7.medisuite.service.DashboardMetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardMetricsService service;

    @GetMapping("/metrics")
    public ResponseEntity<DashboardResponse> metrics(Authentication authentication) {
        return ResponseEntity.ok(service.getMetrics(TenantContext.currentTenantId(), authentication));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDenied() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "forbidden", "message", "Acceso denegado"));
    }
}
