package com.sv.grupo7.medisuite.service;

import com.sv.grupo7.medisuite.dao.AppointmentRepository;
import com.sv.grupo7.medisuite.dao.DoctorRepository;
import com.sv.grupo7.medisuite.dao.PatientRepository;
import com.sv.grupo7.medisuite.exception.BusinessException;
import com.sv.grupo7.medisuite.model.medical.Appointment;
import com.sv.grupo7.medisuite.model.medical.Doctor;
import com.sv.grupo7.medisuite.model.medical.Patient;
import com.sv.grupo7.medisuite.model.tenant.Tenant;
import com.sv.grupo7.medisuite.security.TenantContext;
import com.sv.grupo7.medisuite.service.concurrent.ReservationCodeGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Tag("regression")
@DisplayName("Pruebas unitarias del servicio de citas")
@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest{

    private static final Long TENANT_ID = 1L;

    @Mock
    private AppointmentRepository appointmentRepo;

    @Mock
    private PatientRepository patientRepo;

    @Mock
    private DoctorRepository doctorRepo;

    @Mock
    private ReservationCodeGenerator reservationCodeGenerator;

    @InjectMocks
    private AppointmentService appointmentService;

    @Test
    void create_conFechaPasada_debeLanzarBusinessException(){
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

        try (MockedStatic<TenantContext> ctx = mockStatic(TenantContext.class)) {
            ctx.when(TenantContext::currentTenantId).thenReturn(TENANT_ID);

            when(doctorRepo.findByIdAndTenantId(doctorId, TENANT_ID))
                    .thenReturn(Optional.of(doctor));

            when(patientRepo.findByIdAndTenantId(patientId, TENANT_ID))
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

        try (MockedStatic<TenantContext> ctx = mockStatic(TenantContext.class)) {
            ctx.when(TenantContext::currentTenantId).thenReturn(TENANT_ID);

            when(doctorRepo.findByIdAndTenantId(doctorId, TENANT_ID))
                    .thenReturn(Optional.of(doctor));

            when(patientRepo.findByIdAndTenantId(patientId, TENANT_ID))
                    .thenReturn(Optional.of(patient));

            when(appointmentRepo.existsByDoctorIdAndScheduledAtAndStatusNot(
                    doctorId, fecha, "CANCELLED"))
                    .thenReturn(false);

            when(reservationCodeGenerator.next()).thenReturn("R-00000001");

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
    }


    @Test
    void cancel_conMotivoValido_debeCambiarStatusACancelled() {

        Long appointmentId = 1L;

        Appointment appointment = new Appointment();
        appointment.setStatus("PENDING");
        appointment.setScheduledAt(OffsetDateTime.now().plusDays(3));

        try (MockedStatic<TenantContext> ctx = mockStatic(TenantContext.class)) {
            ctx.when(TenantContext::currentTenantId).thenReturn(TENANT_ID);

            when(appointmentRepo.findByIdAndTenantId(appointmentId, TENANT_ID))
                    .thenReturn(Optional.of(appointment));

            appointmentService.cancel(appointmentId, "Paciente reporta imposibilidad de asistir");

            assertEquals("CANCELLED", appointment.getStatus());
            assertEquals("Paciente reporta imposibilidad de asistir", appointment.getCancelReason());
            assertNotNull(appointment.getCancelledAt());
        }
    }

    @Test
    void cancel_sinMotivo_debeLanzarBusinessException() {
        assertThrows(BusinessException.class, () ->
                appointmentService.cancel(1L, null));
        assertThrows(BusinessException.class, () ->
                appointmentService.cancel(1L, ""));
        assertThrows(BusinessException.class, () ->
                appointmentService.cancel(1L, "xx"));
    }

    @Test
    void complete_antesDeFechaDeCita_debeLanzarBusinessException() {
        Long appointmentId = 1L;
        Appointment appointment = new Appointment();
        appointment.setStatus("PENDING");
        appointment.setScheduledAt(OffsetDateTime.now().plusDays(5));

        try (MockedStatic<TenantContext> ctx = mockStatic(TenantContext.class)) {
            ctx.when(TenantContext::currentTenantId).thenReturn(TENANT_ID);

            when(appointmentRepo.findByIdAndTenantId(appointmentId, TENANT_ID))
                    .thenReturn(Optional.of(appointment));

            assertThrows(BusinessException.class, () ->
                    appointmentService.complete(appointmentId));
        }
    }

    @Test
    void complete_enDiaDeCita_debePermitirYRegistrarCompletedAt() {
        Long appointmentId = 1L;
        Appointment appointment = new Appointment();
        appointment.setStatus("PENDING");
        appointment.setScheduledAt(OffsetDateTime.now());

        try (MockedStatic<TenantContext> ctx = mockStatic(TenantContext.class)) {
            ctx.when(TenantContext::currentTenantId).thenReturn(TENANT_ID);

            when(appointmentRepo.findByIdAndTenantId(appointmentId, TENANT_ID))
                    .thenReturn(Optional.of(appointment));

            Appointment result = appointmentService.complete(appointmentId);

            assertEquals("COMPLETED", result.getStatus());
            assertNotNull(result.getCompletedAt());
        }
    }
        @Test
    void reschedule_conHorarioOcupado_debeLanzarBusinessException() {

        Long appointmentId = 1L;
        Long doctorId = 2L;
        OffsetDateTime nuevaFecha = OffsetDateTime.now().plusDays(3);

        Appointment appointment = mock(Appointment.class);
        when(appointment.getStatus()).thenReturn("PENDING");
        when(appointment.getDoctorId()).thenReturn(doctorId);

        try (MockedStatic<TenantContext> ctx = mockStatic(TenantContext.class)) {
            ctx.when(TenantContext::currentTenantId).thenReturn(TENANT_ID);

            when(appointmentRepo.findByIdAndTenantId(appointmentId, TENANT_ID))
                    .thenReturn(Optional.of(appointment));

            when(appointmentRepo.existsByDoctorIdAndScheduledAtAndStatusNot(
                    doctorId, nuevaFecha, "CANCELLED"))
                    .thenReturn(true);

            assertThrows(BusinessException.class, () ->
                    appointmentService.reschedule(appointmentId, nuevaFecha)
            );

            verify(appointment, never()).setScheduledAt(nuevaFecha);
        }
    }
    @Test
    void cancel_conMotivoMayorA500Caracteres_debeLanzarBusinessException() {

        String motivoDemasiadoLargo = "a".repeat(501);

        assertThrows(BusinessException.class, () ->
                appointmentService.cancel(1L, motivoDemasiadoLargo)
        );

        verifyNoInteractions(appointmentRepo);
    }
        @Test
    void complete_conCitaCancelada_debeLanzarBusinessException() {

        Long appointmentId = 1L;

        Appointment appointment = new Appointment();
        appointment.setStatus("CANCELLED");

        try (MockedStatic<TenantContext> ctx = mockStatic(TenantContext.class)) {
            ctx.when(TenantContext::currentTenantId).thenReturn(TENANT_ID);

            when(appointmentRepo.findByIdAndTenantId(appointmentId, TENANT_ID))
                    .thenReturn(Optional.of(appointment));

            assertThrows(BusinessException.class, () ->
                    appointmentService.complete(appointmentId)
            );

            assertEquals("CANCELLED", appointment.getStatus());
            assertNull(appointment.getCompletedAt());
        }
    }
        @Test
    void cancel_conCitaDeOtroTenant_debeLanzarBusinessException() {

        Long citaDeOtroTenantId = 99L;
        String motivo = "Paciente no puede asistir";

        try (MockedStatic<TenantContext> ctx = mockStatic(TenantContext.class)) {
            ctx.when(TenantContext::currentTenantId).thenReturn(TENANT_ID);

            when(appointmentRepo.findByIdAndTenantId(
                    citaDeOtroTenantId, TENANT_ID))
                    .thenReturn(Optional.empty());

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> appointmentService.cancel(
                            citaDeOtroTenantId, motivo)
            );

            assertEquals("Cita no existe", exception.getMessage());

            verify(appointmentRepo).findByIdAndTenantId(
                    citaDeOtroTenantId, TENANT_ID);
        }
    }
        @Test
    void create_conDoctorTenantAYPacienteTenantB_debeRechazarSinGuardar() {
        Long doctorId = 10L;
        Long patientId = 20L;
        OffsetDateTime fecha = OffsetDateTime.now().plusDays(3);

        Tenant tenantA = new Tenant();
        tenantA.setId(TENANT_ID);

        Doctor doctor = new Doctor();
        doctor.setTenant(tenantA);

        // El paciente de B no es visible en una búsqueda dentro de A.
        try (MockedStatic<TenantContext> ctx = mockStatic(TenantContext.class)) {
            ctx.when(TenantContext::currentTenantId).thenReturn(TENANT_ID);

            when(doctorRepo.findByIdAndTenantId(doctorId, TENANT_ID))
                    .thenReturn(Optional.of(doctor));
            when(patientRepo.findByIdAndTenantId(patientId, TENANT_ID))
                    .thenReturn(Optional.empty());

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> appointmentService.create(
                            patientId, doctorId, fecha, "Consulta general")
            );

            assertEquals("Paciente no existe", exception.getMessage());
            verify(patientRepo).findByIdAndTenantId(patientId, TENANT_ID);
            verifyNoInteractions(appointmentRepo);
        }
    }

    @Test
    void complete_conCitaDeOtroTenant_debeRechazarSinGuardar() {
        Long appointmentId = 50L;

        try (MockedStatic<TenantContext> ctx = mockStatic(TenantContext.class)) {
            ctx.when(TenantContext::currentTenantId).thenReturn(TENANT_ID);

            when(appointmentRepo.findByIdAndTenantId(appointmentId, TENANT_ID))
                    .thenReturn(Optional.empty());

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> appointmentService.complete(appointmentId)
            );

            assertEquals("Cita no existe", exception.getMessage());
            verify(appointmentRepo).findByIdAndTenantId(appointmentId, TENANT_ID);
            verifyNoMoreInteractions(appointmentRepo);
            verifyNoInteractions(patientRepo, doctorRepo);
        }
    }

    @Test
    void reschedule_conCitaDeOtroTenant_debeRechazarSinGuardar() {
        Long appointmentId = 50L;
        OffsetDateTime nuevaFecha = OffsetDateTime.now().plusDays(3);

        try (MockedStatic<TenantContext> ctx = mockStatic(TenantContext.class)) {
            ctx.when(TenantContext::currentTenantId).thenReturn(TENANT_ID);

            when(appointmentRepo.findByIdAndTenantId(appointmentId, TENANT_ID))
                    .thenReturn(Optional.empty());

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> appointmentService.reschedule(appointmentId, nuevaFecha)
            );

            assertEquals("Cita no existe", exception.getMessage());
            verify(appointmentRepo).findByIdAndTenantId(appointmentId, TENANT_ID);
            verifyNoMoreInteractions(appointmentRepo);
            verifyNoInteractions(patientRepo, doctorRepo);
        }
    }

    @Test
    void reschedule_conHorarioDisponible_debeActualizarFechaYEstado() {
        Long appointmentId = 1L;
        Long doctorId = 2L;
        OffsetDateTime fechaOriginal = OffsetDateTime.now().plusDays(3);
        OffsetDateTime nuevaFecha = fechaOriginal.plusDays(1);

        Appointment appointment = mock(Appointment.class);
        when(appointment.getStatus()).thenReturn("PENDING");
        when(appointment.getDoctorId()).thenReturn(doctorId);

        try (MockedStatic<TenantContext> ctx = mockStatic(TenantContext.class)) {
            ctx.when(TenantContext::currentTenantId).thenReturn(TENANT_ID);

            when(appointmentRepo.findByIdAndTenantId(appointmentId, TENANT_ID))
                    .thenReturn(Optional.of(appointment));
            when(appointmentRepo.existsByDoctorIdAndScheduledAtAndStatusNot(
                    doctorId, nuevaFecha, "CANCELLED"))
                    .thenReturn(false);

            Appointment result =
                    appointmentService.reschedule(appointmentId, nuevaFecha);

            assertSame(appointment, result);
            verify(appointment).setScheduledAt(nuevaFecha);
            verify(appointment).setStatus("SCHEDULED");
            verify(appointmentRepo).findByIdAndTenantId(appointmentId, TENANT_ID);
            verify(appointmentRepo).existsByDoctorIdAndScheduledAtAndStatusNot(
                    doctorId, nuevaFecha, "CANCELLED");
        }
    }

    @Test
    void cancel_conMenosDe24Horas_debeRechazarSinModificarCita() {
        Long appointmentId = 1L;
        OffsetDateTime fecha = OffsetDateTime.now().plusHours(12);

        Appointment appointment = new Appointment();
        appointment.setStatus("PENDING");
        appointment.setScheduledAt(fecha);

        try (MockedStatic<TenantContext> ctx = mockStatic(TenantContext.class)) {
            ctx.when(TenantContext::currentTenantId).thenReturn(TENANT_ID);

            when(appointmentRepo.findByIdAndTenantId(appointmentId, TENANT_ID))
                    .thenReturn(Optional.of(appointment));

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> appointmentService.cancel(
                            appointmentId, "Paciente no puede asistir")
            );

            assertEquals(
                    "Solo se puede cancelar con 24h de anticipacion",
                    exception.getMessage()
            );
            assertEquals("PENDING", appointment.getStatus());
            assertEquals(fecha, appointment.getScheduledAt());
            assertNull(appointment.getCancelReason());
            assertNull(appointment.getCancelledAt());

            verify(appointmentRepo).findByIdAndTenantId(appointmentId, TENANT_ID);
            verifyNoMoreInteractions(appointmentRepo);
        }
    }
}