package com.sv.grupo7.medisuite.controller.api;

import com.sv.grupo7.medisuite.service.DashboardMetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardMetricsService service;

    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Long>> metrics(@RequestParam(defaultValue = "1") Long tenantId) {
        return ResponseEntity.ok(service.getMetrics(tenantId));
    }
}