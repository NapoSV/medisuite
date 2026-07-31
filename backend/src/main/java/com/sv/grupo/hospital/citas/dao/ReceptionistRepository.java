package com.sv.grupo.hospital.citas.dao;

import com.sv.grupo.hospital.citas.model.medical.Receptionist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReceptionistRepository extends JpaRepository<Receptionist, Long> {
    List<Receptionist> findByTenantId(Long tenantId);
}
