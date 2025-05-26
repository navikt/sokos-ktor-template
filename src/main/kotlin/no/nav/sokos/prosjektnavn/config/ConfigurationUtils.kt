package no.nav.sokos.prosjektnavn.config

import io.ktor.server.config.ApplicationConfig

object ConfigurationUtils {
    fun ApplicationConfig.get(key: String): String =
        System.getenv(key)
            ?: System.getProperty(key)
            ?: propertyOrNull(key)?.getString()
            ?: PropertiesConfig.environment.get(key)
            ?: throw IllegalStateException("Missing configuration key: $key")

    fun ApplicationConfig.toPropertiesConfig(): PropertiesConfig.Configuration {
        PropertiesConfig.Environment(this)
        return PropertiesConfig.Configuration(this)
    }

    fun ApplicationConfig.determineRunEnv(): PropertiesConfig.Profile {
        val env =
            System.getenv("NAIS_CLUSTER_NAME")
                ?: System.getProperty("NAIS_CLUSTER_NAME")
                ?: propertyOrNull("ktor.environment")?.getString()
                ?: if (isRunningInTestEnvironment()) {
                    "test"
                } else {
                    "unknown"
                }

        return when (env.lowercase()) {
            "test" -> PropertiesConfig.Profile.TEST
            "local" -> PropertiesConfig.Profile.LOCAL
            "dev-fss" -> PropertiesConfig.Profile.DEV
            "prod-fss" -> PropertiesConfig.Profile.PROD
            else -> PropertiesConfig.Profile.UNKNOWN
        }
    }

    private fun isRunningInTestEnvironment() =
        Thread.currentThread().stackTrace.any { element ->
            element.className.lowercase().contains("test")
        }
}
