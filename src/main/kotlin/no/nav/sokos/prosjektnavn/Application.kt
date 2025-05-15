package no.nav.sokos.prosjektnavn

import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.util.AttributeKey

import no.nav.sokos.prosjektnavn.config.ApplicationState
import no.nav.sokos.prosjektnavn.config.PropertiesConfig
import no.nav.sokos.prosjektnavn.config.applicationLifecycleConfig
import no.nav.sokos.prosjektnavn.config.commonConfig
import no.nav.sokos.prosjektnavn.config.routingConfig
import no.nav.sokos.prosjektnavn.config.securityConfig
import no.nav.sokos.prosjektnavn.service.DummyService
import no.nav.sokos.prosjektnavn.service.SomeOtherService
import no.nav.sokos.prosjektnavn.util.configFrom

fun main() {
    embeddedServer(Netty, port = 8080, module = Application::module).start(true)
}

fun Application.module(appConfig: ApplicationConfig = environment.config) {
    val config = resolveConfig(appConfig)

    val module = dependencies(config)
    val applicationState = ApplicationState()

    commonConfig()
    securityConfig()
    applicationLifecycleConfig(applicationState)
    routingConfig(applicationState, module)
}

class Dependencies(val dummyService: DummyService, val someOtherService: SomeOtherService)

fun dependencies(config: PropertiesConfig.Configuration): Dependencies {
    return Dependencies(
        dummyService = DummyService(config.dummyProperties),
        someOtherService = SomeOtherService(config.someOtherProperties),
    )
}

val ConfigAttributeKey = AttributeKey<PropertiesConfig.Configuration>("config")

fun Application.config(): PropertiesConfig.Configuration = this.attributes[ConfigAttributeKey]

fun ApplicationCall.config(): PropertiesConfig.Configuration = this.application.config()

fun Application.resolveConfig(appConfig: ApplicationConfig = environment.config): PropertiesConfig.Configuration {
    return if (attributes.contains(ConfigAttributeKey)) {
        // Bruke config hvis den allerede er satt
        this.config()
    } else {
        configFrom(appConfig).also {
            attributes.put(ConfigAttributeKey, it)
        }
    }
}
