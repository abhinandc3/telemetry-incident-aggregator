# Telemetry Incident Aggregator

## Goal

Build a simple Spring Boot backend that ingests telemetry events and provides health summaries for services.

This is a time-limited coding assessment. Prefer simple, correct, testable code over unnecessary complexity.

## Tech Stack

- Java 17 target
- Spring Boot
- Maven
- Spring Web
- Jakarta Validation
- Spring Boot Actuator
- JUnit
- In-memory storage
- Docker

Base package:

`com.example.telemetry`

## API Requirements

### POST `/api/v1/telemetry`

Example:

```json
{
  "service": "payment-service",
  "timestamp": "2026-10-26T16:30:00Z",
  "cpu": 82.5,
  "memory": 71.2,
  "latencyMs": 340
}
```

Validation:

- service must not be blank
- timestamp must not be null
- cpu: 0–100
- memory: 0–100
- latencyMs >= 0

An event is unhealthy when:

```text
cpu > 80
OR memory > 85
OR latencyMs > 500
```

### GET `/api/v1/services/{service}/summary`

Return:

- service
- totalEvents
- unhealthyEvents
- averageCpu
- averageMemory
- averageLatencyMs
- status

Status:

```text
HEALTHY   < 20% unhealthy
DEGRADED  >= 20% and < 50%
CRITICAL  >= 50%
```

Return `404` for an unknown service.

### GET `/api/v1/services/unhealthy`

Return DEGRADED and CRITICAL services with:

- service
- status
- unhealthyPercentage

## Storage

Do not store individual telemetry events.

Maintain per-service aggregates:

```text
totalEvents
unhealthyEvents
totalCpu
totalMemory
totalLatency
```

Use a thread-safe in-memory structure.

Preferred approach:

```text
ConcurrentHashMap<String, ServiceAccumulator>
```

Updates to the same service must not lose data.

## Architecture

Keep the structure simple:

```text
Controller
    ↓
Service
    ↓
ConcurrentHashMap
    ↓
ServiceAccumulator
```

Suggested packages:

```text
controller/
service/
dto/
model/
exception/
```

Use Java records for simple DTOs where appropriate.

Keep controllers thin. Business logic should not live in controllers.

## Error Handling

At minimum:

- invalid request → 400
- unknown service → 404

Use `@RestControllerAdvice` if needed.

## Testing

Cover important behavior:

- healthy event
- unhealthy event
- 20% → DEGRADED
- 50% → CRITICAL
- averages
- unknown service
- request validation
- unhealthy services listing

Run:

```bash
./mvnw test
```

after meaningful changes.

## Deployment

Application must:

- build with Maven
- run with Docker
- expose `/actuator/health`
- support deployment port configuration

Use:

```properties
server.port=${PORT:8080}
management.endpoints.web.exposure.include=health
```

## Agent Instructions

When working on this repository:

1. Read this file before making changes.
2. Inspect existing code first.
3. Implement only the task requested.
4. Do not build future features unless asked.
5. Keep changes small and simple.
6. Do not introduce Kafka, Flink, ClickHouse, databases, or other infrastructure unless explicitly requested.
7. Do not add unnecessary dependencies or abstractions.
8. Run relevant tests after changes.
9. Briefly explain what changed.
10. Stop once the requested task is complete.

For production-scale discussions, Kafka → Flink → ClickHouse may be discussed, but it should not be implemented in this MVP.