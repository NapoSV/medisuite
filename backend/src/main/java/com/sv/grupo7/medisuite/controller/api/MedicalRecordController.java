package com.sv.grupo7.medisuite.controller.api;

import com.sv.grupo7.medisuite.model.medical.VitalSign;
import com.sv.grupo7.medisuite.service.MedicalRecordService;
import com.sv.grupo7.medisuite.service.MedicalRecordService.VitalSignRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class MedicalRecordController {

    private final MedicalRecordService service;

    @GetMapping("/{patientId}/medical-record")
    public ResponseEntity<Map<String, Object>> get(@PathVariable Long patientId) {
        return ResponseEntity.ok(service.findFullByPatientId(patientId));
    }

    @PostMapping("/{patientId}/vital-signs")
    public ResponseEntity<VitalSign> addVitalSign(@PathVariable Long patientId,
                                                  @RequestBody VitalSignRequest req) {
        return ResponseEntity.status(201).body(service.addVitalSign(patientId, req));
    }
}