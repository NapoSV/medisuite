package com.sv.grupo7.medisuite.model.medical;

import com.sv.grupo7.medisuite.model.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Column(columnDefinition = "text")
    private String instructions;

    /**
     * Lista de ítems de receta en orden de inserción.
     * Se usa {@code List} (y no {@code Set}) porque el orden importa para impresión
     * y se permite el mismo medicamento con distintas dosis.
     * El orden se conserva en la base de datos con {@code @OrderColumn}.
     */
    @OneToMany(mappedBy = "prescription", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderColumn(name = "order_idx")
    private List<PrescriptionItem> items = new ArrayList<>();

    @PrePersist
    void prePersistPrescription() {
        if (issuedOn == null) issuedOn = LocalDate.now();
    }
}
