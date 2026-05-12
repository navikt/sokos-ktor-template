package no.nav.sokos.prosjektnavn

import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import mu.KotlinLogging

import no.nav.sokos.prosjektnavn.config.ApplicationState
import no.nav.sokos.prosjektnavn.config.PropertiesConfig
import no.nav.sokos.prosjektnavn.config.applicationLifecycleConfig
import no.nav.sokos.prosjektnavn.config.commonConfig
import no.nav.sokos.prosjektnavn.config.loadEnv
import no.nav.sokos.prosjektnavn.config.routingConfig
import no.nav.sokos.prosjektnavn.config.securityConfig

private val logger = KotlinLogging.logger {}

fun main() {
    embeddedServer(Netty, port = 8080, module = Application::module).start(true)
}

private fun Application.module() {
    PropertiesConfig.load(environment.config.loadEnv())

    val applicationState = ApplicationState()

    applicationLifecycleConfig(applicationState)
    commonConfig()
    securityConfig(PropertiesConfig.azureAdProperties)
    routingConfig(PropertiesConfig.applicationProperties.useAuthentication, applicationState)

    logger.info { "Application started with profile: ${PropertiesConfig.applicationProperties.profile}" }
}
