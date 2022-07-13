package no.nav.sokos.prosjektnavn.config

import io.ktor.server.engine.embeddedServer
import io.ktor.server.engine.stop
import io.ktor.server.netty.Netty
import no.nav.sokos.prosjektnavn.api.metricsApi
import no.nav.sokos.prosjektnavn.api.naisApi
import no.nav.sokos.prosjektnavn.util.ApplicationState
import java.util.concurrent.TimeUnit

private const val GRACE_PERIOD = 5L
private const val TIMEOUT = 5L

class HttpServerConfig(
    applicationState: ApplicationState,
    port: Int = 8080,
) {
    private val embeddedServer = embeddedServer(Netty, port) {
        installCommonFeatures()
        installMetrics()
        metricsApi()
        naisApi({ applicationState.alive }, { applicationState.ready })
    }

    fun start() = embeddedServer.start(wait = true)
    fun stop() = embeddedServer.stop(GRACE_PERIOD, TIMEOUT, TimeUnit.SECONDS)
}
