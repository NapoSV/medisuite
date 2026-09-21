package com.sv.grupo7.medisuite.controller.api;

import com.sv.grupo7.medisuite.dao.SpecialtyRepository;
import com.sv.grupo7.medisuite.model.medical.Specialty;
import com.sv.grupo7.medisuite.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/specialties")
@RequiredArgsConstructor
public class SpecialtyController {

    private final SpecialtyRepository repo;

    @GetMapping
    public ResponseEntity<List<Specialty>> list() {
        return ResponseEntity.ok(repo.findByTenantIdAndActiveTrue(TenantContext.currentTenantId()));
    }
}
