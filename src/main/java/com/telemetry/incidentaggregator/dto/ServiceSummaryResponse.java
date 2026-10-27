package com.telemetry.incidentaggregator.dto;

import com.telemetry.incidentaggregator.model.HealthStatus;

public record ServiceSummaryResponse(
        String service,
        long totalEvents,
        long unhealthyEvents,
        double averageCpu,
        double averageMemory,
        double averageLatencyMs,
        HealthStatus status
) {
}
