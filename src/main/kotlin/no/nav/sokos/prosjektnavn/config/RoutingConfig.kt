package no.nav.sokos.prosjektnavn.config

import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.Route
import io.ktor.server.routing.routing

import no.nav.sokos.prosjektnavn.ApplicationDependencies
import no.nav.sokos.prosjektnavn.api.catApi
import no.nav.sokos.prosjektnavn.api.databaseServiceApi

fun Application.routingConfig(dependencies: ApplicationDependencies) {
    routing {
        authenticate(dependencies.applicationConfig.properties.security.azure.enabled, AUTHENTICATION_NAME) {
            catApi(dependencies.cats)
            databaseServiceApi(dependencies.databaseService)
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
