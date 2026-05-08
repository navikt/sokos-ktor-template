# HOCON Layering & Example Config Files

## Layered HOCON Pattern

Config files are layered at startup via `mergeWithEnv()`:
1. `application.conf` – base defaults, references `defaults.properties`
2. `application-{local|dev|prod}.conf` – environment overrides
3. `defaults.properties` – local secrets (**never commit this file**)

Environment is detected via `NAIS_CLUSTER_NAME`:

```kotlin
fun ApplicationConfig.mergeWithEnv(): ApplicationConfig {
    val hoconConfig = HoconApplicationConfig(ConfigFactory.load())
    val environment =
        (System.getenv("NAIS_CLUSTER_NAME") ?: System.getProperty("NAIS_CLUSTER_NAME"))
            ?.lowercase()
            ?.substringBefore("-")
            ?: propertyOrNull("ktor.environment")?.getString()
            ?: "local"
    val environmentConfig = ApplicationConfig("application-$environment.conf")
    return this overriding environmentConfig overriding hoconConfig
}

infix fun ApplicationConfig.overriding(other: ApplicationConfig): ApplicationConfig =
    this.withFallback(other)
```

## Example HOCON Files

**`application.conf`** (base):
```hocon
include file("defaults.properties")

ktor {
  environment = local
}

application {
  appName = "my-app"
  appName = ${?NAIS_APP_NAME}
  namespace = "okonomi"
  useAuthentication = true
}

azureAd {
  clientId = ${?AZURE_APP_CLIENT_ID}
  wellKnownUrl = ${?AZURE_APP_WELL_KNOWN_URL}
}
```

**`application-local.conf`** (local overrides):
```hocon
include file("application.conf")

application {
  profile = LOCAL
  useAuthentication = false
}

azureAd {
  clientId = "local-client-id"
  wellKnownUrl = "http://localhost:8080/default/.well-known/openid-configuration"
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
