package com.sv.grupo7.medisuite.dao;

import com.sv.grupo7.medisuite.model.medical.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {
    List<Specialty> findByTenantIdAndActiveTrue(Long tenantId);
    Optional<Specialty> findByIdAndTenantIdAndActiveTrue(Long id, Long tenantId);
}
