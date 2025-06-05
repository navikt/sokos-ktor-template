package no.nav.sokos.prosjektnavn.service

import no.nav.sokos.prosjektnavn.domain.Lily

class LilyService(
    val lily: Lily,
) {
    fun sayHello() = "Thea Marie har en katt som heter ${lily.name}"
}
