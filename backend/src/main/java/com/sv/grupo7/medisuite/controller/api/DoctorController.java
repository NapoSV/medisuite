// DoctorController.java
package com.sv.grupo7.medisuite.controller.api;

import com.sv.grupo7.medisuite.model.medical.Doctor;
import com.sv.grupo7.medisuite.service.DoctorService;
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
    public ResponseEntity<Doctor> create(@RequestBody Doctor d) {
        return ResponseEntity.status(201).body(service.create(d));
    }
}