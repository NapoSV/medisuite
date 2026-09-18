package com.sv.grupo7.medisuite.model.medical;

import com.sv.grupo7.medisuite.model.BaseEntity;
import com.sv.grupo7.medisuite.model.tenant.Tenant;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "vital_signs")
@Getter @Setter
public class VitalSign extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medical_record_id", nullable = false)
    private MedicalRecord medicalRecord;

    @Column(name = "recorded_at", nullable = false)
    private OffsetDateTime recordedAt;

    @Column(name = "weight_kg", precision = 5, scale = 2)
    private BigDecimal weightKg;

    @Column(name = "height_cm", precision = 5, scale = 2)
    private BigDecimal heightCm;

    @Column(name = "blood_pressure", length = 15)
    private String bloodPressure;

    @Column(name = "temperature_c", precision = 4, scale = 1)
    private BigDecimal temperatureC;

    @Column(name = "heart_rate")
    private Integer heartRate;

    @Column(columnDefinition = "text")
    private String symptoms;

    @Column(length = 10)
    private String priority;

    @PrePersist
    void prePersist() {
        if (recordedAt == null) recordedAt = OffsetDateTime.now();
    }
}
