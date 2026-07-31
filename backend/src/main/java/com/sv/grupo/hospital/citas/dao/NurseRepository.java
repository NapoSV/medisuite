package com.sv.grupo.hospital.citas.dao;

import com.sv.grupo.hospital.citas.model.medical.Nurse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NurseRepository extends JpaRepository<Nurse, Long> {
    List<Nurse> findByTenantId(Long tenantId);
}
