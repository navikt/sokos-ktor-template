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
    ) {
        constructor(source: ConfigSource) : this(
            applicationProperties = ApplicationProperties(source),
            securityProperties = SecurityProperties(source),
            dummyProperties = DummyProperties(source),
            someOtherProperties = SomeOtherProperties(source),
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
