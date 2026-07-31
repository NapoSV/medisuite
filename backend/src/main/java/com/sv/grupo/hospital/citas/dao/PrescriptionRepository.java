package com.sv.grupo.hospital.citas.dao;

import com.sv.grupo.hospital.citas.model.medical.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    List<Prescription> findByMedicalRecordIdOrderByIssuedOnDesc(Long medicalRecordId);
}
