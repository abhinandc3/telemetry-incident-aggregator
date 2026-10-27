package com.telemetry.incidentaggregator.exception;

public class ServiceNotFoundException extends RuntimeException {
    public ServiceNotFoundException(String serviceName) {
        super("Service not found: " + serviceName);
    }
}
