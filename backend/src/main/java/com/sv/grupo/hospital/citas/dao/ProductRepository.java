package com.sv.grupo.hospital.citas.dao;

import com.sv.grupo.hospital.citas.model.inventory.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByTenantId(Long tenantId);
    List<Product> findByTenantIdAndCurrentStockLessThanEqualMinStock(Long tenantId);
}
