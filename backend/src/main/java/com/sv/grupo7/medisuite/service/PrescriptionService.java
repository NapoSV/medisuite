package com.sv.grupo7.medisuite.service;

import com.sv.grupo7.medisuite.dao.*;
import com.sv.grupo7.medisuite.exception.BusinessException;
import com.sv.grupo7.medisuite.model.medical.*;
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
        MedicalRecord mr = recordRepo.findById(medicalRecordId)
                .orElseThrow(() -> new BusinessException("Expediente no existe"));
        Doctor d = doctorRepo.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("El usuario autenticado no es un doctor"));

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
        return repo.findById(id).orElseThrow(() ->
                new RuntimeException("Receta " + id + " no existe"));
    }
}