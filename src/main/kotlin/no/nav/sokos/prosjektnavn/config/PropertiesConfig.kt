package no.nav.sokos.prosjektnavn.config

import kotlinx.serialization.Serializable

import com.typesafe.config.ConfigFactory
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.config.HoconApplicationConfig
import io.ktor.server.config.withFallback

@Serializable
data class ApplicationProperties(
    val profile: String,
    val appName: String,
    val namespace: String,
    val configuration: AppConfig,
)

@Serializable
data class AppConfig(
    val security: SecurityProperties,
    val database: PostgresProperties,
)

@Serializable
data class SecurityProperties(
    val azure: AzureAdProperties,
    val vault: VaultProperties,
)

@Serializable
data class VaultProperties(
    val mountpath: String,
)

@Serializable
data class AzureAdProperties(
    val clientId: String,
    val wellKnownUrl: String,
    val enabled: Boolean,
)

@Serializable
data class PostgresProperties(
    val name: String,
    val host: String,
    val port: String,
    val username: String,
    val password: String,
)

enum class Profile(
    val isLocal: Boolean = false,
) {
    LOCAL(true),
    TEST(true),
    DEV(false),
    ;

    companion object {
        private val profilesByName = Profile.entries.associateBy { it.name.lowercase() }

        fun from(env: String): Profile =
            profilesByName[env.lowercase()]
                ?: throw IllegalArgumentException("Unknown environment profile: $env")
    }
}

fun ApplicationConfig.mergeWithEnv(): ApplicationConfig {
    val hoconConfig = HoconApplicationConfig(ConfigFactory.load())
    val environment =
        (System.getenv("CLUSTER_NAME") ?: System.getProperty("CLUSTER_NAME"))
            ?.lowercase()
            ?.substringBefore("-")
            ?: propertyOrNull("ktor.environment")?.getString()
            ?: hoconConfig.propertyOrNull("ktor.environment")?.getString()
            ?: "test"

    return this overriding ApplicationConfig("application-$environment.conf") overriding hoconConfig
}

infix fun ApplicationConfig.overriding(other: ApplicationConfig): ApplicationConfig = this.withFallback(other)
