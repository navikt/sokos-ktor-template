---
name: kotlin-app-config
description: "HOCON-based configuration with PropertiesConfig singleton for Ktor services on NAIS. Use when adding config sections, environment layering, or typed config classes. Accepts prompts in Norwegian and English. (Konfigurasjon, miljøvariabler, HOCON, PropertiesConfig, oppsett)"
---

# Kotlin Application Configuration Skill

This skill describes the HOCON + `PropertiesConfig` singleton pattern for Ktor services on NAIS.
Config is loaded from layered HOCON files via Ktor's `ApplicationConfig` API.

## PropertiesConfig Singleton

`PropertiesConfig` is a Kotlin `object` that holds all typed config sections as lazy properties.
Call `PropertiesConfig.load(config)` once at startup; never re-initialize.

```kotlin
private const val APPLICATION_CONF = "application"
private const val AZUREAD_CONF = "azureAd"

object PropertiesConfig {
    lateinit var config: ApplicationConfig
        private set

    val applicationProperties by lazy {
        config.property(APPLICATION_CONF).getAs<ApplicationProperties>()
    }

    val azureAdProperties by lazy {
        config.property(AZUREAD_CONF).getAs<AzureAdProperties>()
    }

    // Add new config sections here as lazy properties
    // Example when adding a database:
    // val postgresConfig by lazy {
    //     config.property("postgres").getAs<PostgresConfig>()
    // }

    fun load(applicationConfig: ApplicationConfig) {
        if (!::config.isInitialized) {
            config = applicationConfig
        }
    }

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
}

// overriding infix — brukes i loadEnv() for lesbar konfig-lagdeling
infix fun ApplicationConfig.overriding(other: ApplicationConfig): ApplicationConfig = this.withFallback(other)
```

## Usage at Startup

```kotlin
private fun Application.module() {
    PropertiesConfig.load(environment.config.loadEnv())

    val useAuthentication = PropertiesConfig.applicationProperties.useAuthentication

    // ...
}
```

## Sub-files

- See [hocon-layering.md](hocon-layering.md) for the layered HOCON pattern (`ApplicationConfig.loadEnv()` extension function) and example HOCON config files.
- See [config-classes.md](config-classes.md) for typed config section data classes (`@Serializable`) and testing configuration with MockK.

## Benefits

- **Single source of truth**: All config in one singleton, no passing config objects around
- **Lazy initialization**: Config sections only parsed when first accessed
- **Type safety**: `@Serializable data class` properties with compile-time field names
- **Environment layering**: Local overrides without touching base config
- **NAIS-native**: Aligns with the HOCON layering convention used across NAV services
