package no.nav.sokos.prosjektnavn

import io.ktor.server.application.host
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.config.ApplicationConfigValue
import io.ktor.server.config.MapApplicationConfig
import org.flywaydb.core.Flyway
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.ext.ScriptUtils
import org.testcontainers.jdbc.JdbcDatabaseDelegate
import org.testcontainers.utility.DockerImageName

import no.nav.sokos.prosjektnavn.config.ConfigurationUtils.toPropertiesConfig
import no.nav.sokos.prosjektnavn.config.DatabaseConfig
import no.nav.sokos.prosjektnavn.config.PropertiesConfig

class TestContainer {
    private val dockerImageName = "postgres:latest"
    val container =
        PostgreSQLContainer<Nothing>(DockerImageName.parse(dockerImageName)).apply {
            withReuse(false)
            withUsername("$username-admin")
            start()
        }
    var overrides: CompositeApplicationConfig
    var appConfig: PropertiesConfig.Configuration

    init {
        val (appConfig, overrides) = initPostgres()
        this.appConfig = appConfig
        this.overrides = overrides
    }

    fun migrate(script: String = "") {
        if (script.isNotEmpty()) loadInitScript(script)
    }

    private fun loadInitScript(name: String) = ScriptUtils.runInitScript(JdbcDatabaseDelegate(container, ""), name)

    fun initPostgres(): Pair<PropertiesConfig.Configuration, CompositeApplicationConfig> {
        val overrides =
            MapApplicationConfig().apply {
                put("ktor.environment", "test")
                put("database.username", container.username)
                put("database.password", container.password)
                put("database.name", container.databaseName)
                put("database.port", container.firstMappedPort.toString())
                put("database.host", host)
                put("database.init_db", "true")
            }

        val config = CompositeApplicationConfig(overrides, ApplicationConfig("application-test.conf"))
        val appConfig = config.toPropertiesConfig()

        DatabaseConfig.init()
        val flyway =
            Flyway
                .configure()
                .dataSource(DatabaseConfig.dataSource)
                .baselineOnMigrate(true) // This is the key fix
                .load()

        flyway.migrate()

        return Pair(appConfig, config)
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
}
