package com.sv.grupo.hospital.citas.model.medical;

import com.sv.grupo.hospital.citas.model.tenant.Tenant;
import com.sv.grupo.hospital.citas.model.users.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "patients")
@Getter @Setter
public class Patient {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(length = 20)
    private String phone;

    @Column(length = 200)
    private String address;

    @Column(name = "emergency_contact", length = 100)
    private String emergencyContact;

    @Column(name = "blood_type", length = 5)
    private String bloodType;

    @Column(columnDefinition = "text")
    private String allergies;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    void prePersist() { createdAt = updatedAt = OffsetDateTime.now(); }

    @PreUpdate
    void preUpdate() { updatedAt = OffsetDateTime.now(); }
}
