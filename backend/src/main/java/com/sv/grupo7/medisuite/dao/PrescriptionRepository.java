package com.sv.grupo7.medisuite.dao;

import com.sv.grupo7.medisuite.model.medical.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    List<Prescription> findByMedicalRecordIdOrderByIssuedOnDesc(Long medicalRecordId);
    Optional<Prescription> findByIdAndTenantId(Long id, Long tenantId);
}
