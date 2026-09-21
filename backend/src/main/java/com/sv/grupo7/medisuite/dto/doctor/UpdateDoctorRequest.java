package com.sv.grupo7.medisuite.dto.doctor;

import jakarta.validation.constraints.Size;

public record UpdateDoctorRequest(
        Long specialtyId,
        @Size(max = 30) String licenseNumber
) {}
