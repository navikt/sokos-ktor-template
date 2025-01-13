package no.nav.sokos.prosjektnavn.config

import java.io.File

import com.natpryce.konfig.ConfigurationMap
import com.natpryce.konfig.ConfigurationProperties
import com.natpryce.konfig.EnvironmentVariables
import com.natpryce.konfig.Key
import com.natpryce.konfig.overriding
import com.natpryce.konfig.stringType

object PropertiesConfig {
    private val defaultProperties =
        ConfigurationMap(
            mapOf(
                "NAIS_APP_NAME" to "sokos-ktor-template",
                "NAIS_NAMESPACE" to "okonomi",
                "USE_AUTHENTICATION" to "true",
            ),
        )

    private val localDevProperties =
        ConfigurationMap(
            mapOf(
                "APPLICATION_PROFILE" to Profile.LOCAL.toString(),
                "USE_AUTHENTICATION" to "false",
            ),
        )

    private val devProperties = ConfigurationMap(mapOf("APPLICATION_PROFILE" to Profile.DEV.toString()))
    private val prodProperties = ConfigurationMap(mapOf("APPLICATION_PROFILE" to Profile.PROD.toString()))

    private val config =
        when (System.getenv("NAIS_CLUSTER_NAME") ?: System.getProperty("NAIS_CLUSTER_NAME")) {
            "dev-gcp" -> ConfigurationProperties.systemProperties() overriding EnvironmentVariables() overriding devProperties overriding defaultProperties
            "prod-gcp" -> ConfigurationProperties.systemProperties() overriding EnvironmentVariables() overriding prodProperties overriding defaultProperties
            else ->
                ConfigurationProperties.systemProperties() overriding EnvironmentVariables() overriding
                    ConfigurationProperties.fromOptionalFile(
                        File("defaults.properties"),
                    ) overriding localDevProperties overriding defaultProperties
        }

    operator fun get(key: String): String = config[Key(key, stringType)]

    fun getOrEmpty(key: String): String = config.getOrElse(Key(key, stringType), "")

    data class Configuration(
        val naisAppName: String = get("NAIS_APP_NAME"),
        val profile: Profile = Profile.valueOf(get("APPLICATION_PROFILE")),
        val useAuthentication: Boolean = get("USE_AUTHENTICATION").toBoolean(),
        val azureAdProperties: AzureAdProperties = AzureAdProperties(),
    )

    class AzureAdProperties(
        val clientId: String = getOrEmpty("AZURE_APP_CLIENT_ID"),
        val wellKnownUrl: String = getOrEmpty("AZURE_APP_WELL_KNOWN_URL"),
    )

    enum class Profile {
        LOCAL,
        DEV,
        PROD,
    }
}
