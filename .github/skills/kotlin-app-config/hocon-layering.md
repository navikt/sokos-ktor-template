# HOCON Layering & Example Config Files

## Layered HOCON Pattern

Config files are layered at startup via `ApplicationConfig.loadEnv()` — an extension function defined in `PropertiesConfig.kt`:
1. `application.conf` – base defaults (shared across all environments)
2. `application-{local|dev|prod}.conf` – environment overrides (include `application.conf`)
3. `defaults.properties` – local secrets for the LOCAL profile, loaded via `include file(...)` in `application-local.conf` (**never commit this file**)

Environment is detected via `NAIS_CLUSTER_NAME`. Call via `environment.config.loadEnv()` at startup — never use `environment.config` directly or `System.getenv()` in business logic:

```kotlin
fun ApplicationConfig.loadEnv(): ApplicationConfig {
    val hoconConfig = HoconApplicationConfig(ConfigFactory.load())
    val environmentName = System.getenv("NAIS_CLUSTER_NAME") ?: System.getProperty("NAIS_CLUSTER_NAME")
    val environment = environmentName?.lowercase()?.substringBefore("-") ?: "local"

    val environmentConfig = ApplicationConfig("application-$environment.conf")
    return environmentConfig overriding this overriding hoconConfig
}

infix fun ApplicationConfig.overriding(other: ApplicationConfig): ApplicationConfig = this.withFallback(other)
```

The three-way merge `environmentConfig overriding this overriding hoconConfig` means:
- `environmentConfig` wins over `this` (Ktor's default config) which wins over `hoconConfig` (base)

Call once at startup — never in business logic:

```kotlin
private fun Application.module() {
    PropertiesConfig.load(environment.config.loadEnv())
    // ...
}
```

## Example HOCON Files

**`application.conf`** (base — shared values, env vars via `${?ENV_VAR}`):
```hocon
application {
  appName = "my-app"
  appName = ${?NAIS_APP_NAME}
  namespace = "okonomi"
  namespace = ${?NAIS_NAMESPACE}
  useAuthentication = true
}

azureAd {
  clientId = ""
  clientId = ${?AZURE_APP_CLIENT_ID}
  wellKnownUrl = ""
  wellKnownUrl = ${?AZURE_APP_WELL_KNOWN_URL}
}
```

**`application-dev.conf`** (dev environment):
```hocon
include "application.conf"

application {
  profile = DEV
}
```

**`application-local.conf`** (local development — includes dev, overrides profile and auth, loads secrets via `include file(...)`):
```hocon
include "application-dev.conf"
include file("defaults.properties")

application {
  profile = LOCAL
  useAuthentication = false
}
```

**`application-test.conf`** (used in tests — includes base, overrides profile):
```hocon
include "application.conf"

application {
  profile = TEST
}

azureAd {
  clientId = "default"
  wellKnownUrl = ""
}
```

When adding a database, add a `postgres` section on the same pattern:

```hocon
postgres {
  name = "my-app"
  username = ${?POSTGRES_USERNAME}
  password = ${?POSTGRES_PASSWORD}
}
```
