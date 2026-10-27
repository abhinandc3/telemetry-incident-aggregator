package com.telemetry.incidentaggregator.model;

import com.telemetry.incidentaggregator.dto.TelemetryEventRequest;

public class ServiceAccumulator {
    public record Snapshot(
            long totalEvents,
            long unhealthyEvents,
            double averageCpu,
            double averageMemory,
            double averageLatencyMs,
            HealthStatus healthStatus
    ) {
    }

    private long totalEvents;
    private long unhealthyEvents;
    private double totalCpu;
    private double totalMemory;
    private double totalLatency;

    public synchronized void addEvent(TelemetryEventRequest event) {
        totalEvents++;
        totalCpu += event.cpu();
        totalMemory += event.memory();
        totalLatency += event.latencyMs();

        if (event.cpu() > 80 || event.memory() > 85 || event.latencyMs() > 500) {
            unhealthyEvents++;
        }
    }

    public synchronized HealthStatus getHealthStatus() {
        if (totalEvents == 0) {
            return HealthStatus.HEALTHY;
        }

        double unhealthyPercentage = (double) unhealthyEvents / totalEvents * 100;
        if (unhealthyPercentage >= 50) {
            return HealthStatus.CRITICAL;
        }
        if (unhealthyPercentage >= 20) {
            return HealthStatus.DEGRADED;
        }
        return HealthStatus.HEALTHY;
    }

    public synchronized Snapshot snapshot() {
        double divisor = totalEvents == 0 ? 1 : totalEvents;
        return new Snapshot(
                totalEvents,
                unhealthyEvents,
                totalCpu / divisor,
                totalMemory / divisor,
                totalLatency / divisor,
                getHealthStatus()
        );
    }
}
