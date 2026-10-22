package com.sv.grupo7.medisuite.service;

import com.sv.grupo7.medisuite.dao.AppointmentRepository;
import com.sv.grupo7.medisuite.model.medical.AppointmentStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * Calcula los horarios disponibles de un médico.
 *
 * <p>Separado de {@link AppointmentService}: la agenda cambia por razones distintas
 * (horario de atención de la clínica) que el ciclo de vida de una cita. El horario
 * es configurable en {@code application.yml} ({@code app.agenda.*}).</p>
 */
@Service
public class AgendaService {

    static final ZoneId CLINIC_ZONE = ZoneId.of("America/El_Salvador");

    private final AppointmentRepository appointmentRepo;
    private final int openingHour;
    private final int closingHour;
    private final Clock clock;

    @Autowired
    public AgendaService(AppointmentRepository appointmentRepo,
                         @Value("${app.agenda.hora-inicio:8}") int openingHour,
                         @Value("${app.agenda.hora-fin:17}") int closingHour) {
        this(appointmentRepo, openingHour, closingHour, Clock.system(CLINIC_ZONE));
    }

    AgendaService(AppointmentRepository appointmentRepo, int openingHour, int closingHour, Clock clock) {
        this.appointmentRepo = appointmentRepo;
        this.openingHour = openingHour;
        this.closingHour = closingHour;
        this.clock = clock;
    }

    /** Bloques de una hora, futuros y sin otra cita activa del médico. */
    public List<OffsetDateTime> availableSlots(Long doctorId, LocalDate date) {
        OffsetDateTime now = OffsetDateTime.now(clock);
        List<OffsetDateTime> all = new ArrayList<>();
        for (int h = openingHour; h < closingHour; h++) {
            all.add(date.atTime(h, 0).atZone(CLINIC_ZONE).toOffsetDateTime());
        }
        return all.stream().filter(s ->
            s.isAfter(now) &&
            !appointmentRepo.existsByDoctorIdAndScheduledAtAndStatusNot(
                    doctorId, s, AppointmentStatus.CANCELLED.name())
        ).toList();
    }
}
