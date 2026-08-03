package com.sv.grupo7.hospital.citas.dao;

import com.sv.grupo7.hospital.citas.model.inventory.PhysicalAsset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PhysicalAssetRepository extends JpaRepository<PhysicalAsset, Long> {
    List<PhysicalAsset> findByTenantId(Long tenantId);
    List<PhysicalAsset> findByTenantIdAndStatus(Long tenantId, String status);
}
