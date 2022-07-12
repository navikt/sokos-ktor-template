package no.nav.sokos.prosjektnavn.api

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

const val BASE_ENDPOINT = "okonomi-ktor-template"

fun Application.helloApi() {
    routing {
        route(BASE_ENDPOINT) {
            get("/forbidden") {
                call.respond(HttpStatusCode.Forbidden)
            }
            get("/unauthorized") {
                call.respond(HttpStatusCode.Unauthorized)
            }
            get("/internalservererror") {
                call.respond(HttpStatusCode.InternalServerError)
            }
        }
    }
}
