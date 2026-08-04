package com.sv.grupo7.medisuite.dao;

import com.sv.grupo7.medisuite.model.medical.Nurse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NurseRepository extends JpaRepository<Nurse, Long> {
    List<Nurse> findByTenantId(Long tenantId);
}
