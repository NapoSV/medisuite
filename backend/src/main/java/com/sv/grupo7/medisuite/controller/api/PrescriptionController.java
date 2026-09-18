package com.sv.grupo7.medisuite.controller.api;

import com.sv.grupo7.medisuite.model.medical.Prescription;
import com.sv.grupo7.medisuite.model.medical.PrescriptionItem;
import com.sv.grupo7.medisuite.service.PrescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService service;

    public record CreateRequest(Long patientId, Long doctorId,
                                String indications, List<PrescriptionItem> items) {}

    @PostMapping
    public ResponseEntity<Prescription> create(@RequestBody CreateRequest r) {
        Prescription p = service.create(r.patientId(), r.doctorId(),
                r.indications(), r.items());
        return ResponseEntity.status(201).body(p);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Prescription> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }
}