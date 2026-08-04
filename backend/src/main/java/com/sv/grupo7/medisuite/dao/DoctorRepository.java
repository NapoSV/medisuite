package com.sv.grupo7.medisuite.dao;

import com.sv.grupo7.medisuite.model.medical.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    List<Doctor> findByTenantId(Long tenantId);
    List<Doctor> findByTenantIdAndSpecialtyId(Long tenantId, Long specialtyId);
}
