package com.sv.grupo7.medisuite.dao;

import com.sv.grupo7.medisuite.model.medical.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByTenantIdOrderByScheduledAtAsc(Long tenantId);
    List<Appointment> findByPatientIdAndTenantId(Long patientId, Long tenantId);
    List<Appointment> findByDoctorIdAndTenantId(Long doctorId, Long tenantId);
    Optional<Appointment> findByReservationCodeAndTenantId(String reservationCode, Long tenantId);

    List<Appointment> findByTenantIdAndDoctorIdAndScheduledAtBetween(
        Long tenantId, Long doctorId, OffsetDateTime from, OffsetDateTime to);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.tenant.id = :tid " +
           " AND a.scheduledAt >= :dayStart AND a.scheduledAt < :dayEnd")
    long countByDateAndTenant(@Param("tid") Long tenantId,
                              @Param("dayStart") OffsetDateTime start,
                              @Param("dayEnd") OffsetDateTime end);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE DATE(a.scheduledAt) = CURRENT_DATE AND a.tenantId = :tid")
    long countTodayByTenant(@Param("tid") Long tid);

    boolean existsByDoctorIdAndScheduledAtAndStatusNot(
        Long doctorId, OffsetDateTime scheduledAt, String excludedStatus);

    @Query("SELECT a FROM Appointment a WHERE a.tenant.id = :tid " +
           " AND a.scheduledAt >= :now ORDER BY a.scheduledAt ASC")
    List<Appointment> findUpcomingByTenant(@Param("tid") Long tenantId,
                                           @Param("now") OffsetDateTime now);
}
