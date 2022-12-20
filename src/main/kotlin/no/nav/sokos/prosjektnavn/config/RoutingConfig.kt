package no.nav.sokos.prosjektnavn.config

import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.Route
import io.ktor.server.routing.routing
import no.nav.sokos.prosjektnavn.api.dummyRoutes
import no.nav.sokos.prosjektnavn.api.metricsRoutes
import no.nav.sokos.prosjektnavn.api.naisRoutes
import no.nav.sokos.prosjektnavn.service.DummyService
import no.nav.sokos.prosjektnavn.util.ApplicationState


fun Application.configureRouting(
    applicationState: ApplicationState,
    dummyService: DummyService,
    useAuthentication: Boolean
) {
    routing {
        naisRoutes({ applicationState.alive }, { applicationState.ready })
        metricsRoutes()

        dummyRoutes(dummyService, useAuthentication)
    }
}

fun Route.authenticate(useAuthentication: Boolean, authenticationProviderId: String? = null, block: Route.() -> Unit) {
    if (useAuthentication) authenticate(authenticationProviderId) { block() } else block()
}