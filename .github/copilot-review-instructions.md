---
applyTo: "**"
---

# Copilot Code Review — sokos-ktor-template

Instruksjoner for hva Copilot skal fokusere på ved gjennomgang av pull requests.

## 🔴 Blokkerende — flagg alltid

### Sikkerhet og personvern
- **PII i logger uten `TEAM_LOGS_MARKER`** — fnr, navn, adresse, saksnummer, tokens må aldri logges i klartekst
- **Hemmeligheter i kode** — ingen hardkodede credentials, klienthemmeligheter eller API-nøkler
- **`System.getenv()` i forretningslogikk** — all konfig skal gå via `PropertiesConfig`
- **Uautentiserte domene-endepunkter** — alle routes utenfor `/internal/*` må være wrappet i `authenticate(...)`

### Kotlin
- **`!!` uten forutgående null-sjekk** — bruk `?.`, `?:` eller `requireNotNull` med melding
- **`runBlocking` i produksjonskode eller testblokker** — bruk `suspend` og `coroutineScope`
- **`GlobalScope.launch`** — bruk strukturert concurrency

### Nais
- **`resources.limits.cpu` i Nais-manifest** — CPU-grenser skal aldri settes (throttling)
- **Manglende `accessPolicy.inbound`** — tjenester som skal kalles må ha eksplisitte inbound-regler

## ⚠️ Advarsel — kommenter og forklar

### Konfig
- `PropertiesConfig` brukt riktig? Nye seksjoner bør følge `lazy`-property + `@Serializable data class`-mønsteret
- Ny HOCON-konfig lastes via `environment.config.loadEnv()` i `Application.module()` — ikke via `System.getenv()` direkte
- Nye HOCON-felter uten tilhørende `@Serializable data class` — konfig bør være typesikker

### Testing
- `testApplication { }` brukt på service/repository i stedet for API-routes — bruk direkte instansiering med MockK
- API-tester (DummyApiTest) skal bruke `embeddedServer(Netty, PORT)` + RestAssured + `OpenApiValidationFilter` — ikke `testApplication { }` for dette formålet
- Sikkerhetstester (SecurityTest) skal bruke `testApplication { }` med `withMockOAuth2Server`
- Manglende `coEvery`/`coVerify` for suspend-funksjoner — `every`/`verify` fungerer ikke korrekt her
- Delt muterbar state mellom test-scenarier — legg til reset i `beforeEach`
- `FunSpec` er standard — `BehaviorSpec` kun for komplekse integrasjonstester med mange kontekster

### Arkitektur
- Ny forretningslogikk direkte i route-handler — flytt til service-klasse
- Ekte HTTP-kall i tester — mock med MockK
- ORM-rammeverk (Exposed, Hibernate) uten eksplisitt godkjenning — bruk navikt/kotliquery
- DI-rammeverk (Koin, Spring) uten eksplisitt godkjenning — bruk manuell konstruktørinjektion

### Nais
- `resources.limits.memory` over 1Gi uten begrunnelse
- Nye utgående avhengigheter uten tilsvarende `accessPolicy.outbound.rules`

## ✅ Godkjent praksis — ikke flagg

- `FunSpec` for triviell konfig- og sikkerhetstesting (SecurityTest, PropertiesConfigTest)
- `TEAM_LOGS_MARKER` på sensitiv logging — dette er korrekt
- `requireNotNull` med lambda-melding
- `lazy`-delegering i `PropertiesConfig`
- `withFallback` / `overriding` for HOCON-lagdeling
