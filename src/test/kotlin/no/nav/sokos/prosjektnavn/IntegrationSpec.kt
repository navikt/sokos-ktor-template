package no.nav.sokos.prosjektnavn

import io.kotest.core.spec.style.FunSpec
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.host
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.TestApplicationBuilder
import io.ktor.server.testing.testApplication
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.ext.ScriptUtils
import org.testcontainers.jdbc.JdbcDatabaseDelegate
import org.testcontainers.utility.DockerImageName

import no.nav.sokos.prosjektnavn.config.overriding

abstract class IntegrationSpec(
    val initScript: String = "",
) : FunSpec() {
    val dbContainer by lazy { MyContainer() }
    var customConfig: ApplicationConfig? = null

    fun withConfig(config: ApplicationConfig): IntegrationSpec =
        apply {
            customConfig = config
        }

    fun withServer(block: suspend TestApplicationBuilder.(client: HttpClient) -> Unit) =
        testApplication {
            environment {
                // Må alltid gjøres, fordi applikasjonen kobler til testcontaineren
                config = dbContainer.getMapAppConfig()
                customConfig?.let {
                    config = config overriding it
                }
            }
            application {
                if (initScript.isNotEmpty()) dbContainer.migrate(initScript)
                module(setUpDependencies())
            }

            val client =
                createClient {
                    install(ContentNegotiation) { json() }
                }
            block(this, client)
        }
}

class MyContainer {
    private val dockerImageName = "postgres:latest"
    val container =
        PostgreSQLContainer<Nothing>(DockerImageName.parse(dockerImageName)).apply {
            withReuse(false)
            withUsername("$username-admin")
            start()
        }

    fun getMapAppConfig(): ApplicationConfig =
        MapApplicationConfig().apply {
            put("ktor.environment", "test")
            put("application.properties.database.username", container.username)
            put("application.properties.database.password", container.password)
            put("application.properties.database.name", container.databaseName)
            put("application.properties.database.port", container.firstMappedPort.toString())
            put("application.properties.database.host", host)
            put("application.properties.security.azure.wellKnownUrl", "=http://localhost:45225/default/.well-known/openid-configuration")
            put("application.properties.security.clientId", "default")
            put("application.properties.security.enabled", "true")
        }

    fun migrate(script: String) = loadInitScript(script)

    private fun loadInitScript(name: String) = ScriptUtils.runInitScript(JdbcDatabaseDelegate(container, ""), name)
}
