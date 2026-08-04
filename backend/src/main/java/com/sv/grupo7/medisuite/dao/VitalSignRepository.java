package com.sv.grupo7.medisuite.dao;

import com.sv.grupo7.medisuite.model.medical.VitalSign;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VitalSignRepository extends JpaRepository<VitalSign, Long> {
    List<VitalSign> findByMedicalRecordIdOrderByRecordedAtDesc(Long medicalRecordId);
}
