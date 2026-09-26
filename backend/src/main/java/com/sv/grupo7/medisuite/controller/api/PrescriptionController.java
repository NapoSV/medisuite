package com.sv.grupo7.medisuite.controller.api;

import com.sv.grupo7.medisuite.model.medical.Prescription;
import com.sv.grupo7.medisuite.model.medical.PrescriptionItem;
import com.sv.grupo7.medisuite.service.PrescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService service;

    public record CreateRequest(Long medicalRecordId, String diagnosis,
                                String notes, List<PrescriptionItem> items) {}

    @PostMapping
    public ResponseEntity<Prescription> create(@RequestBody CreateRequest r,
                                               Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        Prescription p = service.create(r.medicalRecordId(), userId,
                r.diagnosis(), r.notes(), r.items());
        return ResponseEntity.status(201).body(p);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Prescription> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }
}