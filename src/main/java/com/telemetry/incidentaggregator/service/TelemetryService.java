package com.telemetry.incidentaggregator.service;

import com.telemetry.incidentaggregator.dto.TelemetryEventRequest;
import com.telemetry.incidentaggregator.dto.ServiceSummaryResponse;
import com.telemetry.incidentaggregator.dto.UnhealthyServiceResponse;
import com.telemetry.incidentaggregator.exception.ServiceNotFoundException;
import com.telemetry.incidentaggregator.model.HealthStatus;
import com.telemetry.incidentaggregator.model.ServiceAccumulator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class TelemetryService {
    private final ConcurrentMap<String, ServiceAccumulator> services = new ConcurrentHashMap<>();

    public void ingest(TelemetryEventRequest event) {
        ServiceAccumulator accumulator = services.computeIfAbsent(
                event.service(),
                service -> new ServiceAccumulator()
        );
        accumulator.addEvent(event);
    }

    public ServiceSummaryResponse getSummary(String serviceName) {
        ServiceAccumulator accumulator = services.get(serviceName);
        if (accumulator == null) {
            throw new ServiceNotFoundException(serviceName);
        }

        ServiceAccumulator.Snapshot snapshot = accumulator.snapshot();
        return new ServiceSummaryResponse(
                serviceName,
                snapshot.totalEvents(),
                snapshot.unhealthyEvents(),
                snapshot.averageCpu(),
                snapshot.averageMemory(),
                snapshot.averageLatencyMs(),
                snapshot.healthStatus()
        );
    }

    public List<UnhealthyServiceResponse> getUnhealthyServices() {
        List<UnhealthyServiceResponse> unhealthyServices = new ArrayList<>();
        services.forEach((serviceName, accumulator) -> {
            ServiceAccumulator.Snapshot snapshot = accumulator.snapshot();
            if (snapshot.healthStatus() != HealthStatus.HEALTHY) {
                double unhealthyPercentage = snapshot.unhealthyEvents() * 100.0 / snapshot.totalEvents();
                unhealthyServices.add(new UnhealthyServiceResponse(
                        serviceName,
                        snapshot.healthStatus(),
                        unhealthyPercentage
                ));
            }
        });
        unhealthyServices.sort(Comparator.comparingDouble(UnhealthyServiceResponse::unhealthyPercentage).reversed());
        return unhealthyServices;
    }
}
