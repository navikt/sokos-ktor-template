package no.nav.sokos.prosjektnavn.api

import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.routing.Routing

fun Routing.dummySwaggerApi() {
    swaggerUI(
        path = "api/v1/docs",
        swaggerFile = "openapi/sokos-ktor-template-v1-swagger.yaml",
    )
}
