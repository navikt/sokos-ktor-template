package no.nav.sokos.prosjektnavn.config

import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.routing

import no.nav.sokos.prosjektnavn.api.dummyApi
import no.nav.sokos.prosjektnavn.api.dummySwaggerApi

fun Application.routingConfig(applicationState: ApplicationState) {
    routing {
        internalNaisRoutes(applicationState)
        dummySwaggerApi()
        authenticate(AUTHENTICATION_NAME) {
            dummyApi()
        }
    }
}
