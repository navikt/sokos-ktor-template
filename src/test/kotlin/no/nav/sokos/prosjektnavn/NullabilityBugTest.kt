package no.nav.sokos.prosjektnavn

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.testing.testApplication

class NullabilityBugTest :
    FunSpec({

        test("Lily should be null but Lucy should not be null") {
            testApplication {
                environment {
                    config = ApplicationConfig("application-test-nullability.conf")
                }

                application {
                    module() // leser fra environment.config, som er defaultverdi i module()
                }
                val lucyResponse = client.get("/getNullableLucy")
                lucyResponse.bodyAsText() shouldBe "Thea Marie har en katt som heter Lucy-nullable"

                val lilyResponse = client.get("/getNullableLily")
                lilyResponse.bodyAsText() shouldBe "Thea Marie har en katt som heter null"
            }
        }
    })
