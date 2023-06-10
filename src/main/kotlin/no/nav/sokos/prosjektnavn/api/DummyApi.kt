package no.nav.sokos.prosjektnavn.api

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import no.nav.sokos.prosjektnavn.config.AUTHENTICATION_NAME
import no.nav.sokos.prosjektnavn.config.authenticate
import no.nav.sokos.prosjektnavn.service.DummyService

fun Route.dummyApi(
    dummyService: DummyService,
    useAuthentication: Boolean
) {
    authenticate(useAuthentication, AUTHENTICATION_NAME) {
        route("/api/v1/") {
            get("hello") {
                //val response = dummyService.sayHello()
                call.respond(HttpStatusCode.ServiceUnavailable)
            }
            get("goodbye") {
                call.respond(HttpStatusCode.BadRequest)
            }
        }
    }
}