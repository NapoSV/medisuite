package com.sv.grupo7.hospital.citas.model.inventory;

import com.sv.grupo7.hospital.citas.model.tenant.Tenant;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "physical_assets")
@Getter @Setter
public class PhysicalAsset {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 80)
    private String category;

    @Column(name = "acquisition_value", precision = 12, scale = 2)
    private BigDecimal acquisitionValue;

    @Column(name = "acquired_on")
    private LocalDate acquiredOn;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(length = 100)
    private String location;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    void prePersist() { createdAt = updatedAt = OffsetDateTime.now(); }

    @PreUpdate
    void preUpdate() { updatedAt = OffsetDateTime.now(); }
}
