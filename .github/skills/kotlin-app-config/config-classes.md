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
)

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

Last konfig i `beforeSpec`:

```kotlin
beforeSpec {
    PropertiesConfig.load(ApplicationConfig(TestUtil.APPLICATION_TEST_CONFIG))
}
```

> **NB:** `load()` er idempotent — kaller du den igjen skjer ingenting (guard `if (!::config.isInitialized)`).

### Tilleggsmønster: MockK av PropertiesConfig (nyttig ved DB/Kafka-tester)

Når du trenger full kontroll over konfig-verdier i en enkelt test:

```kotlin
beforeSpec {
    mockkObject(PropertiesConfig)
    every { PropertiesConfig.config } returns ApplicationConfig(TestUtil.APPLICATION_TEST_CONFIG)
}
afterSpec {
    unmockkObject(PropertiesConfig)
}
```

### Tilleggsmønster: DatabaseListener (legg til når DB introduseres)

```kotlin
object DBListener : TestListener {
    init {
        PropertiesConfig.load(ApplicationConfig(TestUtil.APPLICATION_TEST_CONFIG))
    }
    // Start TestContainers, sett opp Flyway, etc.
}
```
