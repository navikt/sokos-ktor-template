package no.nav.sokos.prosjektnavn

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.server.testing.testApplication

import no.nav.sokos.prosjektnavn.service.DatabaseService

class DatabaseTest :
    FunSpec({
        val tc = TestContainer()
        tc.migrate("init.sql")
        test("test") {
            testApplication {
                environment {
                    config = tc.overrides
                }
                application {
                    module()
                }
            }

            val (id, name) = DatabaseService().read()

            id shouldBe 1
            name shouldBe "Thea Marie var her"
        }
    })
