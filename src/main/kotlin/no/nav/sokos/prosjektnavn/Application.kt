package no.nav.sokos.prosjektnavn

import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain
import no.nav.sokos.prosjektnavn.config.Config
import no.nav.sokos.prosjektnavn.config.configureCallId
import no.nav.sokos.prosjektnavn.config.configureMetrics
import no.nav.sokos.prosjektnavn.config.configureRouting
import no.nav.sokos.prosjektnavn.config.configureSecurity
import no.nav.sokos.prosjektnavn.config.configureSerialization
import no.nav.sokos.prosjektnavn.service.DummyService
import no.nav.sokos.prosjektnavn.util.ApplicationState

fun Application.start() {
    val configuration = Config.Configuration()
    val applicationState = ApplicationState()

    val dummyService = DummyService()

    configureSecurity(configuration.azureAdConfig, configuration.useAuthentication)
    configureSerialization()
    configureCallId()
    configureMetrics()
    configureRouting(applicationState, dummyService, configuration.useAuthentication)

    applicationState.ready = true
}

fun main(args: Array<String>): Unit = EngineMain.main(args)
