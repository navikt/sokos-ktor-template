package no.nav.sokos.prosjektnavn.service

import no.nav.sokos.prosjektnavn.metrics.Metrics
import no.nav.sokos.prosjektnavn.domain.DummyDomain

class DummyService {
    fun sayHello(): DummyDomain {
        Metrics.exampleCounter.inc()
        return DummyDomain("This is a template for Team Monster")
    }
}
