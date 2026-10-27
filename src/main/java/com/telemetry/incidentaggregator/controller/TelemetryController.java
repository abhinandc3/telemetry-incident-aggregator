package com.telemetry.incidentaggregator.controller;

import com.telemetry.incidentaggregator.dto.TelemetryEventRequest;
import com.telemetry.incidentaggregator.dto.ServiceSummaryResponse;
import com.telemetry.incidentaggregator.dto.UnhealthyServiceResponse;
import com.telemetry.incidentaggregator.service.TelemetryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class TelemetryController {
    private final TelemetryService telemetryService;

    public TelemetryController(TelemetryService telemetryService) {
        this.telemetryService = telemetryService;
    }

    @PostMapping("/telemetry")
    public ResponseEntity<Void> ingest(@Valid @RequestBody TelemetryEventRequest event) {
        telemetryService.ingest(event);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/services/{service}/summary")
    public ResponseEntity<ServiceSummaryResponse> getSummary(@PathVariable String service) {
        return ResponseEntity.ok(telemetryService.getSummary(service));
    }

    @GetMapping("/services/unhealthy")
    public ResponseEntity<List<UnhealthyServiceResponse>> getUnhealthyServices() {
        return ResponseEntity.ok(telemetryService.getUnhealthyServices());
    }
}
