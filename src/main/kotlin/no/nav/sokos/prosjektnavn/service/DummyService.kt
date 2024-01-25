package no.nav.sokos.prosjektnavn.service

import no.nav.sokos.prosjektnavn.domain.DummyDomain

class DummyService {
    fun sayHello(): List<DummyDomain> {
        return listOf(
            DummyDomain(
                id = "1",
                navn = "Ola Nordmann",
                yrke = "Utvikler"
            ),
            DummyDomain(
                id = "2",
                navn = "Kari Nordmann",
                yrke = "Tech Lead"
            )
        )
    }
}