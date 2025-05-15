package no.nav.sokos.prosjektnavn.service

import no.nav.sokos.prosjektnavn.config.PropertiesConfig

class DummyService(val dummyProperties: PropertiesConfig.DummyProperties) {
    fun sayHello() = "Thea Marie har en katt som heter ${dummyProperties.dummyProperty}"
}
