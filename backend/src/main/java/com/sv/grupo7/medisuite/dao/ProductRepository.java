package com.sv.grupo7.medisuite.dao;

import com.sv.grupo7.medisuite.model.inventory.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByTenantId(Long tenantId);

    @Query("SELECT p FROM Product p WHERE p.tenant.id = :tenantId AND p.currentStock <= p.minStock")
    List<Product> findLowStockByTenantId(@Param("tenantId") Long tenantId);
}
