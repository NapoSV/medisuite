package com.sv.grupo7.hospital.citas.dao;

import com.sv.grupo7.hospital.citas.model.medical.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    Optional<MedicalRecord> findByPatientIdAndTenantId(Long patientId, Long tenantId);
}
