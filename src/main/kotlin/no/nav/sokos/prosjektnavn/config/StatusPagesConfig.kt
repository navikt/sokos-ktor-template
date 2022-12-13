package no.nav.sokos.prosjektnavn.config

import io.ktor.client.plugins.ClientRequestException
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.install
import io.ktor.server.application.log
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.httpMethod
import io.ktor.server.request.path
import io.ktor.server.response.respond
import java.time.ZonedDateTime
import no.nav.sokos.prosjektnavn.util.InvalidInputException

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            val (responseStatus, apiError) = when (cause) {

                is InvalidInputException -> Pair(HttpStatusCode.NotFound, ApiError(HttpStatusCode.NotFound, cause, call))

                is ClientRequestException -> {
                    val httpStatusCode = cause.response.status
                    Pair(httpStatusCode, ApiError(httpStatusCode, cause, call))
                }

                else -> Pair(HttpStatusCode.InternalServerError, ApiError(HttpStatusCode.InternalServerError, cause, call))
            }

            call.application.log.error("Feilet håndtering av ${call.request.httpMethod} - ${call.request.path()} status=$responseStatus", cause)
            call.respond(responseStatus, apiError)
        }
    }
}

internal data class ApiError(
    val timestamp: ZonedDateTime,
    val status: Int,
    val error: String,
    val message: String,
    val path: String
) {

    constructor(httpStatusCode: HttpStatusCode, cause: Throwable, call: ApplicationCall) :
        this(ZonedDateTime.now(), httpStatusCode.value, httpStatusCode.description, cause.message ?: "Unknown error", call.request.path())
}
