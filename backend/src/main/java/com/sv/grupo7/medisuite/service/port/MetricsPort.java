package com.sv.grupo7.medisuite.service.port;

import com.sv.grupo7.medisuite.dto.dashboard.DashboardQuery;
import com.sv.grupo7.medisuite.dto.dashboard.DashboardResponse;

public interface MetricsPort {
    DashboardResponse getMetrics(DashboardQuery query);
}