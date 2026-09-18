package com.sv.grupo7.medisuite.service;

import com.sv.grupo7.medisuite.dao.AppointmentRepository;
import com.sv.grupo7.medisuite.dao.DoctorRepository;
import com.sv.grupo7.medisuite.dao.PatientRepository;
import com.sv.grupo7.medisuite.exception.BusinessException;
import com.sv.grupo7.medisuite.model.medical.Appointment;
import com.sv.grupo7.medisuite.model.medical.Doctor;
import com.sv.grupo7.medisuite.model.medical.Patient;
import com.sv.grupo7.medisuite.model.tenant.Tenant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest{

    @Mock
    private AppointmentRepository appointmentRepo;

    @Mock
    private PatientRepository patientRepo;

    @Mock
    private DoctorRepository doctorRepo;

    @InjectMocks
    private AppointmentService appointmentService;

    @Test
    void create_conFechaPasada_debeLanzarBussinessException(){
        OffsetDateTime fechaPasada = OffsetDateTime.now().minusDays(1);

        assertThrows(BusinessException.class, () ->
                appointmentService.create(
                        1L,
                        1L,
                        fechaPasada,
                        "Consulta general"
                )
        );

    }


    @Test
    void create_conDoctorOcupado_debeLanzarBusinessException() {

        Long patientId = 1L;
        Long doctorId = 1L;
        OffsetDateTime fecha = OffsetDateTime.now().plusDays(2);

        Doctor doctor = new Doctor();
        Patient patient = new Patient();

        when(doctorRepo.findById(doctorId))
                .thenReturn(Optional.of(doctor));

        when(patientRepo.findById(patientId))
                .thenReturn(Optional.of(patient));

        when(appointmentRepo.existsByDoctorIdAndScheduledAtAndStatusNot(
                doctorId, fecha, "CANCELLED"))
                .thenReturn(true);

        assertThrows(BusinessException.class, () ->
                appointmentService.create(
                        patientId,
                        doctorId,
                        fecha,
                        "Consulta general"
                )
        );
    }

    @Test
    void create_exitoso_debeRetornarAppointmentPendingConCodigoReserva() {

        Long patientId = 1L;
        Long doctorId = 1L;
        OffsetDateTime fecha = OffsetDateTime.now().plusDays(2);

        Tenant tenant = new Tenant();

        Doctor doctor = new Doctor();
        doctor.setTenant(tenant);

        Patient patient = new Patient();

        when(doctorRepo.findById(doctorId))
                .thenReturn(Optional.of(doctor));

        when(patientRepo.findById(patientId))
                .thenReturn(Optional.of(patient));

        when(appointmentRepo.existsByDoctorIdAndScheduledAtAndStatusNot(
                doctorId, fecha, "CANCELLED"))
                .thenReturn(false);

        when(appointmentRepo.save(any(Appointment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Appointment resultado = appointmentService.create(
                patientId,
                doctorId,
                fecha,
                "Consulta general"
        );

        assertNotNull(resultado);
        assertEquals("PENDING", resultado.getStatus());
        assertNotNull(resultado.getReservationCode());
    }


    @Test
    void cancel_debeCambiarStatusACancelled() {

        Long appointmentId = 1L;

        Appointment appointment = new Appointment();
        appointment.setStatus("PENDING");
        appointment.setScheduledAt(OffsetDateTime.now().plusDays(3));

        when(appointmentRepo.findById(appointmentId))
                .thenReturn(Optional.of(appointment));

        appointmentService.cancel(appointmentId);

        assertEquals("CANCELLED", appointment.getStatus());
    }


}