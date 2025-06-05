
package no.nav.sokos.prosjektnavn

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.config.getAs
import io.ktor.server.testing.testApplication
import io.mockk.every
import io.mockk.mockk

import no.nav.sokos.prosjektnavn.config.ApplicationProperties
import no.nav.sokos.prosjektnavn.domain.Cats
import no.nav.sokos.prosjektnavn.domain.Lily
import no.nav.sokos.prosjektnavn.domain.Lucy

class TestApplicationEksempler :
    FunSpec(
        {

            test("metode 1") {
                testApplication {
                    environment {
                        config = ApplicationConfig("application-test.conf")
                    }

                    application {
                        module() // leser fra environment.config, som er defaultverdi i module()
                    }
                    val response = client.get("/getLucy")
                    response.status shouldBe HttpStatusCode.OK
                    response.bodyAsText() shouldBe "Thea Marie har en katt som heter Lucy-test"
                }
            }
            test("metode 2") {
                testApplication {
                    application {
                        val newConfig = ApplicationConfig("application-test.conf")
                        module(newConfig) // ren injection
                    }
                    val response = client.get("/getLily")
                    response.status shouldBe HttpStatusCode.OK
                    response.bodyAsText() shouldBe "Thea Marie har en katt som heter Lily-test"
                }
            }

            test("Glemte å sette config") {
                testApplication {
                    application {
                        module()
                    }
                    val response = client.get("/getLucy")
                    response.status shouldBe HttpStatusCode.OK
                    response.bodyAsText() shouldBe "Thea Marie har en katt som heter Lucy-local"
                }
            }

            test("mocker config") {
                testApplication {
                    application {

                        val mockConfig =
                            mockk<ApplicationConfig>(relaxed = true) {
                                every { property("application") } returns
                                    mockk {
                                        every { getAs<ApplicationProperties>() } returns mockk<ApplicationProperties>(relaxed = true)
                                    }

                                every { property("cats") } returns
                                    mockk {
                                        every { getAs<Cats>() } returns
                                            Cats(
                                                lucy = Lucy("Lucy-mock"),
                                                lily = Lily("Lily-mock"),
                                            )
                                    }
                            }

                        module(mockConfig)
                    }
                    val response = client.get("/getLucy")
                    response.status shouldBe HttpStatusCode.OK
                    response.bodyAsText() shouldBe "Thea Marie har en katt som heter Lucy-mock"
                }
            }
        },
    )
