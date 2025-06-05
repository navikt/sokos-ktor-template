package no.nav.sokos.prosjektnavn.api

import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

import no.nav.sokos.prosjektnavn.service.LilyService
import no.nav.sokos.prosjektnavn.service.LucyService

fun Route.catApi(
    lucyService: LucyService,
    lilyService: LilyService,
) {
    get("getLucy") {
        val response = lucyService.sayHello()
        call.respond(HttpStatusCode.OK, response)
    }
    get("getLily") {
        val response = lilyService.sayHello()
        call.respond(HttpStatusCode.OK, response)
    }
}
