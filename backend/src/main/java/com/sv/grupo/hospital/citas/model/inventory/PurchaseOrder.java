package com.sv.grupo.hospital.citas.model.inventory;

import com.sv.grupo.hospital.citas.model.tenant.Tenant;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "purchase_orders")
@Getter @Setter
public class PurchaseOrder {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(length = 150)
    private String supplier;

    @Column(name = "ordered_on", nullable = false)
    private LocalDate orderedOn;

    @Column(nullable = false, length = 25)
    private String status;

    @Column(name = "total_amount", precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    void prePersist() {
        createdAt = updatedAt = OffsetDateTime.now();
        if (orderedOn == null) orderedOn = LocalDate.now();
    }

    @PreUpdate
    void preUpdate() { updatedAt = OffsetDateTime.now(); }
}
