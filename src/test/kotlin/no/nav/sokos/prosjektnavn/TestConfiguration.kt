package no.nav.sokos.prosjektnavn

import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.routing.routing
import io.ktor.server.testing.ApplicationTestBuilder
import no.nav.sokos.prosjektnavn.api.metricsApi
import no.nav.sokos.prosjektnavn.api.naisApi
import no.nav.sokos.prosjektnavn.api.swaggerApi
import no.nav.sokos.prosjektnavn.config.commonConfig

internal const val API_BASE_PATH = "/api/v1"

fun ApplicationTestBuilder.configureTestApplication() {
    val mapApplicationConfig = MapApplicationConfig()
    environment {
        config = mapApplicationConfig
    }

    application {
        commonConfig()
        val applicationState = ApplicationState(ready = true)

        routing {
            naisApi({ applicationState.initialized }, { applicationState.running })
            metricsApi()
            swaggerApi()
        }
    }
}
