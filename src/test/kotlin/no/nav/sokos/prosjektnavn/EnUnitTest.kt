package no.nav.sokos.prosjektnavn

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

import no.nav.sokos.prosjektnavn.domain.Lucy
import no.nav.sokos.prosjektnavn.service.LucyService

class EnUnitTest :
    FunSpec({

        val fakeLucy =
            Lucy(
                name = "Lucy-fake",
            )

        test("katten skal hete Lucy-fake") {
            val lucyService = LucyService(fakeLucy)
            lucyService.sayHello() shouldBe "Thea Marie har en katt som heter Lucy-fake"
        }
    })
