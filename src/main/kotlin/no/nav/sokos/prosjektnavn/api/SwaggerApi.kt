package no.nav.sokos.prosjektnavn.api

import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.routing.Routing

fun Routing.swaggerRoutes() {
    swaggerUI(path = "api/v1/docs", swaggerFile = "openapi/pets.json")
}