package com.sv.grupo7.medisuite.dto;

import com.sv.grupo7.medisuite.model.medical.Patient;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

/** Campos editables; tenant, usuario y fechas de auditoria son responsabilidad del servidor. */
public record PatientRequest(Long id,
        @NotBlank @Size(max=80) String firstName,
        @NotBlank @Size(max=80) String lastName,
        @NotBlank @Pattern(regexp="\\d{8}-\\d", message="DUI: formato 00000000-0") String dui,
        @NotNull @PastOrPresent LocalDate birthDate,
        @Size(max=20) String phone, @Size(max=200) String address,
        @Size(max=100) String emergencyContact, @Size(max=5) String bloodType, String allergies) {
    public Patient toPatient() {
        Patient p = new Patient();
        p.setId(id);
        p.setFirstName(firstName);
        p.setLastName(lastName);
        p.setDui(dui);
        p.setBirthDate(birthDate);
        p.setPhone(phone);
        p.setAddress(address);
        p.setEmergencyContact(emergencyContact);
        p.setBloodType(bloodType);
        p.setAllergies(allergies);
        return p;
    }
}
