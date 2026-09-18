package com.sv.grupo7.medisuite.dao;

import com.sv.grupo7.medisuite.model.medical.MedicalRecord;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    @EntityGraph(attributePaths = {"patient"})
    Optional<MedicalRecord> findByPatientIdAndTenantId(Long patientId, Long tenantId);
}
