package com.sv.grupo7.medisuite.controller.api;

import com.sv.grupo7.medisuite.model.medical.Appointment;
import com.sv.grupo7.medisuite.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService service;

    public record CreateRequest(Long patientId, Long doctorId,
                                OffsetDateTime scheduledAt, String reason) {}

    @GetMapping
    public ResponseEntity<List<Appointment>> list(
            @RequestParam(defaultValue = "false") boolean upcoming) {
        return ResponseEntity.ok(upcoming ? service.upcoming() : service.upcoming());
    }

    @PostMapping
    public ResponseEntity<Appointment> create(@RequestBody CreateRequest r) {
        Appointment a = service.create(r.patientId(), r.doctorId(),
                                       r.scheduledAt(), r.reason());
        return ResponseEntity.status(201).body(a);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Map<String,String>> cancel(@PathVariable Long id) {
        service.cancel(id);
        return ResponseEntity.ok(Map.of("status", "CANCELLED"));
    }

        @GetMapping("/doctors/{doctorId}/slots")
    public ResponseEntity<List<OffsetDateTime>> slots(@PathVariable Long doctorId,
                                                      @RequestParam String date) {
        return ResponseEntity.ok(service.availableSlots(doctorId, LocalDate.parse(date)));
    }
}