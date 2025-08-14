package no.nav.sokos.prosjektnavn.config

import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStarted
import io.ktor.server.application.ApplicationStopped

fun Application.applicationLifecycleConfig(applicationState: ApplicationState) {
    monitor.subscribe(ApplicationStarted) {
        applicationState.ready = true
    }

    monitor.subscribe(ApplicationStopped) {
        applicationState.ready = false
    }
}

class ApplicationState(
    var ready: Boolean = true,
    var alive: Boolean = true,
)
