package no.nav.sokos.prosjektnavn.config

import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.Route
import io.ktor.server.routing.routing

import no.nav.sokos.prosjektnavn.api.dummyApi
import no.nav.sokos.prosjektnavn.config.PropertiesConfig.configuration

fun Application.routingConfig(applicationState: ApplicationState) {
    routing {
        internalNaisRoutes(applicationState)
        authenticate(configuration.securityProperties.azureAdProperties.enabled, AUTHENTICATION_NAME) {
            dummyApi()
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
