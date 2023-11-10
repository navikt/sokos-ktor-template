package no.nav.sokos.prosjektnavn.api

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import mu.KotlinLogging
import no.nav.sokos.prosjektnavn.config.AUTHENTICATION_NAME
import no.nav.sokos.prosjektnavn.config.authenticate
import no.nav.sokos.prosjektnavn.service.DummyService

private val log = KotlinLogging.logger {}
fun Route.dummyApi(
    dummyService: DummyService,
    useAuthentication: Boolean
) {
    authenticate(useAuthentication, AUTHENTICATION_NAME) {
        route("/api/v1/") {
            get("hello") {
                val response = dummyService.sayHello()
                call.respond(HttpStatusCode.OK, response)
            }

            get("error") {
                for (i in 0..5) {
                    log.error { "Nå er'e feil igjen, Error: $i" }
                }
                call.respond("Nå er det 1000 errors i loggen")
            }

            get("warn") {

                for (i in 0..5) {
                    log.warn { "Nå er'e feil igjen, Warning: $i" }
                }
                call.respond("Nå er det 1000 errors i loggen")
            }

        }
    }
}