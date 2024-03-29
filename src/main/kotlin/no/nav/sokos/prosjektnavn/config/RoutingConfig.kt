package no.nav.sokos.prosjektnavn.config

import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.Route
import io.ktor.server.routing.routing
import no.nav.sokos.prosjektnavn.ApplicationState
import no.nav.sokos.prosjektnavn.api.dummyApi
import no.nav.sokos.prosjektnavn.api.naisApi
import no.nav.sokos.prosjektnavn.api.swaggerApi

fun Application.routingConfig(
    applicationState: ApplicationState,
    useAuthentication: Boolean
) {
    routing {
        naisApi({ applicationState.initialized }, { applicationState.running })
        swaggerApi()
        authenticate(useAuthentication, AUTHENTICATION_NAME) {
            dummyApi()
        }
    }
}

fun Route.authenticate(useAuthentication: Boolean, authenticationProviderId: String? = null, block: Route.() -> Unit) {
    if (useAuthentication) authenticate(authenticationProviderId) { block() } else block()
}