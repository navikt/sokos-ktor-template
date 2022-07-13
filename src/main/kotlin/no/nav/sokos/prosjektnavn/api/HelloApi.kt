package no.nav.sokos.prosjektnavn.api

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.helloApi() {
    routing {
        route("okonomi-ktor-template") {
            get("/hello") {
                call.respond(HttpStatusCode.OK, "Hello From Nais! :D")
            }
        }
    }
}
