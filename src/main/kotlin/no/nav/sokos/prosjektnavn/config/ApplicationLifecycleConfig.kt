package no.nav.sokos.prosjektnavn.config

import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStarted
import io.ktor.server.application.ApplicationStopped
import io.ktor.server.application.ServerReady
import io.ktor.server.application.log

fun Application.applicationLifecycleConfig(applicationState: ApplicationState) {
    monitor.subscribe(ApplicationStarted) {
        applicationState.alive = true
        it.log.info("ApplicationStarted. ApplicationState: alive(${applicationState.alive}, ready(${applicationState.ready})")
    }

    monitor.subscribe(ServerReady) {
        applicationState.ready = true
        it.log.info("ServerReady. ApplicationState: alive(${applicationState.alive}, ready(${applicationState.ready})")
    }

    monitor.subscribe(ApplicationStopped) {
        applicationState.alive = false
        applicationState.ready = false
        it.log.info("ApplicationStopped. ApplicationState: alive(${applicationState.alive}, ready(${applicationState.ready})")
    }
}

class ApplicationState(
    var ready: Boolean = false,
    var alive: Boolean = false,
)

/*fun Application.applicationLifecycleConfig(applicationState: ApplicationState) {
    monitor.subscribe(ApplicationStarting) {
        applicationState.alive = true
        applicationState.ready = false
        it.log.info("ApplicationStarting. ApplicationState: alive(${applicationState.alive}, ready(${applicationState.ready})")
    }

    monitor.subscribe(ApplicationStarted) {
        applicationState.alive = true
        it.log.info("ApplicationStarted. ApplicationState: alive(${applicationState.alive}, ready(${applicationState.ready})")
    }

    monitor.subscribe(ServerReady) {
        applicationState.ready = true
        it.log.info("ServerReady. ApplicationState: alive(${applicationState.alive}, ready(${applicationState.ready})")
    }

    monitor.subscribe(ApplicationStopPreparing) {
        applicationState.ready = false
        it.log.info("ApplicationStopPreparing. ApplicationState: alive(${applicationState.alive}, ready(${applicationState.ready})")
    }

    monitor.subscribe(ApplicationStopping) {
        applicationState.ready = false
        it.log.info("ApplicationStopping. ApplicationState: alive(${applicationState.alive}, ready(${applicationState.ready})")
    }

    monitor.subscribe(ApplicationStopped) {
        applicationState.alive = false
        it.log.info("ApplicationStopped. ApplicationState: alive(${applicationState.alive}, ready(${applicationState.ready})")
    }
}

class ApplicationState(
    var ready: Boolean = false,
    var alive: Boolean = false,
)*/
