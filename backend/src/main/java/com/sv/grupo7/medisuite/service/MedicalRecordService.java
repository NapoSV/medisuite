package com.sv.grupo7.medisuite.service;

import com.sv.grupo7.medisuite.dao.*;
import com.sv.grupo7.medisuite.exception.BusinessException;
import com.sv.grupo7.medisuite.model.medical.*;
import com.sv.grupo7.medisuite.model.tenant.Tenant;
import com.sv.grupo7.medisuite.security.TenantContext;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MedicalRecordService {

    private final MedicalRecordRepository recordRepo;
    private final PatientRepository patientRepo;
    private final PrescriptionRepository prescriptionRepo;
    private final VitalSignRepository vitalSignRepo;
    private final TenantRepository tenantRepo;

    /** Vista del expediente que devuelve la API (mismas claves JSON que la versión anterior). */
    public record MedicalRecordView(
        Long id,
        Patient patient,
        String generalNotes,
        List<Prescription> prescriptions,
        List<VitalSign> vitalSigns
    ) {}

    /**
     * Expediente completo del paciente. Todo paciente tiene expediente: si aún no existe
     * se crea en este momento (por eso el método es transaccional de escritura).
     */
    @Transactional
    public MedicalRecordView findFullByPatientId(Long patientId) {
        Long tid = TenantContext.currentTenantId();
        MedicalRecord mr = getOrCreateRecord(patientId, tid);
        return new MedicalRecordView(
                mr.getId(),
                mr.getPatient(),
                mr.getGeneralNotes() == null ? "" : mr.getGeneralNotes(),
                prescriptionRepo.findByMedicalRecordIdOrderByIssuedOnDesc(mr.getId()),
                vitalSignRepo.findByMedicalRecordIdOrderByRecordedAtDesc(mr.getId()));
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

    /** Escala vigente de triaje; la BD la impone desde V14. */
    private static final Set<String> PRIORIDADES = Set.of("NORMAL", "URGENTE", "EMERGENCIA");

    @Transactional
    public VitalSign addVitalSign(Long patientId, VitalSignRequest req) {
        String priority = req.priority() == null || req.priority().isBlank()
                ? "NORMAL" : req.priority().trim().toUpperCase();
        if (!PRIORIDADES.contains(priority)) {
            throw new BusinessException("Prioridad no valida: use NORMAL, URGENTE o EMERGENCIA");
        }
        Long tid = TenantContext.currentTenantId();
        Tenant tenant = tenantRepo.findById(tid)
                .orElseThrow(() -> new BusinessException("Tenant no encontrado"));
        // El paciente debe pertenecer al tenant antes de tocar su expediente.
        patientRepo.findByIdAndTenantId(patientId, tid)
                .orElseThrow(() -> new BusinessException("Paciente no existe"));
        MedicalRecord mr = getOrCreateRecord(patientId, tid);

        VitalSign vs = new VitalSign();
        vs.setMedicalRecord(mr);
        vs.setTenant(tenant);
        if (req.temperatureC() != null)  vs.setTemperatureC(req.temperatureC());
        if (req.heartRate() != null)     vs.setHeartRate(req.heartRate());
        if (req.bloodPressure() != null) vs.setBloodPressure(req.bloodPressure());
        if (req.weightKg() != null)      vs.setWeightKg(req.weightKg());
        if (req.heightCm() != null)      vs.setHeightCm(req.heightCm());
        if (req.symptoms() != null)      vs.setSymptoms(req.symptoms());
        vs.setPriority(priority);
        return vitalSignRepo.save(vs);
    }

    /** Un solo lugar para "buscar el expediente del tenant o crearlo". */
    private MedicalRecord getOrCreateRecord(Long patientId, Long tid) {
        return recordRepo.findByPatientIdAndTenantId(patientId, tid)
                .orElseGet(() -> {
                    Patient patient = patientRepo.findByIdAndTenantId(patientId, tid)
                            .orElseThrow(() -> new BusinessException("Paciente no existe"));
                    MedicalRecord nuevo = new MedicalRecord();
                    nuevo.setPatient(patient);
                    nuevo.setTenant(patient.getTenant());
                    nuevo.setCreatedOn(LocalDate.now());
                    return recordRepo.save(nuevo);
                });
    }
}