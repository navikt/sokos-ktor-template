package no.nav.sokos.prosjektnavn

import io.kotest.matchers.shouldBe
import io.ktor.client.call.body
import io.ktor.client.request.get

class ExampleIntegrationTest : IntegrationSpec("init.sql") {
    init {
        test("example test") {
            withServer { client ->
                val response = client.get("/read")
                val (id, name) = response.body<Pair<Int, String>>()

                id shouldBe 1
                name shouldBe "Thea Marie var her"
            }
        }
    }
}
