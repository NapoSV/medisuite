package com.sv.grupo7.medisuite.dao;

import com.sv.grupo7.medisuite.model.medical.Receptionist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReceptionistRepository extends JpaRepository<Receptionist, Long> {
    List<Receptionist> findByTenantId(Long tenantId);
}
