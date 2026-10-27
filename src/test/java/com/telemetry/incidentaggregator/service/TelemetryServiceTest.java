package com.telemetry.incidentaggregator.service;

import com.telemetry.incidentaggregator.dto.ServiceSummaryResponse;
import com.telemetry.incidentaggregator.dto.TelemetryEventRequest;
import com.telemetry.incidentaggregator.dto.UnhealthyServiceResponse;
import com.telemetry.incidentaggregator.exception.ServiceNotFoundException;
import com.telemetry.incidentaggregator.model.HealthStatus;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TelemetryServiceTest {

    private final TelemetryService telemetryService = new TelemetryService();

    @Test
    void ingestsTelemetryAndReportsHealthyStatus() {
        telemetryService.ingest(event("payments", 50, 60, 100));

        ServiceSummaryResponse summary = telemetryService.getSummary("payments");

        assertThat(summary.totalEvents()).isEqualTo(1);
        assertThat(summary.unhealthyEvents()).isZero();
        assertThat(summary.status()).isEqualTo(HealthStatus.HEALTHY);
    }

    @Test
    void exactlyTwentyPercentUnhealthyIsDegraded() {
        telemetryService.ingest(event("payments", 81, 60, 100));
        for (int i = 0; i < 4; i++) {
            telemetryService.ingest(event("payments", 80, 85, 500));
        }

        assertThat(telemetryService.getSummary("payments").status()).isEqualTo(HealthStatus.DEGRADED);
    }

    @Test
    void exactlyFiftyPercentUnhealthyIsCritical() {
        telemetryService.ingest(event("payments", 81, 60, 100));
        telemetryService.ingest(event("payments", 80, 85, 500));

        assertThat(telemetryService.getSummary("payments").status()).isEqualTo(HealthStatus.CRITICAL);
    }

    @Test
    void calculatesAveragesAcrossEvents() {
        telemetryService.ingest(event("payments", 40, 20, 100));
        telemetryService.ingest(event("payments", 60, 80, 200));

        ServiceSummaryResponse summary = telemetryService.getSummary("payments");

        assertThat(summary.averageCpu()).isEqualTo(50);
        assertThat(summary.averageMemory()).isEqualTo(50);
        assertThat(summary.averageLatencyMs()).isEqualTo(150);
    }

    @Test
    void throwsWhenServiceIsUnknown() {
        assertThatThrownBy(() -> telemetryService.getSummary("missing"))
                .isInstanceOf(ServiceNotFoundException.class)
                .hasMessage("Service not found: missing");
    }

    @Test
    void listsOnlyUnhealthyServicesSortedByPercentageDescending() {
        telemetryService.ingest(event("degraded", 81, 60, 100));
        for (int i = 0; i < 4; i++) {
            telemetryService.ingest(event("degraded", 80, 85, 500));
        }
        telemetryService.ingest(event("critical", 81, 60, 100));
        telemetryService.ingest(event("critical", 81, 60, 100));
        telemetryService.ingest(event("healthy", 80, 85, 500));

        List<UnhealthyServiceResponse> unhealthyServices = telemetryService.getUnhealthyServices();

        assertThat(unhealthyServices)
                .extracting(UnhealthyServiceResponse::service)
                .containsExactly("critical", "degraded");
        assertThat(unhealthyServices.get(0).unhealthyPercentage()).isEqualTo(100);
        assertThat(unhealthyServices.get(1).unhealthyPercentage()).isEqualTo(20);
    }

    private static TelemetryEventRequest event(String service, double cpu, double memory, double latencyMs) {
        return new TelemetryEventRequest(
                service,
                Instant.parse("2026-10-26T16:30:00Z"),
                cpu,
                memory,
                latencyMs
        );
    }
}
