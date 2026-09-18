package com.sv.grupo7.medisuite.model.medical;

import com.sv.grupo7.medisuite.model.BaseEntity;
import com.sv.grupo7.medisuite.model.tenant.Tenant;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "medical_records")
@Getter @Setter
public class MedicalRecord extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false, unique = true)
    private Patient patient;

    @Column(name = "created_on", nullable = false)
    private LocalDate createdOn;

    @Column(name = "general_notes", columnDefinition = "text")
    private String generalNotes;

    @PrePersist
    void onMedicalRecordCreate() {
        if (createdOn == null) createdOn = LocalDate.now();
    }
}

