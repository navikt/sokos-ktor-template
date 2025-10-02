package no.nav.sokos.prosjektnavn

import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

import no.nav.sokos.prosjektnavn.config.ApplicationState
import no.nav.sokos.prosjektnavn.config.PropertiesConfig
import no.nav.sokos.prosjektnavn.config.applicationLifecycleConfig
import no.nav.sokos.prosjektnavn.config.commonConfig
import no.nav.sokos.prosjektnavn.config.routingConfig
import no.nav.sokos.prosjektnavn.config.securityConfig

private val embeddedServer = embeddedServer(Netty, port = 8080, module = Application::module)

fun main() {
    embeddedServer.start(true)
}

fun Application.module() {
    val useAuthentication = PropertiesConfig.Configuration().useAuthentication
    val applicationState = ApplicationState()

    commonConfig()
    applicationLifecycleConfig(applicationState)
    securityConfig(useAuthentication)
    routingConfig(useAuthentication, applicationState)

    Runtime.getRuntime().addShutdownHook(
        Thread {
            embeddedServer.stop(gracePeriodMillis = 3000, timeoutMillis = 5000)
        },
    )
}
