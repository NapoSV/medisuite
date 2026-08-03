package com.sv.grupo7.medisuite.dao;

import com.sv.grupo7.medisuite.model.audit.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByTenantIdOrderByCreatedAtDesc(Long tenantId);
    List<AuditLog> findByEntityNameAndEntityId(String entityName, Long entityId);
}
