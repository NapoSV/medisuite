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
}