package no.nav.sokos.prosjektnavn

import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.engine.stop
import io.ktor.server.netty.Netty
import no.nav.sokos.prosjektnavn.config.PropertiesConfig
import no.nav.sokos.prosjektnavn.config.commonConfig
import no.nav.sokos.prosjektnavn.config.configureLifecycleConfig
import no.nav.sokos.prosjektnavn.config.configureSecurity
import no.nav.sokos.prosjektnavn.config.routingConfig
import java.util.concurrent.TimeUnit

fun main() {
    HttpServer().start()
}

class HttpServer(
    port: Int = 8080,
) {
    init {
        Runtime.getRuntime().addShutdownHook(
            Thread {
                this.stop()
            },
        )
    }

    private val embeddedServer =
        embeddedServer(Netty, port, module = {
            serverModule()
        })

    fun start() {
        embeddedServer.start(wait = true)
    }

    private fun stop() {
        embeddedServer.stop(5, 5, TimeUnit.SECONDS)
    }
}

class ApplicationState(
    var ready: Boolean = true,
    var alive: Boolean = true,
)

fun Application.serverModule() {
    val applicationState = ApplicationState()
    val applicationConfiguration = PropertiesConfig.Configuration()

    commonConfig()
    configureLifecycleConfig(applicationState)
    configureSecurity(applicationConfiguration.azureAdProperties, applicationConfiguration.useAuthentication)
    routingConfig(applicationState, applicationConfiguration.useAuthentication)
}
