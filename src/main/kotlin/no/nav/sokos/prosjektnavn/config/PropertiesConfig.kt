package no.nav.sokos.prosjektnavn.config

import io.ktor.server.config.ApplicationConfig

import no.nav.sokos.prosjektnavn.config.ConfigurationUtils.determineRunEnv
import no.nav.sokos.prosjektnavn.config.ConfigurationUtils.get

object PropertiesConfig {
    private var _configuration: Configuration? = null
    private var _environment: Environment? = null

    val configuration: Configuration
        get() = _configuration ?: throw IllegalStateException("Configuration not initialized")

    val environment: Environment
        get() = _environment ?: throw IllegalStateException("Environment not initialized")

    enum class Profile { LOCAL, TEST, DEV, PROD, UNKNOWN }

    data class Environment(
        val profile: Profile,
        val environmentConfig: ApplicationConfig,
    ) {
        constructor(source: ApplicationConfig) : this(
            profile = source.determineRunEnv(),
            environmentConfig =
                when (source.determineRunEnv()) {
                    Profile.TEST -> ApplicationConfig("application-test.conf")
                    Profile.LOCAL -> ApplicationConfig("application-dev-local.conf")
                    Profile.DEV -> ApplicationConfig("application-dev.conf")
                    Profile.PROD -> ApplicationConfig("application-prod.conf")
                    Profile.UNKNOWN -> ApplicationConfig("application-test.conf")
                },
        ) {
            _environment = this
        }

        val isLocal: Boolean = profile == Profile.LOCAL || profile == Profile.TEST

        fun get(key: String): String? = environmentConfig.propertyOrNull(key)?.getString()
    }

    data class Configuration(
        val applicationProperties: ApplicationProperties,
        val securityProperties: SecurityProperties,
        val postgresProperties: PostgresProperties,
        val someOtherProperties: SomeOtherProperties,
        val dummyProperties: DummyProperties,
    ) {
        constructor(source: ApplicationConfig) : this(
            applicationProperties = ApplicationProperties(source),
            securityProperties = SecurityProperties(source),
            postgresProperties = PostgresProperties(source),
            dummyProperties = DummyProperties(source),
            someOtherProperties = SomeOtherProperties(source),
        ) {
            _configuration = this
        }
    }

    data class ApplicationProperties(
        val naisAppName: String,
    ) {
        constructor(source: ApplicationConfig) : this(
            naisAppName = source.get("application.nais.app_name"),
        )
    }

    data class PostgresProperties(
        val name: String,
        val host: String,
        val port: String,
        val username: String,
        val password: String,
        val vaultMountPath: String,
    ) {
        constructor(source: ApplicationConfig) : this(
            host = source.get("database.host"),
            port = source.get("database.port"),
            name = source.get("database.name"),
            username = source.get("database.username").trim(),
            password = source.get("database.password").trim(),
            vaultMountPath = source.get("database.vault_mountpath"),
        )

        val adminUser = "$name-admin"
        val user = "$name-user"
    }

    data class SecurityProperties(
        val azureAdProperties: AzureAdProperties,
    ) {
        constructor(source: ApplicationConfig) : this(
            azureAdProperties = AzureAdProperties(source),
        )
    }

    data class AzureAdProperties(
        val clientId: String,
        val wellKnownUrl: String,
        val enabled: Boolean,
    ) {
        constructor(source: ApplicationConfig) : this(
            clientId = source.get("security.azure.client_id"),
            wellKnownUrl = source.get("security.azure.well_known_url"),
            enabled = source.get("security.azure.enabled").toBoolean(),
        )
    }

    data class DummyProperties(
        val dummyProperty: String,
    ) {
        constructor(source: ApplicationConfig) : this(
            dummyProperty = source.get("dummy_properties.katt1"),
        )
    }

    data class SomeOtherProperties(
        val someOtherProperty: String,
    ) {
        constructor(source: ApplicationConfig) : this(
            someOtherProperty = source.get("dummy_properties.katt2"),
        )
    }
}
