package com.sv.grupo7.hospital.citas.dao;

import com.sv.grupo7.hospital.citas.model.inventory.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    List<PurchaseOrder> findByTenantIdOrderByOrderedOnDesc(Long tenantId);
    List<PurchaseOrder> findByTenantIdAndStatus(Long tenantId, String status);
}
