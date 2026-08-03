package com.sv.grupo7.medisuite.dao;

import com.sv.grupo7.medisuite.model.medical.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {
    List<Specialty> findByTenantIdAndActiveTrue(Long tenantId);
}
