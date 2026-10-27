package com.telemetry.incidentaggregator.dto;

import com.telemetry.incidentaggregator.model.HealthStatus;

public record UnhealthyServiceResponse(
        String service,
        HealthStatus status,
        double unhealthyPercentage
) {
}
