package no.nav.sokos.prosjektnavn.service

import no.nav.sokos.prosjektnavn.domain.DummyDomain
import no.nav.sokos.prosjektnavn.metrics.Metrics

class DummyService {
    fun sayHello(): DummyDomain {
        Metrics.exampleCounter.inc()
        return DummyDomain("Hello World! Greeting from master branch!")
    }
}
