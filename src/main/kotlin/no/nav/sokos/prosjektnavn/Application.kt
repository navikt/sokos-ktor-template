package no.nav.sokos.prosjektnavn

import io.ktor.server.application.Application
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

import no.nav.sokos.prosjektnavn.config.ApplicationState
import no.nav.sokos.prosjektnavn.config.DatabaseConfig.databaseMigrate
import no.nav.sokos.prosjektnavn.config.PropertiesConfig
import no.nav.sokos.prosjektnavn.config.applicationLifecycleConfig
import no.nav.sokos.prosjektnavn.config.commonConfig
import no.nav.sokos.prosjektnavn.config.routingConfig
import no.nav.sokos.prosjektnavn.config.securityConfig

fun main() {
    embeddedServer(Netty, port = 8080, module = Application::module).start(true)
}

private val logger = mu.KotlinLogging.logger {}

fun Application.module(applicationConfig: ApplicationConfig = environment.config) {
    PropertiesConfig.initEnvConfig(applicationConfig)

    val useAuthentication = PropertiesConfig.getApplicationProperties().useAuthentication
    val applicationState = ApplicationState()

    applicationLifecycleConfig(applicationState)
    commonConfig()
    securityConfig(useAuthentication)
    routingConfig(useAuthentication, applicationState)
    databaseMigrate()

    logger.info { "Application started with environment: ${PropertiesConfig.getApplicationProperties().environment}, useAuthentication: $useAuthentication" }
}
