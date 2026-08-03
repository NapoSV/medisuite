package com.sv.grupo7.hospital.citas.dao;

import com.sv.grupo7.hospital.citas.model.medical.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    List<Patient> findByTenantId(Long tenantId);
    Optional<Patient> findByUserIdAndTenantId(Long userId, Long tenantId);
}
