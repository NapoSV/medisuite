package com.sv.grupo7.medisuite.dto.dashboard;

/** Local clinic hour, 0-23, with scheduled non-cancelled appointment count. */
public record OccupancySlot(int hour, long appointments) {}
