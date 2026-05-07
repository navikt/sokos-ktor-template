# Typed Config Classes & Testing

## Typed Config Section Data Classes

Each config section is a `@Serializable data class` deserialized via Ktor's `getAs<T>()` extension.

```kotlin
enum class Profile { LOCAL, DEV, TEST, PROD }

@Serializable
data class ApplicationProperties(
    val profile: Profile,
    val appName: String,
    val namespace: String,
    val useAuthentication: Boolean,
) {
    val isLocal = profile == Profile.LOCAL
}

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

Add new `@Serializable data class` config sections as your service needs them (e.g. external API credentials, SFTP settings, scheduler config). Register each one as a `by lazy` property in `PropertiesConfig`.

## In Tests

Load a fixed test config directly from `application-test.conf`:

```kotlin
object DBListener : TestListener {
    init {
        PropertiesConfig.load(ApplicationConfig("application-test.conf"))
    }
    // ...
}
```

Mock `PropertiesConfig` with MockK when needed:

```kotlin
beforeSpec {
    mockkObject(PropertiesConfig)
    every { PropertiesConfig.config } returns ApplicationConfig("application-test.conf")
}
afterSpec {
    unmockkObject(PropertiesConfig)
}
```
