package no.nav.sokos.prosjektnavn

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.testApplication

import no.nav.sokos.prosjektnavn.service.DatabaseService
import no.nav.sokos.prosjektnavn.util.CompositeApplicationConfig

class DatabaseTest : FunSpec({
    val container = TestContainer().container

    test("test") {
        testApplication {
            environment {
                val overrides =
                    MapApplicationConfig().apply {
                        put("POSTGRES_USERNAME", container.username)
                        put("POSTGRES_PASSWORD", container.password)
                        put("POSTGRES_NAME", container.databaseName)
                        put("POSTGRES_PORT", container.firstMappedPort.toString())
                        put("POSTGRES_HOST", container.host)
                        put("USE_AUTHENTICATION", "false")
                        put("INIT_DB", "true")
                    }

                config = CompositeApplicationConfig(overrides, ApplicationConfig("application.conf"))
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
