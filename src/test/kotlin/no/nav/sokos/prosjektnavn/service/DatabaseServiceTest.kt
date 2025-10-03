package no.nav.sokos.prosjektnavn.service

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.server.testing.testApplication

import no.nav.sokos.prosjektnavn.TestUtil.configureTestApplication
import no.nav.sokos.prosjektnavn.TestUtil.configureTestEnvironment
import no.nav.sokos.prosjektnavn.listener.PostgresListener

class DatabaseServiceTest :
    FunSpec({
        extensions(PostgresListener)

        test("Database connection should be established") {

            testApplication {
                configureTestEnvironment()
                configureTestApplication("init.sql")

                startApplication()

                // Add your tests here
                val databaseService = DatabaseService()
                val result = databaseService.read()
                result.first shouldBe 1
                result.second shouldBe "Thea Marie var her i går"
            }
        }

        test("Database connection should be established 2") {

            testApplication {
                configureTestEnvironment()
                configureTestApplication("init.sql")

                startApplication()

                // Add your tests here
                val databaseService = DatabaseService()
                val result = databaseService.read()
                result.first shouldBe 1
                result.second shouldBe "Thea Marie var her i går"
            }
        }
    })
