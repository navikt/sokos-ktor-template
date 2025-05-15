package no.nav.sokos.prosjektnavn.service

import no.nav.sokos.prosjektnavn.config.PropertiesConfig

class SomeOtherService(val someOtherProperties: PropertiesConfig.SomeOtherProperties) {
    fun sayHello() = "Thea Marie har en katt som heter ${someOtherProperties.someOtherProperty}"
}
