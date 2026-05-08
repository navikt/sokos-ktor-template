---
applyTo: "src/main/**/*.kt"
---

# Kotlin/Ktor — mønstre og regler

Ktor backend-tjeneste på NAIS (ikke Spring Boot, ikke Rapids & Rivers). For tester, se `testing.instructions.md` og `kotest`-skillen.

## Konfig — PropertiesConfig

All konfig via `PropertiesConfig`-singleton. Aldri `System.getenv()` i forretningslogikk.

```kotlin
val appName = PropertiesConfig.applicationProperties.appName
val azureConfig = PropertiesConfig.azureAdProperties
```

Nye seksjoner legges til som `lazy`-property med `@Serializable data class`. Se `kotlin-app-config`-skillen.

## Logging

- Vanlige meldinger: `private val logger = KotlinLogging.logger {}`
- Sensitiv data (PII, saksnummer, tokens, request/response bodies): **alltid** `TEAM_LOGS_MARKER`

```kotlin
logger.error(marker = TEAM_LOGS_MARKER) { "Feil for sak: $sakId" }
```

## Ktor-routes og auth

- Interne endepunkter (`/internal/isAlive`, `/internal/isReady`, `/internal/metrics`): **uautentiserte**
- Alle domene-routes: `authenticate(useAuthentication, AUTHENTICATION_NAME)`
- RBAC med roller/scopes: bruk `azure-rbac-ktor`-skillen

## Metrics

Bruk `Metrics`-objektet (`PrometheusMeterRegistry`). Definer namespace som matcher appnavnet (f.eks. `sokos_my_app`). Registrer tellere via `Counter.builder()`.

## Kotlin-idiomer

- `val` fremfor `var` — immutabilitet som standard
- `?.` / `?:` / `requireNotNull` — aldri `!!` uten null-sjekk
- `sealed class/interface` for domenefeil og resultattyper
- `suspend` + `coroutineScope`/`async` for strukturert concurrency — aldri `runBlocking` eller `GlobalScope`
- Manuell konstruktørinjektion — aldri DI-rammeverk (Koin, Spring)
- navikt/kotliquery for databasetilgang — aldri ORM (Exposed, Hibernate)

## Nais

- Aldri `resources.limits.cpu` — kun `requests.cpu`
- `accessPolicy.inbound` og `accessPolicy.outbound` alltid eksplisitt
- Hemmeligheter via Nais secrets — aldri hardkodet

## Boundaries

### ✅ Always
- `PropertiesConfig` for all konfig
- `TEAM_LOGS_MARKER` for sensitiv data
- `suspend` + strukturert concurrency for async

### 🚫 Never
- `System.getenv()` i forretningslogikk
- `!!` uten forutgående null-sjekk
- `runBlocking` eller `GlobalScope.launch`
- ORM-rammeverk eller DI-rammeverk
- PII i logger uten `TEAM_LOGS_MARKER`
- Hardkodede hemmeligheter eller `defaults.properties` i commits

