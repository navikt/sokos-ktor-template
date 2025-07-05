package no.nav.sokos.prosjektnavn.service

import mu.KotlinLogging

import no.nav.sokos.prosjektnavn.config.TEAM_LOGS_MARKER
import no.nav.sokos.prosjektnavn.domain.DummyDomain
import no.nav.sokos.prosjektnavn.metrics.Metrics

private val logger = KotlinLogging.logger {}

class DummyService {
    fun sayHello(): DummyDomain {
        Metrics.exampleCounter.inc()
        logger.info(marker = TEAM_LOGS_MARKER) { "Dette havner i team logs!" }
        return DummyDomain("This is a template for Team Motta og Beregne")
    }
}
