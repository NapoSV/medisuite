package com.sv.grupo7.medisuite.service;

import com.sv.grupo7.medisuite.dao.AppointmentRepository;
import com.sv.grupo7.medisuite.dao.DoctorRepository;
import com.sv.grupo7.medisuite.dao.PatientRepository;
import com.sv.grupo7.medisuite.exception.BusinessException;
import com.sv.grupo7.medisuite.model.medical.Appointment;
import com.sv.grupo7.medisuite.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Random;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepo;
    private final PatientRepository patientRepo;
    private final DoctorRepository doctorRepo;

    @Transactional
    public Appointment create(Long patientId, Long doctorId,
                              OffsetDateTime scheduledAt, String reason) {
        if (scheduledAt.isBefore(OffsetDateTime.now())) {
            throw new BusinessException("La cita no puede ser en el pasado");
        }
        Long tid = TenantContext.currentTenantId();
        var doctor = doctorRepo.findByIdAndTenantId(doctorId, tid)
            .orElseThrow(() -> new BusinessException("Doctor no existe"));
        var patient = patientRepo.findByIdAndTenantId(patientId, tid)
            .orElseThrow(() -> new BusinessException("Paciente no existe"));

        boolean occupied = appointmentRepo
            .existsByDoctorIdAndScheduledAtAndStatusNot(doctorId, scheduledAt, "CANCELLED");
        if (occupied) {
            throw new BusinessException("El doctor ya tiene una cita en ese horario");
        }

        Appointment a = new Appointment();
        a.setTenant(doctor.getTenant());   // tenant desde entidad relacionada (o TenantContext)
        a.setPatient(patient);
        a.setDoctor(doctor);
        a.setScheduledAt(scheduledAt);
        a.setReason(reason);
        a.setStatus("PENDING");
        a.setReservationCode("COD-" + String.format("%04d", new Random().nextInt(10000)));
        return appointmentRepo.save(a);
    }

    public List<Appointment> upcoming() {
        Long tid = TenantContext.currentTenantId();
        return appointmentRepo.findUpcomingByTenant(tid, OffsetDateTime.now());
    }

    @Transactional
    public void cancel(Long appointmentId, String reason) {
        if (reason == null || reason.trim().length() < 5) {
            throw new BusinessException("Debe indicar un motivo de cancelacion (minimo 5 caracteres)");
        }
        if (reason.length() > 500) {
            throw new BusinessException("El motivo de cancelacion no puede exceder 500 caracteres");
        }
        Appointment a = appointmentRepo.findByIdAndTenantId(appointmentId, TenantContext.currentTenantId())
            .orElseThrow(() -> new BusinessException("Cita no existe"));
        if ("CANCELLED".equals(a.getStatus()) || "COMPLETED".equals(a.getStatus())) {
            throw new BusinessException("La cita ya esta " + a.getStatus().toLowerCase());
        }
        if (a.getScheduledAt().minusHours(24).isBefore(OffsetDateTime.now())) {
            throw new BusinessException("Solo se puede cancelar con 24h de anticipacion");
        }
        a.setStatus("CANCELLED");
        a.setCancelReason(reason.trim());
        a.setCancelledAt(OffsetDateTime.now());
    }

    @Transactional
    public Appointment complete(Long appointmentId) {
        Appointment a = appointmentRepo.findByIdAndTenantId(appointmentId, TenantContext.currentTenantId())
            .orElseThrow(() -> new BusinessException("Cita no existe"));
        if ("CANCELLED".equals(a.getStatus()) || "COMPLETED".equals(a.getStatus())) {
            throw new BusinessException("La cita ya esta " + a.getStatus().toLowerCase());
        }
        // Validacion: no se puede completar una cita antes de su fecha agendada.
        // Permitimos completar desde el inicio del dia de la cita (00:00) hasta siempre.
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime inicioDelDiaDeCita = a.getScheduledAt().toLocalDate()
                .atStartOfDay().atOffset(a.getScheduledAt().getOffset());
        if (now.isBefore(inicioDelDiaDeCita)) {
            throw new BusinessException(
                "No se puede completar una cita antes de su fecha agendada (" +
                a.getScheduledAt().toLocalDate() + ")");
        }
        a.setStatus("COMPLETED");
        a.setCompletedAt(now);
        return a;
    }

    @Transactional
    public Appointment reschedule(Long appointmentId, OffsetDateTime newTime) {
        if (newTime.isBefore(OffsetDateTime.now())) {
            throw new BusinessException("La nueva hora no puede ser en el pasado");
        }
        Appointment a = appointmentRepo.findByIdAndTenantId(appointmentId, TenantContext.currentTenantId())
            .orElseThrow(() -> new BusinessException("Cita no existe"));
        if ("CANCELLED".equals(a.getStatus()) || "COMPLETED".equals(a.getStatus())) {
            throw new BusinessException("No se puede reprogramar una cita " + a.getStatus().toLowerCase());
        }
        boolean occupied = appointmentRepo.existsByDoctorIdAndScheduledAtAndStatusNot(
            a.getDoctorId(), newTime, "CANCELLED");
        if (occupied) {
            throw new BusinessException("El doctor ya tiene una cita en ese horario");
        }
        a.setScheduledAt(newTime);
        a.setStatus("SCHEDULED");
        return a;
    }

    public List<OffsetDateTime> availableSlots(Long doctorId, LocalDate date) {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.of("-06:00"));
        List<OffsetDateTime> all = new ArrayList<>();
        for (int h = 8; h < 17; h++) {
            all.add(date.atTime(h, 0).atOffset(ZoneOffset.of("-06:00")));
        }
        return all.stream().filter(s ->
            s.isAfter(now) &&
            !appointmentRepo.existsByDoctorIdAndScheduledAtAndStatusNot(doctorId, s, "CANCELLED")
        ).toList();
    }



}