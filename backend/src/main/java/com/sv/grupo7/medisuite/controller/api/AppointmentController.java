package com.sv.grupo7.medisuite.controller.api;

import com.sv.grupo7.medisuite.exception.DataAccessException;
import com.sv.grupo7.medisuite.exception.JdbcErrorCode;
import com.sv.grupo7.medisuite.model.medical.Appointment;
import com.sv.grupo7.medisuite.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private static final int MAX_CREATE_RETRIES = 3;

    private final AppointmentService service;

    public record CreateRequest(Long patientId, Long doctorId,
                                OffsetDateTime scheduledAt, String reason) {}

    @GetMapping
    public ResponseEntity<List<Appointment>> list(
            @RequestParam(defaultValue = "false") boolean upcoming) {
        // Nota: hoy el service solo expone upcoming(); el flag se preserva
        // para extender a historico en Fase 3.
        return ResponseEntity.ok(service.upcoming());
    }

    @PostMapping
    public ResponseEntity<Appointment> create(@RequestBody CreateRequest r) {
        // Cada intento es una transacción nueva (create() es @Transactional): tras un
        // UNIQUE_VIOLATION la transacción anterior queda abortada y no se puede reutilizar.
        for (int retry = 0; ; retry++) {
            try {
                Appointment a = service.create(r.patientId(), r.doctorId(),
                                               r.scheduledAt(), r.reason());
                return ResponseEntity.status(201).body(a);
            } catch (DataIntegrityViolationException e) {
                JdbcErrorCode code = JdbcErrorCode.from(e);
                if (code != JdbcErrorCode.UNIQUE_VIOLATION || retry >= MAX_CREATE_RETRIES) {
                    throw new DataAccessException("crear cita", code, e);
                }
                log.warn("UNIQUE_VIOLATION al crear cita; reintento {} de {}",
                         retry + 1, MAX_CREATE_RETRIES);
            }
        }
    }

    public record CancelRequest(String reason) {}

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Map<String,String>> cancel(@PathVariable Long id,
                                                     @RequestBody(required = false) CancelRequest r) {
        String reason = (r != null) ? r.reason() : null;
        service.cancel(id, reason);
        return ResponseEntity.ok(Map.of("status", "CANCELLED"));
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<Appointment> complete(@PathVariable Long id) {
        return ResponseEntity.ok(service.complete(id));
    }

    public record RescheduleRequest(OffsetDateTime scheduledAt) {}

    @PostMapping("/{id}/reschedule")
    public ResponseEntity<Appointment> reschedule(@PathVariable Long id,
                                                  @RequestBody RescheduleRequest r) {
        return ResponseEntity.ok(service.reschedule(id, r.scheduledAt()));
    }

        @GetMapping("/doctors/{doctorId}/slots")
    public ResponseEntity<List<OffsetDateTime>> slots(@PathVariable Long doctorId,
                                                      @RequestParam String date) {
        return ResponseEntity.ok(service.availableSlots(doctorId, LocalDate.parse(date)));
    }
}