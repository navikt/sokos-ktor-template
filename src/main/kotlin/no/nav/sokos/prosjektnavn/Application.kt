package no.nav.sokos.prosjektnavn

import io.ktor.server.application.Application
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

import no.nav.sokos.prosjektnavn.config.ApplicationState
import no.nav.sokos.prosjektnavn.config.ConfigurationUtils.toPropertiesConfig
import no.nav.sokos.prosjektnavn.config.applicationLifecycleConfig
import no.nav.sokos.prosjektnavn.config.commonConfig
import no.nav.sokos.prosjektnavn.config.routingConfig
import no.nav.sokos.prosjektnavn.config.securityConfig

fun main() {
    embeddedServer(Netty, port = 8080, module = Application::module).start(true)
}

fun Application.module(appConfig: ApplicationConfig = environment.config) {
    appConfig.toPropertiesConfig()

    val applicationState = ApplicationState()

    commonConfig()
    securityConfig()
    applicationLifecycleConfig(applicationState)
    routingConfig(applicationState)
}
