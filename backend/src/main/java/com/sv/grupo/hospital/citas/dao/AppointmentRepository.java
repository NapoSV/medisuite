package com.sv.grupo.hospital.citas.dao;

import com.sv.grupo.hospital.citas.model.medical.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByTenantIdOrderByScheduledAtAsc(Long tenantId);
    List<Appointment> findByPatientIdAndTenantId(Long patientId, Long tenantId);
    List<Appointment> findByDoctorIdAndTenantId(Long doctorId, Long tenantId);
    Optional<Appointment> findByReservationCodeAndTenantId(String reservationCode, Long tenantId);
}
