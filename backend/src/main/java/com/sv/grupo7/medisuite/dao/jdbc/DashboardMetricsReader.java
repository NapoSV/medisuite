package com.sv.grupo7.medisuite.dao.jdbc;

import com.sv.grupo7.medisuite.dto.dashboard.DashboardQuery;
import com.sv.grupo7.medisuite.dto.dashboard.DashboardResponse;

/** Temporary B1 read contract; B4 owns the final service.port.MetricsPort. */
public interface DashboardMetricsReader {
    DashboardResponse read(DashboardQuery query);
}
