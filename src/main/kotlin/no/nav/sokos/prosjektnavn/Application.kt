package no.nav.sokos.prosjektnavn

import io.ktor.server.application.Application
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.util.AttributeKey

import no.nav.sokos.prosjektnavn.config.ApplicationState
import no.nav.sokos.prosjektnavn.config.ConfigurationUtils.toPropertiesConfig
import no.nav.sokos.prosjektnavn.config.PropertiesConfig
import no.nav.sokos.prosjektnavn.config.applicationLifecycleConfig
import no.nav.sokos.prosjektnavn.config.commonConfig
import no.nav.sokos.prosjektnavn.config.routingConfig
import no.nav.sokos.prosjektnavn.config.securityConfig

fun main() {
    embeddedServer(Netty, port = 8080, module = Application::module).start(true)
}

fun Application.module(appConfig: ApplicationConfig = environment.config) {
    getOrInitConfig(appConfig)

    val applicationState = ApplicationState()

    commonConfig()
    securityConfig()
    applicationLifecycleConfig(applicationState)
    routingConfig(applicationState)
}

val ConfigAttributeKey = AttributeKey<PropertiesConfig.Configuration>("config")

fun Application.getOrInitConfig(appConfig: ApplicationConfig = environment.config): PropertiesConfig.Configuration =
    if (attributes.contains(ConfigAttributeKey)) {
        attributes[ConfigAttributeKey]
    } else {
        val config = appConfig.toPropertiesConfig()
        attributes.put(ConfigAttributeKey, config)
        config
    }
