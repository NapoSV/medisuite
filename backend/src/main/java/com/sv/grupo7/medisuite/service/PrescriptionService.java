package com.sv.grupo7.medisuite.service;

import com.sv.grupo7.medisuite.dao.*;
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
    private final PatientRepository patientRepo;

    @Transactional
    public Prescription create(Long patientId, Long doctorId,
                               String indications, List<PrescriptionItem> items) {
        // El frontend envia patientId; buscamos (o creamos) su expediente
        var patient = patientRepo.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Paciente no existe"));
        MedicalRecord mr = recordRepo
                .findByPatientIdAndTenantId(patientId, patient.getTenant().getId())
                .orElseGet(() -> {
                    MedicalRecord nuevo = new MedicalRecord();
                    nuevo.setPatient(patient);
                    nuevo.setTenant(patient.getTenant());
                    return recordRepo.save(nuevo);
                });
        Doctor d = doctorRepo.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor no existe"));

        Prescription p = new Prescription();
        p.setTenant(patient.getTenant());
        p.setMedicalRecord(mr);
        p.setDoctor(d);
        p.setIssuedOn(java.time.LocalDate.now());
        p.setInstructions(indications);
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