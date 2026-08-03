package com.sv.grupo7.medisuite.dao;

import com.sv.grupo7.medisuite.model.inventory.PhysicalAsset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PhysicalAssetRepository extends JpaRepository<PhysicalAsset, Long> {
    List<PhysicalAsset> findByTenantId(Long tenantId);
    List<PhysicalAsset> findByTenantIdAndStatus(Long tenantId, String status);
}
