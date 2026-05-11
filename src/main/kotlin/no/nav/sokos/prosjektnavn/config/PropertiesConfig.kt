package no.nav.sokos.prosjektnavn.config

import java.io.File

import kotlinx.serialization.Serializable

import com.typesafe.config.ConfigFactory
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.config.HoconApplicationConfig
import io.ktor.server.config.getAs

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

    fun load(applicationConfig: ApplicationConfig) {
        if (!::config.isInitialized) {
            config = applicationConfig
        }
    }
}

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

enum class Profile {
    LOCAL,
    TEST,
    DEV,
    PROD,
}

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
