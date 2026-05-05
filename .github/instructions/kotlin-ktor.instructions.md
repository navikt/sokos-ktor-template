---
applyTo: "**/config/**/*.kt,**/api/**/*.kt,**/security/**/*.kt,**/metrics/**/*.kt,**/frontend/**/*.kt,**/dto/**/*.kt,**/domain/**/*.kt,**/util/**/*.kt"
---

# Kotlin/Ktor general patterns

Ktor backend service on NAIS (not Rapids & Rivers, not Spring Boot). For tests see `testing.instructions.md` and the `kotest` skill.

## Configuration

All config access via `PropertiesConfig` singleton. See the `kotlin-app-config` skill for full HOCON layering and `@Serializable` data-class pattern.

```kotlin
val appName = PropertiesConfig.Configuration().naisAppName
```

## Ktor routing

Health/metrics endpoints are unauthenticated; domain routes sit behind `authenticate(AUTHENTICATION_NAME)`. Expose: `/internal/isAlive`, `/internal/isReady`, `/internal/metrics`.

## Logging

- Regular messages → Logback → Grafana Loki.
- Sensitive data (PII, case numbers, request/response bodies, tokens) → **must** use `TEAM_LOGS_MARKER`:

```kotlin
logger.error(marker = TEAM_LOGS_MARKER) { "Error for case: $caseId" }
```

## Metrics

Use the `Metrics` object (Micrometer `PrometheusMeterRegistry`). Define a namespace constant matching the app name (e.g. `sokos_my_app`). Register counters via `Counter.builder()`.

## Boundaries

### ✅ Always
- `PropertiesConfig` for config — never `System.getenv()` in business logic
- `TEAM_LOGS_MARKER` for sensitive data

### 🚫 Never
- Commit `defaults.properties`
- Log PII/request bodies without `TEAM_LOGS_MARKER`
- Use `!!` without a preceding null check
