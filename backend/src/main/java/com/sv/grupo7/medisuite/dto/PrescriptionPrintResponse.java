package com.sv.grupo7.medisuite.dto;

import java.time.LocalDate;
import java.util.List;

/** Contrato de impresion sin proxies JPA ni datos ajenos a la receta. */
public record PrescriptionPrintResponse(Long id, LocalDate issuedOn, PatientInfo patient,
                                        DoctorInfo doctor, String instructions, List<Item> items) {
    public record PatientInfo(Long id, String firstName, String lastName, String dui) {}
    public record DoctorInfo(String fullName, String licenseNumber) {}
    public record Item(String medication, String dosage, String frequency, Integer durationDays) {}
}
