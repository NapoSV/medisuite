package com.sv.grupo7.medisuite.model.medical;

import com.sv.grupo7.medisuite.model.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.io.Serializable;

@Entity
@Table(name = "medical_records")
@Getter @Setter
public class MedicalRecord extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false, unique = true)
    private Patient patient;

    @Column(name = "patient_id", insertable = false, updatable = false)
    private Long patientId;

    @Column(name = "created_on", nullable = false)
    private LocalDate createdOn;

    @Column(name = "general_notes", columnDefinition = "text")
    private String generalNotes;

    @PrePersist
    void onMedicalRecordCreate() {
        if (createdOn == null) createdOn = LocalDate.now();
    }
}

