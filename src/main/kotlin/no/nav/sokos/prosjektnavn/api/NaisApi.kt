package no.nav.sokos.prosjektnavn.api

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.naisApi(alive: () -> Boolean, ready: () -> Boolean) {
    routing {
        route("internal") {
            get("isAlive") {
                when (alive()) {
                    true -> call.respondText { "Application is alive" }
                    else -> call.respondText(
                        text = "Application is not alive",
                        status = HttpStatusCode.InternalServerError
                    )
                }
            }
            get("isReady") {
                when (ready()) {
                    true -> call.respondText { "Application is ready" }
                    else -> call.respondText(
                        text = "Application is not ready",
                        status = HttpStatusCode.InternalServerError
                    )
                }
            }
        }
    }
}
