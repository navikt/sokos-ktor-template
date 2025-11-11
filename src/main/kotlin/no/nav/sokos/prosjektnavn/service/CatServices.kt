package no.nav.sokos.prosjektnavn.service

import no.nav.sokos.prosjektnavn.domain.Lily
import no.nav.sokos.prosjektnavn.domain.Lucy

class LilyService(
    val lily: Lily,
) {
    fun sayHello() = "Thea Marie har en katt som heter ${lily.name}"
}

class LucyService(
    val lucy: Lucy,
) {
    fun sayHello() = "Thea Marie har en katt som heter ${lucy.name}"
}

class NullableLilyService(
    val nullableLily: Lily?,
) {
    fun sayHello() = "Thea Marie har en katt som heter ${nullableLily?.name}"
}

class NullableLucyService(
    val nullableLucy: Lucy?,
) {
    fun sayHello() = "Thea Marie har en katt som heter ${nullableLucy?.name}"
}
