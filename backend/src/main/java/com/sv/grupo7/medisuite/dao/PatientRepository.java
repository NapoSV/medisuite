package com.sv.grupo7.medisuite.dao;

import com.sv.grupo7.medisuite.model.medical.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    List<Patient> findByTenantId(Long tenantId);
    Optional<Patient> findByUserIdAndTenantId(Long userId, Long tenantId);

    // En PatientRepository.java — agregar:
    @Query("SELECT COUNT(p) FROM Patient p WHERE p.tenantId = :tid")
    long countActiveByTenant(@Param("tid") Long tid);
}
