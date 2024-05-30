package no.nav.sokos.prosjektnavn.config

import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.ApplicationTestBuilder

internal const val API_BASE_PATH = "/api/v1"

fun ApplicationTestBuilder.configureTestApplication() {
    val mapApplicationConfig = MapApplicationConfig()
    environment {
        config = mapApplicationConfig
    }

    application {
        commonConfig()
    }
}
