package no.nav.sokos.prosjektnavn

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe


class ApplicationTest : FunSpec ({

    test("my first test") {
        1 + 1 shouldBe 2
    }

    test("my second test") {
        2 + 2 shouldBe 4
    }

    test("my third test") {
        3 + 3 shouldBe 6
    }

})
