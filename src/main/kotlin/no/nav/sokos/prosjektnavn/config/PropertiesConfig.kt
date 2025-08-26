package no.nav.sokos.prosjektnavn.config

import java.io.File

import kotlinx.serialization.Serializable

import com.typesafe.config.ConfigFactory
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.config.HoconApplicationConfig
import io.ktor.server.config.getAs

object PropertiesConfig {
    private var envConfig: HoconApplicationConfig = HoconApplicationConfig(ConfigFactory.empty())

    fun initEnvConfig(applicationConfig: ApplicationConfig? = null) {
        val environment = System.getenv("APPLICATION_ENV") ?: System.getProperty("APPLICATION_ENV")
        val config =
            when {
                environment == null || environment.lowercase() == "local" -> {
                    val defaultConfig = ConfigFactory.parseFile(File("defaults.properties"))
                    ConfigFactory.parseResources("application-local.conf").withFallback(defaultConfig)
                }

                else -> ConfigFactory.parseResources("application-${environment.lowercase()}.conf")
            }

        envConfig = applicationConfig?.let { external ->
            HoconApplicationConfig(config.withFallback(ConfigFactory.parseMap(external.toMap())).resolve())
        } ?: HoconApplicationConfig(config.resolve())
    }

    fun getApplicationProperties(): ApplicationProperties = envConfig.property("application").getAs<ApplicationProperties>()

    fun getH2Properties(): H2Properties? = envConfig.propertyOrNull("application.h2")?.getAs<H2Properties>()

    fun getPostgresProperties(): PostgresProperties? = envConfig.propertyOrNull("application.postgres")?.getAs<PostgresProperties>()

    fun getOrEmpty(key: String): String = envConfig.propertyOrNull(key)?.getString() ?: ""

    data class AzureAdProperties(
        val clientId: String = getOrEmpty("AZURE_APP_CLIENT_ID"),
        val wellKnownUrl: String = getOrEmpty("AZURE_APP_WELL_KNOWN_URL"),
        val tenantId: String = getOrEmpty("AZURE_APP_TENANT_ID"),
        val clientSecret: String = getOrEmpty("AZURE_APP_CLIENT_SECRET"),
    )

    @Serializable
    data class ApplicationProperties(
        val appName: String,
        val environment: Environment,
        val useAuthentication: Boolean,
        val databaseType: DatabaseType,
    )

    @Serializable
    data class PostgresProperties(
        val host: String,
        val port: String,
        val name: String,
        val username: String,
        val password: String,
    )

    @Serializable
    data class H2Properties(
        val jdbcUrl: String,
        val username: String,
        val password: String,
    )

    enum class Environment {
        LOCAL,
        DEV,
        TEST,
        PROD,
    }

    enum class DatabaseType {
        H2,
        POSTGRES,
    }
}
