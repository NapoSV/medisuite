package com.sv.grupo7.medisuite.model.tenant;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "tenants")
@Getter @Setter
public class Tenant implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String slug;

    @Column(name = "commercial_name", nullable = false, length = 150)
    private String commercialName;

    @Column(name = "legal_name", length = 150)
    private String legalName;

    @Column(name = "tax_id", length = 30)
    private String taxId;

    @Column(length = 60)
    private String country;

    @Column(nullable = false, length = 50)
    private String timezone;

    @Column(name = "default_language", nullable = false, length = 5)
    private String defaultLanguage;

    @Column(nullable = false, length = 20)
    private String plan;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "trial_expires_at")
    private LocalDate trialExpiresAt;

    @Column(name = "logo_url", length = 255)
    private String logoUrl;

    @Column(name = "primary_color", length = 7)
    private String primaryColor;

    @Column(name = "max_users", nullable = false)
    private Integer maxUsers;

    @Column(name = "max_patients", nullable = false)
    private Integer maxPatients;

    @Column(name = "max_doctors", nullable = false)
    private Integer maxDoctors;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    void prePersist() { createdAt = updatedAt = OffsetDateTime.now(); }

    @PreUpdate
    void preUpdate() { updatedAt = OffsetDateTime.now(); }
}
