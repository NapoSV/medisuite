package com.sv.grupo7.medisuite.model.medical;

import com.sv.grupo7.medisuite.model.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.OffsetDateTime;

@Entity
@Table(name = "appointments", indexes = {
    @Index(name = "idx_appt_doctor_date", columnList = "doctor_id, scheduled_at"),
    @Index(name = "idx_appt_patient", columnList = "patient_id")
})
@Getter @Setter
public class Appointment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(name = "scheduled_at", nullable = false)
    private OffsetDateTime scheduledAt;

    @Column(nullable = false, length = 20)
    private String status;   // PENDING, CONFIRMED, IN_WAITING, IN_CONSULTATION, COMPLETED, CANCELLED, NO_SHOW

    @Column(length = 200)
    private String reason;

    @Column(length = 50)
    private String office;

    @Column(name = "reservation_code", nullable = false, length = 10)
    private String reservationCode;
}