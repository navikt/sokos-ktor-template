package no.nav.sokos.prosjektnavn.api

import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route

import no.nav.sokos.prosjektnavn.service.DummyService

const val API_BASE_PATH = "/api/v1"

fun Route.dummyApi(dummyService: DummyService = DummyService()) {
    route(API_BASE_PATH) {
        get("hello") {
            val response = dummyService.sayHello()
            call.respond(HttpStatusCode.OK, response)
        }
    }
}
