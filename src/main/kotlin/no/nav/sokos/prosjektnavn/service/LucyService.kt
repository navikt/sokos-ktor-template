package no.nav.sokos.prosjektnavn.service

import no.nav.sokos.prosjektnavn.domain.Lucy

class LucyService(
    val lucy: Lucy,
) {
    fun sayHello() = "Thea Marie har en katt som heter ${lucy.name}"
}
