---
applyTo: "**"
---

# sokos-ktor-template

Kotlin/Ktor backend-template for Team Motta og Beregne (`okonomi`-namespace på Nais/GCP).
Stack: Ktor · Azure AD JWT · Micrometer/Prometheus · Kotest · Gradle Kotlin DSL · JVM 25

## Fil-spesifikke instruksjoner

Copilot laster detaljerte regler automatisk basert på filtype:
- **Kotlin-kode**: `.github/instructions/kotlin-ktor.instructions.md`
- **Tester**: `.github/instructions/testing.instructions.md`

## Kritiske regler — gjelder alltid

- Logg aldri PII (fnr, navn, adresse) uten `TEAM_LOGS_MARKER`
- All konfig via `PropertiesConfig` — aldri `System.getenv()` i forretningslogikk
- Aldri `runBlocking` i produksjonskode eller testblokker
- Aldri `!!` uten forutgående null-sjekk
- Aldri `resources.limits.cpu` i Nais-manifest
- Kjør `./gradlew build` etter alle endringer for å verifisere

