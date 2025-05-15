package no.nav.sokos.prosjektnavn.api

import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route

import no.nav.sokos.prosjektnavn.service.DummyService
import no.nav.sokos.prosjektnavn.service.SomeOtherService

fun Route.dummyApi(
    dummyService: DummyService,
    someOtherService: SomeOtherService,
) {
    route("/api/v1/") {
        get("helloKatt1") {
            val response = dummyService.sayHello()
            call.respond(HttpStatusCode.OK, response)
        }
        get("helloKatt2") {
            val response = someOtherService.sayHello()
            call.respond(HttpStatusCode.OK, response)
        }
    }
}
