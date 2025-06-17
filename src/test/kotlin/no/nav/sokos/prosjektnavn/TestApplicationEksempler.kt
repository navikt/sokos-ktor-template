
package no.nav.sokos.prosjektnavn

import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.config.MapApplicationConfig

// Må alltid bruke IntegrationSpec siden vi alltid må spinne opp en testcontainer, siden vi tester hele applikasjonen
class TestApplicationEksempler : IntegrationSpec() {
    init {

        test("Test conf blir lastet fra fil") {
            withServer { client ->
                val response = client.get("/getLucy")
                response.status shouldBe HttpStatusCode.OK
                response.bodyAsText() shouldBe "Thea Marie har en katt som heter Lucy-test"
            }
        }

        test("Override test conf med kode") {
            val config =
                MapApplicationConfig().apply {
                    put("cats.lucy.name", "Lucy-override")
                }
            withConfig(config).withServer { client ->
                val response = client.get("/getLucy")
                response.status shouldBe HttpStatusCode.OK
                response.bodyAsText() shouldBe "Thea Marie har en katt som heter Lucy-override"
            }
        }

        test("Override test conf med fil") {
            val config = ApplicationConfig("LilyOverride.conf")
            withConfig(config).withServer { client ->
                val response = client.get("/getLily")
                response.status shouldBe HttpStatusCode.OK
                response.bodyAsText() shouldBe "Thea Marie har en katt som heter Lily-override"
            }
        }
    }
}
