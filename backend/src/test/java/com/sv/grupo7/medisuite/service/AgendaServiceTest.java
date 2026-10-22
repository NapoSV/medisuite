package com.sv.grupo7.medisuite.service;

import com.sv.grupo7.medisuite.dao.AppointmentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@Tag("regression")
@DisplayName("Pruebas unitarias de la agenda de horarios disponibles")
@ExtendWith(MockitoExtension.class)
class AgendaServiceTest {

    private static final LocalDate DIA = LocalDate.of(2030, 1, 10);
    private static final Long DOCTOR_ID = 7L;

    @Mock
    private AppointmentRepository appointmentRepo;

    private static Clock relojA(int hora, int minuto) {
        ZonedDateTime ahora = DIA.atTime(hora, minuto).atZone(AgendaService.CLINIC_ZONE);
        return Clock.fixed(ahora.toInstant(), AgendaService.CLINIC_ZONE);
    }

    private static OffsetDateTime bloque(LocalDate dia, int hora) {
        return dia.atTime(hora, 0).atZone(AgendaService.CLINIC_ZONE).toOffsetDateTime();
    }

    @Test
    @DisplayName("Excluye las horas ya pasadas y las ocupadas por otra cita activa")
    void availableSlots_excluyePasadosYOcupados() {
        AgendaService agenda = new AgendaService(appointmentRepo, 8, 17, relojA(9, 30));
        // Solo el bloque de las 11:00 tiene otra cita activa.
        when(appointmentRepo.existsByDoctorIdAndScheduledAtAndStatusNot(
                eq(DOCTOR_ID), any(OffsetDateTime.class), eq("CANCELLED")))
                .thenAnswer(inv -> bloque(DIA, 11).isEqual(inv.getArgument(1)));

        List<OffsetDateTime> slots = agenda.availableSlots(DOCTOR_ID, DIA);

        assertThat(slots).containsExactly(
                bloque(DIA, 10), bloque(DIA, 12), bloque(DIA, 13),
                bloque(DIA, 14), bloque(DIA, 15), bloque(DIA, 16));
    }

    @Test
    @DisplayName("Respeta el horario configurado de la clínica")
    void availableSlots_usaElHorarioConfigurado() {
        AgendaService agenda = new AgendaService(appointmentRepo, 13, 15, relojA(9, 0));
        LocalDate manana = DIA.plusDays(1);

        List<OffsetDateTime> slots = agenda.availableSlots(DOCTOR_ID, manana);

        assertThat(slots).containsExactly(bloque(manana, 13), bloque(manana, 14));
    }
}
