package no.nav.sokos.prosjektnavn.api

import io.ktor.http.ContentType
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.prometheus.client.exporter.common.TextFormat
import no.nav.sokos.prosjektnavn.metrics.Metrics

const val BASE_ENDPOINT = "/okonomi-ktor-template"

fun Application.helloApi() {
    routing {
        route(BASE_ENDPOINT) {
            get("/hello") {
                call.respondText { "Hello there! This is from NAIS! :)" }
            }
        }
    }
}