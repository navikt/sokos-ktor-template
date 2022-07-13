package no.nav.sokos.prosjektnavn.util

import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.statuspages.StatusPagesConfig
import io.ktor.server.response.respond
import mu.KotlinLogging

private val logger = KotlinLogging.logger { }

fun StatusPagesConfig.exceptionhandler() {
    exception<Throwable> { call, cause ->
        when (cause) {
            is IkkeTilgjengeligException -> {
                logger.warn(cause) { "Uventet feil" }
                call.respond(HttpStatusCode.InternalServerError)
            }
            is IkkeTilgangException -> {
                logger.warn(cause) { "Ikke tilgang" }
                call.respond(HttpStatusCode.Forbidden)
            }
            is IkkeFunnetException -> {
                logger.warn(cause) { "Ikke funnet" }
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}

data class IkkeTilgjengeligException(override val message: String?, override val cause: Throwable?) :
    RuntimeException(message, cause)

data class IkkeFunnetException(override val message: String?, override val cause: Throwable?) :
    RuntimeException(message, cause)

data class IkkeTilgangException(override val message: String?, override val cause: Throwable?) :
    RuntimeException(message, cause)