package com.sv.grupo7.medisuite.service;

import com.sv.grupo7.medisuite.dao.AppointmentRepository;
import com.sv.grupo7.medisuite.dao.DoctorRepository;
import com.sv.grupo7.medisuite.dao.PatientRepository;
import com.sv.grupo7.medisuite.exception.BusinessException;
import com.sv.grupo7.medisuite.model.medical.Appointment;
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
        var doctor = doctorRepo.findById(doctorId)
            .orElseThrow(() -> new BusinessException("Doctor no existe"));
        var patient = patientRepo.findById(patientId)
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
        Long tid = com.sv.grupo7.medisuite.security.TenantContext.currentTenantId();
        return appointmentRepo.findUpcomingByTenant(tid, OffsetDateTime.now());
    }

    @Transactional
    public void cancel(Long appointmentId) {
        Appointment a = appointmentRepo.findById(appointmentId)
            .orElseThrow(() -> new BusinessException("Cita no existe"));
        if (a.getScheduledAt().minusHours(24).isBefore(OffsetDateTime.now())) {
            throw new BusinessException("Solo se puede cancelar con 24h de anticipacion");
        }
        a.setStatus("CANCELLED");
    }

    @Transactional
    public Appointment complete(Long appointmentId) {
        Appointment a = appointmentRepo.findById(appointmentId)
            .orElseThrow(() -> new BusinessException("Cita no existe"));
        if ("CANCELLED".equals(a.getStatus()) || "COMPLETED".equals(a.getStatus())) {
            throw new BusinessException("La cita ya está " + a.getStatus().toLowerCase());
        }
        a.setStatus("COMPLETED");
        return a;
    }

    @Transactional
    public Appointment reschedule(Long appointmentId, OffsetDateTime newTime) {
        if (newTime.isBefore(OffsetDateTime.now())) {
            throw new BusinessException("La nueva hora no puede ser en el pasado");
        }
        Appointment a = appointmentRepo.findById(appointmentId)
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