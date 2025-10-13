package no.nav.sokos.prosjektnavn.api

import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route

import no.nav.sokos.prosjektnavn.service.DatabaseService

fun Route.databaseServiceApi(databaseService: DatabaseService) {
    route("/api/v1/") {
        get("read") {
            val response = databaseService.read()
            call.respond(HttpStatusCode.OK, response)
        }
    }
}
