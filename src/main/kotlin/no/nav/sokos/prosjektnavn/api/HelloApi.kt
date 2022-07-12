package no.nav.sokos.prosjektnavn.api

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import no.nav.sokos.prosjektnavn.util.IkkeFunnetException
import no.nav.sokos.prosjektnavn.util.IkkeTilgangException
import no.nav.sokos.prosjektnavn.util.IkkeTilgjengeligException

const val BASE_ENDPOINT = "okonomi-ktor-template"

fun Application.helloApi() {
    routing {
        route(BASE_ENDPOINT) {
            get("/forbidden") {
                println("Kommer du hit?")
                throw IkkeTilgangException("Ingen tilgang", null)
            }
            get("/ikkefunnet") {
                throw IkkeFunnetException("Ikke funnet", null)
            }
            get("/internalservererror") {
                throw IkkeTilgjengeligException("App nede!", null)
            }
        }
    }
}
