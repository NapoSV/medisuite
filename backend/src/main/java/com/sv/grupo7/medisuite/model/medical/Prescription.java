package com.sv.grupo7.medisuite.model.medical;

import com.sv.grupo7.medisuite.model.BaseEntity;
import com.sv.grupo7.medisuite.model.tenant.Tenant;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "prescriptions")
@Getter @Setter
public class Prescription extends BaseEntity {

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

    @PrePersist
    void prePersist() {
        if (issuedOn == null) issuedOn = LocalDate.now();
    }

}
