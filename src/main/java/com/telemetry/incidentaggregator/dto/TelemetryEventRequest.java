package com.telemetry.incidentaggregator.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record TelemetryEventRequest(
        @NotBlank
        String service,

        @NotNull
        Instant timestamp,

        @NotNull
        @DecimalMin("0.0")
        @DecimalMax("100.0")
        Double cpu,

        @NotNull
        @DecimalMin("0.0")
        @DecimalMax("100.0")
        Double memory,

        @NotNull
        @DecimalMin("0.0")
        Double latencyMs
) {
}
