package no.nav.sokos.prosjektnavn.config

import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.Route
import io.ktor.server.routing.routing

import no.nav.sokos.prosjektnavn.api.databaseServiceApi
import no.nav.sokos.prosjektnavn.api.dummyApi
import no.nav.sokos.prosjektnavn.service.DatabaseService
import no.nav.sokos.prosjektnavn.service.DummyService

fun Application.routingConfig(
    useAuthentication: Boolean,
    applicationState: ApplicationState,
) {
    routing {
        internalNaisRoutes(applicationState)
        authenticate(useAuthentication, AUTHENTICATION_NAME) {
            dummyApi(dummyService = DummyService())
            databaseServiceApi(databaseService = DatabaseService())
        }
    }
}

fun Route.authenticate(
    useAuthentication: Boolean,
    authenticationProviderId: String? = null,
    block: Route.() -> Unit,
) {
    if (useAuthentication) authenticate(authenticationProviderId) { block() } else block()
}
