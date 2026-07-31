package com.sv.grupo.hospital.citas.model.medical;

import com.sv.grupo.hospital.citas.model.tenant.Tenant;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "medical_records")
@Getter @Setter
public class MedicalRecord {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false, unique = true)
    private Patient patient;

    @Column(name = "created_on", nullable = false)
    private LocalDate createdOn;

    @Column(name = "general_notes", columnDefinition = "text")
    private String generalNotes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    void prePersist() {
        createdAt = updatedAt = OffsetDateTime.now();
        if (createdOn == null) createdOn = LocalDate.now();
    }

    @PreUpdate
    void preUpdate() { updatedAt = OffsetDateTime.now(); }
}
