package com.sv.grupo7.medisuite.service;

import com.sv.grupo7.medisuite.dao.*;
import com.sv.grupo7.medisuite.exception.BusinessException;
import com.sv.grupo7.medisuite.model.medical.*;
import com.sv.grupo7.medisuite.model.tenant.Tenant;
import com.sv.grupo7.medisuite.security.TenantContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MedicalRecordService {

    private final MedicalRecordRepository recordRepo;
    private final PatientRepository patientRepo;
    private final PrescriptionRepository prescriptionRepo;
    private final VitalSignRepository vitalSignRepo;
    private final TenantRepository tenantRepo;

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

    public record VitalSignRequest(
        BigDecimal temperatureC,
        Integer heartRate,
        String bloodPressure,
        BigDecimal weightKg,
        BigDecimal heightCm,
        String symptoms,
        String priority
    ) {}

    @Transactional
    public VitalSign addVitalSign(Long patientId, VitalSignRequest req) {
        Long tid = TenantContext.currentTenantId();
        Tenant tenant = tenantRepo.findById(tid)
                .orElseThrow(() -> new BusinessException("Tenant no encontrado"));
        Patient patient = patientRepo.findById(patientId)
                .orElseThrow(() -> new BusinessException("Paciente no existe"));

        MedicalRecord mr = recordRepo.findByPatientIdAndTenantId(patientId, tid)
                .orElseGet(() -> {
                    MedicalRecord nuevo = new MedicalRecord();
                    nuevo.setPatient(patient);
                    nuevo.setTenant(tenant);
                    return recordRepo.save(nuevo);
                });

        VitalSign vs = new VitalSign();
        vs.setMedicalRecord(mr);
        vs.setTenant(tenant);
        if (req.temperatureC() != null)  vs.setTemperatureC(req.temperatureC());
        if (req.heartRate() != null)     vs.setHeartRate(req.heartRate());
        if (req.bloodPressure() != null) vs.setBloodPressure(req.bloodPressure());
        if (req.weightKg() != null)      vs.setWeightKg(req.weightKg());
        if (req.heightCm() != null)      vs.setHeightCm(req.heightCm());
        if (req.symptoms() != null)      vs.setSymptoms(req.symptoms());
        vs.setPriority(req.priority() != null ? req.priority() : "NORMAL");
        return vitalSignRepo.save(vs);
    }
}