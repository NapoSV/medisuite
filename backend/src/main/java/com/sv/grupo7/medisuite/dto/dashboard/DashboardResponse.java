package com.sv.grupo7.medisuite.dto.dashboard;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/** Null means that the role is not authorized for the metric; null fields are omitted from JSON. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record DashboardResponse(
        Long appointmentsToday,
        Long activePatients,
        Long prescriptionsIssued,
        Long alerts,
        Long prescriptionsThisWeek,
        Long criticalAlerts,
        List<WaitingRoomEntry> waitingRoom,
        List<OccupancySlot> occupancyByHour,
        List<CriticalAlert> clinicalAlerts
) {}
