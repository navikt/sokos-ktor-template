package no.nav.sokos.prosjektnavn

import no.nav.sokos.prosjektnavn.config.HttpServerConfig
import no.nav.sokos.prosjektnavn.util.ApplicationState

fun main() {
    val applicationState = ApplicationState()
    val httpServer = HttpServerConfig(
        applicationState
    )

    applicationState.ready = true

    Runtime.getRuntime().addShutdownHook(Thread {
        applicationState.ready = false
        httpServer.stop()
    })
    httpServer.start()
}
