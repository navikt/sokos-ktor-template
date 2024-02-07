package no.nav.sokos.prosjektnavn

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import no.nav.sokos.prosjektnavn.config.PropertiesConfig
import no.nav.sokos.prosjektnavn.config.commonConfig
import no.nav.sokos.prosjektnavn.config.configureSecurity
import no.nav.sokos.prosjektnavn.config.routingConfig
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates
import no.nav.sokos.prosjektnavn.metrics.Metrics

fun main() {
    val applicationState = ApplicationState()
    val applicationConfiguration = PropertiesConfig.Configuration()

    HttpServer(applicationState, applicationConfiguration).start()

}

class HttpServer(
    private val applicationState: ApplicationState,
    private val applicationConfiguration: PropertiesConfig.Configuration,
    port: Int = 8080,
) {
    init {
        Runtime.getRuntime().addShutdownHook(Thread {
            this.stop()
        })
    }

    private val embeddedServer = embeddedServer(Netty, port, module = {
        applicationModule(applicationConfiguration, applicationState)
    })

    fun start() {
        applicationState.running = true
        embeddedServer.start(wait = true)
    }

    private fun stop() {
        applicationState.running = false
        embeddedServer.stop(5, 5, TimeUnit.SECONDS)
    }
}

class ApplicationState(
    alive: Boolean = true,
    ready: Boolean = false
) {
    var initialized: Boolean by Delegates.observable(alive) { _, _, newValue ->
        if (!newValue) Metrics.appStateReadyFalse.inc()
    }
    var running: Boolean by Delegates.observable(ready) { _, _, newValue ->
        if (!newValue) Metrics.appStateRunningFalse.inc()
    }
}

fun Application.applicationModule(
    applicationConfiguration: PropertiesConfig.Configuration,
    applicationState: ApplicationState
) {
    commonConfig()
    configureSecurity(applicationConfiguration.azureAdConfig, applicationConfiguration.useAuthentication)
    routingConfig(applicationState, applicationConfiguration.useAuthentication)
}