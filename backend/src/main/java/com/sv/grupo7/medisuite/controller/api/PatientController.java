package com.sv.grupo7.medisuite.controller.api;

import com.sv.grupo7.medisuite.model.medical.Patient;
import com.sv.grupo7.medisuite.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService service;

    @GetMapping
    public ResponseEntity<Page<Patient>> list(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(service.search(search, PageRequest.of(page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Patient> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<Patient> create(@RequestBody Patient p) {
        return ResponseEntity.status(201).body(service.create(p));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Patient> update(@PathVariable Long id, @RequestBody Patient p) {
        return ResponseEntity.ok(service.update(id, p));
    }
}