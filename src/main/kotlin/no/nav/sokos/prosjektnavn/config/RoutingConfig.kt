package no.nav.sokos.prosjektnavn.config

import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.Route
import io.ktor.server.routing.routing

import no.nav.sokos.prosjektnavn.api.catApi
import no.nav.sokos.prosjektnavn.api.nullableCatApi
import no.nav.sokos.prosjektnavn.service.LilyService
import no.nav.sokos.prosjektnavn.service.LucyService
import no.nav.sokos.prosjektnavn.service.NullableLilyService
import no.nav.sokos.prosjektnavn.service.NullableLucyService

fun Application.routingConfig(
    useAuthentication: Boolean,
    lucyService: LucyService,
    lilyService: LilyService,
    nullableLilyService: NullableLilyService,
    nullableLucyService: NullableLucyService,
) {
    routing {
        authenticate(useAuthentication, AUTHENTICATION_NAME) {
            catApi(lucyService, lilyService)
            nullableCatApi(nullableLucyService, nullableLilyService)
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
