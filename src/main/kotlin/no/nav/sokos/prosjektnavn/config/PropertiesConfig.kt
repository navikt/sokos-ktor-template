package no.nav.sokos.prosjektnavn.config

interface ConfigSource {
    fun get(key: String): String
}

object PropertiesConfig {
    data class Configuration(
        val applicationProperties: ApplicationProperties,
        val securityProperties: SecurityProperties,
        val dummyProperties: DummyProperties,
        val someOtherProperties: SomeOtherProperties,
        val postgresProperties: PostgresProperties,
    ) {
        constructor(source: ConfigSource) : this(
            applicationProperties = ApplicationProperties(source),
            securityProperties = SecurityProperties(source),
            dummyProperties = DummyProperties(source),
            someOtherProperties = SomeOtherProperties(source),
            postgresProperties = PostgresProperties(source),
        )
    }

    data class ApplicationProperties(
        val naisAppName: String,
        val profile: Profile,
    ) {
        constructor(source: ConfigSource) : this(
            naisAppName = source.get("APP_NAME"),
            profile = Profile.valueOf(source.get("APPLICATION_PROFILE")),
        )
    }

    data class PostgresProperties(
        val initDB: Boolean,
        val name: String,
        val host: String,
        val port: String,
        val username: String,
        val password: String,
        val adminUser: String,
        val user: String,
    ) {
        constructor(source: ConfigSource) : this(
            initDB = source.get("INIT_DB").toBoolean(),
            name = source.get("POSTGRES_NAME"),
            host = source.get("POSTGRES_HOST"),
            port = source.get("POSTGRES_PORT"),
            username = source.get("POSTGRES_USERNAME").trim(),
            password = source.get("POSTGRES_PASSWORD").trim(),
            adminUser = "${source.get("POSTGRES_NAME")}-admin",
            user = "${source.get("POSTGRES_NAME")}-user",
        )
    }

    data class SecurityProperties(
        val useAuthentication: Boolean,
        val azureAdProperties: AzureAdProperties,
    ) {
        constructor(source: ConfigSource) : this(
            useAuthentication = source.get("USE_AUTHENTICATION").toBoolean(),
            azureAdProperties = AzureAdProperties(source),
        )
    }

    data class AzureAdProperties(val clientId: String, val wellKnownUrl: String) {
        constructor(source: ConfigSource) : this(
            clientId = source.get("AZURE_APP_CLIENT_ID"),
            wellKnownUrl = source.get("AZURE_APP_WELL_KNOWN_URL"),
        )
    }

    data class DummyProperties(val dummyProperty: String) {
        constructor(source: ConfigSource) : this(
            dummyProperty = source.get("DUMMY_PROPERTY"),
        )
    }

    data class SomeOtherProperties(val someOtherProperty: String) {
        constructor(source: ConfigSource) : this(
            someOtherProperty = source.get("SOME_OTHER_PROPERTY"),
        )
    }

    enum class Profile {
        LOCAL,
        DEV,
        PROD,
    }
}
