package com.sv.grupo.hospital.citas.model.medical;

import com.sv.grupo.hospital.citas.model.tenant.Tenant;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "prescriptions")
@Getter @Setter
public class Prescription {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medical_record_id", nullable = false)
    private MedicalRecord medicalRecord;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(name = "issued_on", nullable = false)
    private LocalDate issuedOn;

    @Column(nullable = false, columnDefinition = "text")
    private String medications;

    @Column(length = 150)
    private String dosage;

    @Column(length = 50)
    private String duration;

    @Column(columnDefinition = "text")
    private String instructions;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    void prePersist() {
        createdAt = updatedAt = OffsetDateTime.now();
        if (issuedOn == null) issuedOn = LocalDate.now();
    }

    @PreUpdate
    void preUpdate() { updatedAt = OffsetDateTime.now(); }
}
