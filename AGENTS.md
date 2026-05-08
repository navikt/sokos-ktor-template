# AGENTS.md — sokos-ktor-template

Instruksjonsfil for GitHub Copilot coding agent og andre AI-agenter som jobber i dette repoet.

## Prosjektoversikt

Kotlin/Ktor backend-template for Team Motta og Beregne (namespace: `okonomi`).
Brukes som utgangspunkt for nye tjenester ved å kjøre `./setupTemplate.sh`.

- **Stack**: Kotlin + Ktor + Nais/GCP · JVM 25
- **Auth**: Azure AD JWT (`AUTHENTICATION_NAME = "azureAd"` i `SecurityConfig.kt`)
- **Metrics**: Micrometer/Prometheus (`/internal/metrics`)
- **Logging**: kotlin-logging + Logback + logstash-logback-encoder
- **Config**: `PropertiesConfig`-singleton med HOCON-lagdeling
- **Testing**: Kotest (FunSpec) + MockK + mock-oauth2-server

## Bygg og test

```bash
./gradlew build              # Bygg + test + ktlintFormat (alltid kjør dette)
./gradlew build -x test      # Bygg uten tester
./gradlew test               # Kun tester
./gradlew koverHtmlReport    # Koverrapport
```

> `ktlintFormat` kjøres automatisk som del av kompilering — hopp aldri over bygg.

## Prosjektstruktur

```
src/main/kotlin/no/nav/sokos/prosjektnavn/
├── Application.kt
├── api/                    # Ktor-routes
├── config/
│   ├── PropertiesConfig.kt # Singleton for all konfig
│   ├── SecurityConfig.kt   # JWT/Azure AD
│   ├── RoutingConfig.kt    # Route-registrering
│   ├── CommonConfig.kt     # Serialization, call-logging
│   └── HttpClientConfig.kt # Ktor HTTP-klient
├── domain/                 # Domenemodeller
├── metrics/                # Micrometer-metrikker
├── service/                # Forretningslogikk
└── util/                   # Hjelpefunksjoner

src/test/kotlin/no/nav/sokos/prosjektnavn/
├── config/                 # Konfig-tester (PropertiesConfigTest)
├── security/               # Auth-tester (SecurityTest)
├── api/                    # API-tester (FunSpec + testApplication) — legg til ved behov
├── service/unit/           # Enhetstester — legg til ved behov
└── TestUtil.kt             # Felles testhjelpere
```

## Detaljerte kodekonvensjoner

Les disse filene før du skriver kode — de er de autoritative kildene:

- **Kotlin/Ktor-mønstre** (konfig, logging, auth, idiomer): `.github/instructions/kotlin-ktor.instructions.md`
- **Testmønstre** (FunSpec, MockK, testApplication, JWT): `.github/instructions/testing.instructions.md`
- **RBAC med roller/scopes**: `.github/skills/azure-rbac-ktor/SKILL.md`
- **Ny konfig-seksjon**: `.github/skills/kotlin-app-config/SKILL.md`

## Nais-manifest

Manifestene ligger i `.nais/dev/` og `.nais/prod/`.

- Helse: `/internal/isAlive` og `/internal/isReady`
- Metrics: `/internal/metrics`
- Auth: `azure.application.enabled: true`
- `resources.limits.cpu`: **sett aldri** — kun `requests.cpu`
- `accessPolicy.inbound` og `accessPolicy.outbound`: alltid eksplisitt

## Grenser for agenten

### ✅ Alltid
- Kjør `./gradlew build` for å verifisere at koden kompilerer og tester passerer
- Bruk `PropertiesConfig` for konfig — aldri `System.getenv()` i forretningslogikk
- Bruk `TEAM_LOGS_MARKER` for all sensitiv data (PII, tokens, saksnummer)
- `FunSpec` som standard for tester — `BehaviorSpec` kun for komplekse integrasjonstester
- Les `.github/instructions/`-filene for mønstre før du skriver kode

### 🚫 Aldri
- Logg PII/fnr uten `TEAM_LOGS_MARKER`
- `System.getenv()` i forretningslogikk
- `runBlocking` eller `GlobalScope` i produksjonskode eller testblokker
- `!!` uten forutgående null-sjekk
- CPU-limit i Nais-manifest
- ORM-rammeverk (Exposed, Hibernate) — bruk navikt/kotliquery
- DI-rammeverk (Koin, Spring) — bruk manuell konstruktørinjektion
- Commit `defaults.properties` eller hemmeligheter

## Relevante skills

| Skill | Bruk når |
|-------|----------|
| `backend-vulnerabilities` | GitHub Security-varsel, Trivy/CodeQL-funn, Dependabot-PR |
| `azure-rbac-ktor` | Legge til RBAC (roller/scopes) på endepunkter |
| `kotest` | Skrive eller endre tester |
| `kotlin-app-config` | Legge til ny konfig-seksjon |
| `kotlin-patterns` | Usikker på idiomatisk Kotlin |

