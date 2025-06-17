package no.nav.sokos.prosjektnavn

import io.ktor.server.application.Application
import io.ktor.server.config.getAs
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

import no.nav.sokos.prosjektnavn.config.AppConfig
import no.nav.sokos.prosjektnavn.config.mergeWithEnv
import no.nav.sokos.prosjektnavn.config.routingConfig
import no.nav.sokos.prosjektnavn.config.securityConfig
import no.nav.sokos.prosjektnavn.config.serverConfig
import no.nav.sokos.prosjektnavn.config.setUpDatabase
import no.nav.sokos.prosjektnavn.domain.Cats
import no.nav.sokos.prosjektnavn.service.DatabaseService

data class ApplicationDependencies(
    val applicationConfig: AppConfig,
    val cats: Cats,
    val databaseService: DatabaseService,
)

fun main() {
    embeddedServer(Netty, port = 8080, module = Application::module).start(true)
}

// For å tvinge at vi kun har EN datasource i applikasjonen må dette enten injectes her eller settes opp i embeddedServer
fun Application.module(dependencies: ApplicationDependencies = setUpDependencies()) {
    val applicationConfig = dependencies.applicationConfig
    serverConfig()
    securityConfig(applicationConfig)
    routingConfig(dependencies)
}

fun Application.setUpDependencies(): ApplicationDependencies {
    val appConfig = environment.config.mergeWithEnv()
    val cats = appConfig.property("cats").getAs<Cats>()
    val applicationConfig = appConfig.property("application").getAs<AppConfig>()
    val databaseService = DatabaseService(setUpDatabase(applicationConfig))
    return ApplicationDependencies(
        applicationConfig = applicationConfig,
        cats = cats,
        databaseService = databaseService,
    )
}
