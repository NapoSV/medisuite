package com.sv.grupo7.hospital.citas.dao;

import com.sv.grupo7.hospital.citas.model.medical.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {
    List<Specialty> findByTenantIdAndActiveTrue(Long tenantId);
}
