package com.sv.grupo7.medisuite.dao;

import com.sv.grupo7.medisuite.model.medical.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByTenantIdOrderByScheduledAtAsc(Long tenantId);
    List<Appointment> findByPatientIdAndTenantId(Long patientId, Long tenantId);
    List<Appointment> findByDoctorIdAndTenantId(Long doctorId, Long tenantId);
    Optional<Appointment> findByReservationCodeAndTenantId(String reservationCode, Long tenantId);

    // En AppointmentRepository.java — agregar:
    @Query("SELECT COUNT(a) FROM Appointment a WHERE DATE(a.scheduledAt) = CURRENT_DATE AND a.tenantId = :tid")
    long countTodayByTenant(@Param("tid") Long tid);


}
