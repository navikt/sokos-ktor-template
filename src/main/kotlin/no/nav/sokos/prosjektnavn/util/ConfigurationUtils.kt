package no.nav.sokos.prosjektnavn.util

import java.io.File
import java.util.Properties

import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.ifEmpty

import io.ktor.server.config.ApplicationConfig
import io.ktor.server.config.ApplicationConfigValue
import io.ktor.server.config.MapApplicationConfig

import no.nav.sokos.prosjektnavn.config.ConfigSource
import no.nav.sokos.prosjektnavn.config.PropertiesConfig.Configuration

fun configSourceFrom(config: ApplicationConfig): ConfigSource {
    val clusterName = System.getenv("NAIS_CLUSTER_NAME") ?: System.getProperty("NAIS_CLUSTER_NAME")
    return when (clusterName) {
        "dev-gcp", "prod-gcp" -> FallbackConfigSource(config)
        else -> LocalDevConfigSource(config)
    }
}

fun configFrom(config: ApplicationConfig): Configuration {
    val configSource = configSourceFrom(config)
    return Configuration(configSource)
}

// This is the default config fallback if nothing else is set (application.conf)
private val baseConfig = ApplicationConfig("application.conf")

// Loads defaults.properties for local dev
private val fallbackOverrides =
    MapApplicationConfig().apply {
        val file = File("defaults.properties")
        if (file.exists()) {
            val props = Properties().apply { load(file.inputStream()) }
            props.forEach { (k, v) -> put(k.toString(), v.toString()) }
        }
    }

private class FallbackConfigSource(private val config: ApplicationConfig) : ConfigSource {
    override fun get(key: String): String =
        System.getenv(key)
            ?: System.getProperty(key)
            ?: config.propertyOrNull(key)?.getString()
            ?: fallbackOverrides.propertyOrNull(key)?.getString()
            ?: baseConfig.propertyOrNull(key)?.getString()
            ?: throw IllegalStateException("Missing configuration key: $key")
}

private class LocalDevConfigSource(config: ApplicationConfig) : ConfigSource {
    private val fallback = FallbackConfigSource(config)

    override fun get(key: String): String = fallback.get(key)
}

class MapOverridingConfigSource(
    private val overrides: Map<String, String>,
    private val fallback: ConfigSource,
) : ConfigSource {
    override fun get(key: String): String = overrides[key] ?: fallback.get(key)
}

class CompositeApplicationConfig(
    private val primary: ApplicationConfig,
    private val fallback: ApplicationConfig,
) : ApplicationConfig {
    override fun property(path: String): ApplicationConfigValue = primary.propertyOrNull(path) ?: fallback.property(path)

    override fun propertyOrNull(path: String): ApplicationConfigValue? = primary.propertyOrNull(path) ?: fallback.propertyOrNull(path)

    override fun config(path: String): ApplicationConfig = CompositeApplicationConfig(primary.config(path), fallback.config(path))

    override fun configList(path: String): List<ApplicationConfig> = primary.configList(path).ifEmpty { fallback.configList(path) }

    override fun keys(): Set<String> = primary.keys() + fallback.keys()

    override fun toMap(): Map<String, Any> = (fallback.toMap().mapValues { it.value as Any } + primary.toMap().mapValues { it.value as Any })
}
