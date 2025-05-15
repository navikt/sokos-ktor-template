package no.nav.sokos.prosjektnavn.config

import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.Route
import io.ktor.server.routing.routing

import no.nav.sokos.prosjektnavn.Dependencies
import no.nav.sokos.prosjektnavn.api.dummyApi
import no.nav.sokos.prosjektnavn.config

fun Application.routingConfig(
    applicationState: ApplicationState,
    dependencies: Dependencies,
) {
    routing {
        internalNaisRoutes(applicationState)
        authenticate(config().securityProperties.useAuthentication, AUTHENTICATION_NAME) {
            dummyApi(dependencies.dummyService, dependencies.someOtherService)
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
