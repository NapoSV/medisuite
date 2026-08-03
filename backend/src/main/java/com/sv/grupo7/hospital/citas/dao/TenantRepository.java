package com.sv.grupo7.hospital.citas.dao;

import com.sv.grupo7.hospital.citas.model.tenant.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TenantRepository extends JpaRepository<Tenant, Long> {
    Optional<Tenant> findBySlug(String slug);
}
