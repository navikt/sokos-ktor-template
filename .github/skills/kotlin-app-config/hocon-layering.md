# HOCON Layering & Example Config Files

## Layered HOCON Pattern

Config files are layered at startup via the top-level `loadEnv()` function in `PropertiesConfig.kt`:
1. `application.conf` – base defaults (shared across all environments)
2. `application-{local|dev|prod}.conf` – environment overrides (include `application.conf`)
3. `defaults.properties` – local secrets for the LOCAL profile (**never commit this file**)

Environment is detected via `NAIS_CLUSTER_NAME`. Use `loadEnv()` — not `environment.config`:

```kotlin
fun loadEnv(): ApplicationConfig {
    val environment =
        (System.getenv("NAIS_CLUSTER_NAME") ?: System.getProperty("NAIS_CLUSTER_NAME"))
            ?.lowercase()
            ?.substringBefore("-") ?: "local"

    val fileConfig =
        when {
            environment == "local" -> {
                val defaultPropertiesConfig = ConfigFactory.parseFile(File("defaults.properties"))
                ConfigFactory.parseResources("application-local.conf").withFallback(defaultPropertiesConfig)
            }
            else -> {
                ConfigFactory.parseResources("application-$environment.conf")
            }
        }

    val base =
        ConfigFactory
            .systemEnvironment()
            .withFallback(ConfigFactory.systemProperties())
            .withFallback(fileConfig)

    return HoconApplicationConfig(base.resolve())
}
```

Call once at startup — never in business logic:

```kotlin
private fun Application.module() {
    PropertiesConfig.load(loadEnv())
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

**`application-local.conf`** (local development — includes dev, overrides profile and auth):
```hocon
include "application-dev.conf"

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
