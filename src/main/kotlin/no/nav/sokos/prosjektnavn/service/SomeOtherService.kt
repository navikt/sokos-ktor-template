package no.nav.sokos.prosjektnavn.service

import no.nav.sokos.prosjektnavn.config.PropertiesConfig.configuration

class SomeOtherService {
    fun sayHello() = "Thea Marie har en katt som heter ${configuration.someOtherProperties.someOtherProperty}"
}
