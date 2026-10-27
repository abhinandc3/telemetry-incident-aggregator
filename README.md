# Telemetry Incident Aggregator

A minimal Spring Boot service that accepts telemetry events and maintains per-service in-memory health aggregates.

## Requirements

- Java 17 or later
- Maven 3.6.3 or later
- Docker (optional)

## Run

```bash
mvn spring-boot:run
```

The server listens on port 8080 by default. Set `PORT` to use another port:

```bash
PORT=9090 mvn spring-boot:run
```

## Test

```bash
mvn test
```

## API

### Ingest telemetry

`POST /api/v1/telemetry` accepts JSON and returns `204 No Content` on success. `service` must be nonblank, `timestamp` is required, `cpu` and `memory` must be between 0 and 100, and `latencyMs` must be nonnegative.

```bash
curl -i -X POST http://localhost:8080/api/v1/telemetry \
  -H 'Content-Type: application/json' \
  -d '{"service":"payment-service","timestamp":"2026-10-26T16:30:00Z","cpu":82.5,"memory":71.2,"latencyMs":340}'
```

### Get a service summary

`GET /api/v1/services/{service}/summary` returns aggregate counts, averages, and status. Unknown services return `404`.

```bash
curl -i http://localhost:8080/api/v1/services/payment-service/summary
```

### List unhealthy services

`GET /api/v1/services/unhealthy` returns DEGRADED and CRITICAL services, sorted by unhealthy percentage descending.

```bash
curl -i http://localhost:8080/api/v1/services/unhealthy
```

### Health check

`GET /actuator/health` returns the Actuator health status.

```bash
curl -i http://localhost:8080/actuator/health
```

## Docker

Build the image and run the container:

```bash
docker build -t telemetry-incident-aggregator .
docker run --rm -p 8080:8080 telemetry-incident-aggregator
```

To configure the application port in the container, set `PORT` and publish the matching port:

```bash
docker run --rm -e PORT=9090 -p 9090:9090 telemetry-incident-aggregator
```

The service stores aggregates in memory; restarting the process clears them.
