package no.nav.sokos.prosjektnavn.config

import kotlinx.serialization.Serializable

import com.typesafe.config.ConfigFactory
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.config.HoconApplicationConfig
import io.ktor.server.config.withFallback

@Serializable
data class AppConfig(
    val profile: String,
    val appName: String,
    val namespace: String,
    val properties: ApplicationProperties,
) {
    val currentProfile: Profile = Profile.from(profile)
}

@Serializable
data class ApplicationProperties(
    val security: SecurityProperties,
    val database: DatabaseProperties,
)

@Serializable
data class SecurityProperties(
    val azure: AzureAdProperties,
)

@Serializable
data class AzureAdProperties(
    val clientId: String,
    val wellKnownUrl: String,
    val enabled: Boolean,
)

@Serializable
data class DatabaseProperties(
    val name: String,
    val host: String,
    val port: String,
    val username: String,
    val password: String,
    val vaultMountPath: String,
) {
    val adminUser = "$name-admin"
    val user = "$name-user"
}

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

    return this overriding ApplicationConfig("application-$environment.conf")
}

infix fun ApplicationConfig.overriding(other: ApplicationConfig): ApplicationConfig = this.withFallback(other)
