package no.nav.sokos.prosjektnavn

import io.ktor.server.engine.embeddedServer
import io.ktor.server.engine.stop
import io.ktor.server.netty.Netty
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates
import no.nav.sokos.prosjektnavn.config.Config
import no.nav.sokos.prosjektnavn.config.configureCallId
import no.nav.sokos.prosjektnavn.config.configureMetrics
import no.nav.sokos.prosjektnavn.config.configureRouting
import no.nav.sokos.prosjektnavn.config.configureSecurity
import no.nav.sokos.prosjektnavn.config.configureSerialization
import no.nav.sokos.prosjektnavn.config.configureStatusPages
import no.nav.sokos.prosjektnavn.metrics.appStateReadyFalse
import no.nav.sokos.prosjektnavn.metrics.appStateRunningFalse
import no.nav.sokos.prosjektnavn.service.DummyService

fun main() {
    val applicationState = ApplicationState()
    val applicationConfiguration = Config.Configuration()
    val dummyService = DummyService()

    HttpServer(applicationState, applicationConfiguration, dummyService).start()

}
class HttpServer(
    private val applicationState: ApplicationState,
    private val applicationConfiguration: Config.Configuration,
    private val dummyService: DummyService,
    port: Int = 8080,
) {
    init {
        Runtime.getRuntime().addShutdownHook(Thread {
            this.stop()
        })
    }

    private val embeddedServer = embeddedServer(Netty, port) {
        configureSecurity(applicationConfiguration.azureAdConfig, applicationConfiguration.useAuthentication)
        configureSerialization()
        configureCallId()
        configureMetrics()
        configureStatusPages()
        configureRouting(applicationState, dummyService, applicationConfiguration.useAuthentication)
    }

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
        if (!newValue) appStateReadyFalse.inc()
    }
    var running: Boolean by Delegates.observable(ready) { _, _, newValue ->
        if (!newValue) appStateRunningFalse.inc()
    }
}