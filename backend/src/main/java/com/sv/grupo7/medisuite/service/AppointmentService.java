package com.sv.grupo7.medisuite.service;

import com.sv.grupo7.medisuite.dao.AppointmentRepository;
import com.sv.grupo7.medisuite.dao.DoctorRepository;
import com.sv.grupo7.medisuite.dao.PatientRepository;
import com.sv.grupo7.medisuite.exception.BusinessException;
import com.sv.grupo7.medisuite.model.medical.Appointment;
import com.sv.grupo7.medisuite.model.medical.AppointmentStatus;
import com.sv.grupo7.medisuite.security.TenantContext;
import com.sv.grupo7.medisuite.service.concurrent.ReservationCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepo;
    private final PatientRepository patientRepo;
    private final DoctorRepository doctorRepo;
    private final ReservationCodeGenerator reservationCodeGenerator;

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

        ensureSlotFree(doctorId, scheduledAt);

        Appointment a = new Appointment();
        a.setTenant(doctor.getTenant());   // tenant desde entidad relacionada (o TenantContext)
        a.setPatient(patient);
        a.setDoctor(doctor);
        a.setScheduledAt(scheduledAt);
        a.setReason(reason);
        a.setStatus(AppointmentStatus.PENDING.name());
        a.setReservationCode(reservationCodeGenerator.next());
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
        Appointment a = findOwnedOrThrow(appointmentId);
        ensureOpen(a, "La cita ya esta ");
        if (a.getScheduledAt().minusHours(24).isBefore(OffsetDateTime.now())) {
            throw new BusinessException("Solo se puede cancelar con 24h de anticipacion");
        }
        a.setStatus(AppointmentStatus.CANCELLED.name());
        a.setCancelReason(reason.trim());
        a.setCancelledAt(OffsetDateTime.now());
    }

    @Transactional
    public Appointment complete(Long appointmentId) {
        Appointment a = findOwnedOrThrow(appointmentId);
        ensureOpen(a, "La cita ya esta ");
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
        a.setStatus(AppointmentStatus.COMPLETED.name());
        a.setCompletedAt(now);
        return a;
    }

    @Transactional
    public Appointment reschedule(Long appointmentId, OffsetDateTime newTime) {
        if (newTime.isBefore(OffsetDateTime.now())) {
            throw new BusinessException("La nueva hora no puede ser en el pasado");
        }
        Appointment a = findOwnedOrThrow(appointmentId);
        ensureOpen(a, "No se puede reprogramar una cita ");
        ensureSlotFree(a.getDoctorId(), newTime);
        a.setScheduledAt(newTime);
        a.setStatus(AppointmentStatus.SCHEDULED.name());
        return a;
    }

    /** Cita del tenant actual; una cita de otra clínica se trata como inexistente. */
    private Appointment findOwnedOrThrow(Long appointmentId) {
        return appointmentRepo.findByIdAndTenantId(appointmentId, TenantContext.currentTenantId())
            .orElseThrow(() -> new BusinessException("Cita no existe"));
    }

    /** Una cita cancelada o completada ya no admite cambios. */
    private void ensureOpen(Appointment a, String messagePrefix) {
        if (AppointmentStatus.isClosed(a.getStatus())) {
            throw new BusinessException(messagePrefix + a.getStatus().toLowerCase());
        }
    }

    /**
     * Validación rápida con mensaje claro. La garantía ante dos peticiones simultáneas es
     * el índice único {@code ux_appt_doctor_slot} (V5) más los reintentos del controlador.
     */
    private void ensureSlotFree(Long doctorId, OffsetDateTime when) {
        boolean occupied = appointmentRepo.existsByDoctorIdAndScheduledAtAndStatusNot(
            doctorId, when, AppointmentStatus.CANCELLED.name());
        if (occupied) {
            throw new BusinessException("El doctor ya tiene una cita en ese horario");
        }
    }
}