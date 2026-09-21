// DoctorController.java
package com.sv.grupo7.medisuite.controller.api;

import com.sv.grupo7.medisuite.dto.doctor.CreateDoctorRequest;
import com.sv.grupo7.medisuite.dto.doctor.UpdateDoctorRequest;
import com.sv.grupo7.medisuite.model.medical.Doctor;
import com.sv.grupo7.medisuite.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {
    private final DoctorService service;

    @GetMapping
    public ResponseEntity<List<Doctor>> list() { return ResponseEntity.ok(service.findAll()); }

    @GetMapping("/{id}")
    public ResponseEntity<Doctor> get(@PathVariable Long id) { return ResponseEntity.ok(service.findById(id)); }

    @PostMapping
    public ResponseEntity<Doctor> create(@Valid @RequestBody CreateDoctorRequest req) {
        return ResponseEntity.status(201).body(service.create(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Doctor> update(@PathVariable Long id, @Valid @RequestBody UpdateDoctorRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }
}