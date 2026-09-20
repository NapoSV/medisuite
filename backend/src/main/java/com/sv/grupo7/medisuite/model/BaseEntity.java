package com.sv.grupo7.medisuite.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sv.grupo7.medisuite.model.tenant.Tenant;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.OffsetDateTime;

@MappedSuperclass
@Getter @Setter
public abstract class BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(name = "tenant_id", insertable = false, updatable = false)
    private Long tenantId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    void onCreate() { createdAt = updatedAt = OffsetDateTime.now(); }

    @PreUpdate
    void onUpdate() { updatedAt = OffsetDateTime.now(); }

}
