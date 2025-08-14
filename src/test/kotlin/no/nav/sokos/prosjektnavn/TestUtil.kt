package no.nav.sokos.prosjektnavn

import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.TestApplicationBuilder

import no.nav.sokos.prosjektnavn.config.PropertiesConfig
import no.nav.sokos.prosjektnavn.listener.PostgresListener

internal const val API_BASE_PATH = "/api/v1"

object TestUtil {
    // Her ligger alt verktøy metoder som brukes i testene

    fun TestApplicationBuilder.configureTestEnvironment() {
        environment {
            System.setProperty("APPLICATION_ENV", "TEST")
            val dbConfig =
                MapApplicationConfig().apply {
                    // Database properties
                    put("application.databaseType", "POSTGRES")
                    put("application.postgres.username", PostgresListener.dbContainer.username)
                    put("application.postgres.password", PostgresListener.dbContainer.password)
                    put("application.postgres.name", PostgresListener.dbContainer.databaseName)
                    put("application.postgres.port", PostgresListener.dbContainer.firstMappedPort.toString())
                    put("application.postgres.host", PostgresListener.dbContainer.host)
                }
            PropertiesConfig.externalConfig = dbConfig
        }
    }

    fun TestApplicationBuilder.configureTestApplication(migrationScript: String = "init.sql") {
        application {
            PostgresListener.migrate(migrationScript)
            module()
        }
    }
}
