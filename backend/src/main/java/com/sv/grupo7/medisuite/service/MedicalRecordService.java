package com.sv.grupo7.medisuite.service;

import com.sv.grupo7.medisuite.dao.*;
import com.sv.grupo7.medisuite.model.medical.*;
import com.sv.grupo7.medisuite.security.TenantContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MedicalRecordService {

    private final MedicalRecordRepository recordRepo;
    private final PatientRepository patientRepo;
    private final PrescriptionRepository prescriptionRepo;
    private final VitalSignRepository vitalSignRepo;

    @Transactional
    public Map<String, Object> findFullByPatientId(Long patientId) {
        Long tid = TenantContext.currentTenantId();
        MedicalRecord mr = recordRepo.findByPatientIdAndTenantId(patientId, tid)
                .orElseGet(() -> {
                    Patient p = patientRepo.findById(patientId).orElseThrow();
                    MedicalRecord nuevo = new MedicalRecord();
                    nuevo.setPatient(p);
                    nuevo.setTenant(p.getTenant());
                    nuevo.setCreatedOn(java.time.LocalDate.now());
                    return recordRepo.save(nuevo);
                });
        List<Prescription> prescriptions =
                prescriptionRepo.findByMedicalRecordIdOrderByIssuedOnDesc(mr.getId());
        List<VitalSign> vitalSigns =
                vitalSignRepo.findByMedicalRecordIdOrderByRecordedAtDesc(mr.getId());
        return Map.of(
                "id", mr.getId(),
                "patient", mr.getPatient(),
                "generalNotes", mr.getGeneralNotes() == null ? "" : mr.getGeneralNotes(),
                "prescriptions", prescriptions,
                "vitalSigns", vitalSigns
        );
    }
}