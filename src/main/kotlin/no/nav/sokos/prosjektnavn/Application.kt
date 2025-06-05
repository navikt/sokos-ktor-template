package no.nav.sokos.prosjektnavn

import io.ktor.server.application.Application
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.config.getAs
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

import no.nav.sokos.prosjektnavn.config.ApplicationProperties
import no.nav.sokos.prosjektnavn.config.mergeWithEnv
import no.nav.sokos.prosjektnavn.config.routingConfig
import no.nav.sokos.prosjektnavn.config.securityConfig
import no.nav.sokos.prosjektnavn.config.serverConfig
import no.nav.sokos.prosjektnavn.domain.Cats
import no.nav.sokos.prosjektnavn.service.LilyService
import no.nav.sokos.prosjektnavn.service.LucyService

fun main() {
    embeddedServer(Netty, port = 8080, module = Application::module).start(true)
}

fun Application.module(appConfig: ApplicationConfig = environment.config.mergeWithEnv()) {
    val applicationProperties = appConfig.property("application").getAs<ApplicationProperties>()

    val cats = appConfig.property("cats").getAs<Cats>()
    val lucyService = LucyService(cats.lucy)
    val lilyService = LilyService(cats.lily)

    serverConfig()
    securityConfig(applicationProperties)
    routingConfig(applicationProperties.configuration.security.azure.enabled, lucyService, lilyService)
}
