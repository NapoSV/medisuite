package com.sv.grupo7.medisuite.dao;

import com.sv.grupo7.medisuite.model.medical.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    List<Doctor> findByTenantId(Long tenantId);
    List<Doctor> findByTenantIdAndSpecialtyId(Long tenantId, Long specialtyId);
    Optional<Doctor> findByIdAndTenantId(Long id, Long tenantId);

    @Query("SELECT d FROM Doctor d WHERE d.user.id = :userId")
    Optional<Doctor> findByUserId(@Param("userId") Long userId);
}
