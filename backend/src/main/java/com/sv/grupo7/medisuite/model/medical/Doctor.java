package com.sv.grupo7.medisuite.model.medical;

import com.sv.grupo7.medisuite.model.BaseEntity;
import com.sv.grupo7.medisuite.model.tenant.Tenant;
import com.sv.grupo7.medisuite.model.users.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;

@Entity
@Table(name = "doctors")
@Getter @Setter
public class Doctor extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialty_id", nullable = false)
    private Specialty specialty;

    @Column(name = "license_number", nullable = false, length = 30)
    private String licenseNumber;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "available_schedule", columnDefinition = "jsonb")
    private String availableSchedule;
}
