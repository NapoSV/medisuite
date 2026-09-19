package com.sv.grupo7.medisuite.model.audit;

import com.sv.grupo7.medisuite.model.BaseEntity;
import com.sv.grupo7.medisuite.model.tenant.Tenant;
import com.sv.grupo7.medisuite.model.users.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;

@Entity
@Table(name = "audit_logs")
@Getter @Setter
public class AuditLog extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 20)
    private String action;

    @Column(name = "entity_name", nullable = false, length = 60)
    private String entityName;

    @Column(name = "entity_id")
    private Long entityId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "data_before", columnDefinition = "jsonb")
    private String dataBefore;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "data_after", columnDefinition = "jsonb")
    private String dataAfter;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;
}
