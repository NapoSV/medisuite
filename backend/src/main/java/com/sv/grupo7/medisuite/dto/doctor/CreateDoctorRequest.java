package com.sv.grupo7.medisuite.dto.doctor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateDoctorRequest(
        @NotBlank @Size(max = 80) String firstName,
        @NotBlank @Size(max = 80) String lastName,
        @NotBlank @Email @Size(max = 150) String email,
        @NotBlank @Size(max = 20) String cif,
        @NotNull Long specialtyId,
        @NotBlank @Size(max = 30) String licenseNumber
) {}
