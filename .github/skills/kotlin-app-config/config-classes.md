# Typed Config Classes & Testing

## Typed Config Section Data Classes

Each config section is a `@Serializable data class` deserialized via Ktor's `getAs<T>()` extension.

The actual config classes in this project:

```kotlin
enum class Profile { LOCAL, DEV, TEST, PROD }

@Serializable
data class ApplicationProperties(
    val profile: Profile,
    val appName: String,
    val namespace: String,
    val useAuthentication: Boolean = true, // DO NOT CHANGE!
) {
    val isLocal = profile == Profile.LOCAL
    val isTest = profile == Profile.TEST
    val isProd = profile == Profile.PROD
}

@Serializable
data class AzureAdProperties(
    val clientId: String,
    val wellKnownUrl: String,
)
```

When adding a database, add a `PostgresConfig` on the same pattern:

```kotlin
@Serializable
data class PostgresConfig(
    val host: String,
    val port: String,
    val name: String,
    val username: String = "",
    val password: String = "",
) {
    val adminUser = "$name-admin"
    val user = "$name-user"
}
```

Register each new section as a `by lazy` property in `PropertiesConfig`.

## I tester

`PropertiesConfig` lastes **én gang** for hele testsuiten via Kotest `AbstractProjectConfig` — ingen manuell `beforeSpec` nødvendig:

```kotlin
// src/test/kotlin/no/nav/sokos/prosjektnavn/ProjectConfig.kt
private const val APPLICATION_TEST_CONFIG = "application-test.conf"

class ProjectConfig : AbstractProjectConfig() {
    override suspend fun beforeProject() {
        PropertiesConfig.load(ApplicationConfig(APPLICATION_TEST_CONFIG))
    }
}
```

> **NB:** `load()` er idempotent — kaller du den igjen skjer ingenting (guard `if (!::config.isInitialized)`).
> Kotest oppdager `ProjectConfig` automatisk via classpath-scanning — ingen eksplisitt registrering trengs.

### Tilleggsmønster: MockK av PropertiesConfig (nyttig ved DB/Kafka-tester)

Når du trenger full kontroll over konfig-verdier i en enkelt test:

```kotlin
beforeSpec {
    mockkObject(PropertiesConfig)
    every { PropertiesConfig.config } returns ApplicationConfig("application-test.conf")
}
afterSpec {
    unmockkObject(PropertiesConfig)
}
```

### Tilleggsmønster: DatabaseListener (legg til når DB introduseres)

```kotlin
object DBListener : TestListener {
    init {
        PropertiesConfig.load(ApplicationConfig("application-test.conf"))
    }
    // Start TestContainers, sett opp Flyway, etc.
}
```
