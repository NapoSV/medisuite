package com.sv.grupo7.medisuite.service;

import com.sv.grupo7.medisuite.dao.*;
import com.sv.grupo7.medisuite.exception.BusinessException;
import com.sv.grupo7.medisuite.model.medical.*;
import com.sv.grupo7.medisuite.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PrescriptionService {

    private final PrescriptionRepository repo;
    private final MedicalRecordRepository recordRepo;
    private final DoctorRepository doctorRepo;

    @Transactional
    public Prescription create(Long medicalRecordId, Long userId,
                               String diagnosis, String notes, List<PrescriptionItem> items) {
        if (items == null || items.isEmpty()) {
            throw new BusinessException("La receta requiere al menos un medicamento");
        }
        Long tid = TenantContext.currentTenantId();
        MedicalRecord mr = recordRepo.findByIdAndTenantId(medicalRecordId, tid)
                .orElseThrow(() -> new BusinessException("Expediente no existe"));
        Doctor d = doctorRepo.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("El usuario autenticado no es un doctor"));
        if (!tid.equals(d.getTenantId())) {
            throw new BusinessException("Doctor no pertenece al tenant actual");
        }

        String instructions = diagnosis != null ? diagnosis : "";
        if (notes != null && !notes.isBlank()) instructions += "\n" + notes;

        Prescription p = new Prescription();
        p.setTenant(mr.getPatient().getTenant());
        p.setMedicalRecord(mr);
        p.setDoctor(d);
        p.setIssuedOn(java.time.LocalDate.now());
        p.setInstructions(instructions);
        items.forEach(it -> it.setPrescription(p));
        p.setItems(items);
        return repo.save(p);
    }

    @Transactional(readOnly = true)
    public Prescription findById(Long id) {
        Long tid = TenantContext.currentTenantId();
        return repo.findByIdAndTenantId(id, tid).orElseThrow(() ->
                new RuntimeException("Receta " + id + " no existe"));
    }
}