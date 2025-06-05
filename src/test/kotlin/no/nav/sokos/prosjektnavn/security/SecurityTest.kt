package no.nav.sokos.prosjektnavn.security

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.testApplication

import no.nav.security.mock.oauth2.MockOAuth2Server
import no.nav.security.mock.oauth2.token.DefaultOAuth2TokenCallback
import no.nav.security.mock.oauth2.withMockOAuth2Server
import no.nav.sokos.prosjektnavn.config.overriding
import no.nav.sokos.prosjektnavn.module

class SecurityTest :
    FunSpec({

        test("test http GET endepunkt uten token bør returnere 401") {
            withMockOAuth2Server {
                testApplication {
                    environment {
                        config = authConfig(ApplicationConfig("application-test.conf"))
                    }

                    application {
                        module()
                    }
                    val response = client.get("/getLucy")
                    response.status shouldBe HttpStatusCode.Unauthorized
                }
            }
        }

        test("test http GET endepunkt med token bør returnere 200") {
            withMockOAuth2Server {
                val mockOAuth2Server = this
                testApplication {

                    application {
                        val newConfig = authConfig(ApplicationConfig("application-test.conf"))
                        module(newConfig) // injection av config
                    }
                    val response =
                        client.get("/getLucy") {
                            header("Authorization", "Bearer ${mockOAuth2Server.tokenFromDefaultProvider()}")
                            contentType(ContentType.Application.Json)
                        }

                    response.status shouldBe HttpStatusCode.OK

                    val response2 =
                        client.get("/getLily") {
                            header("Authorization", "Bearer ${mockOAuth2Server.tokenFromDefaultProvider()}")
                            contentType(ContentType.Application.Json)
                        }

                    response2.status shouldBe HttpStatusCode.OK
                }
            }
        }
    })

private fun MockOAuth2Server.tokenFromDefaultProvider() =
    issueToken(
        issuerId = "default",
        clientId = "default",
        tokenCallback = DefaultOAuth2TokenCallback(),
    ).serialize()

private fun MockOAuth2Server.authConfig(config: ApplicationConfig): ApplicationConfig {
    val newConf =
        MapApplicationConfig().apply {
            put("application.configuration.security.azure.wellKnownUrl", wellKnownUrl("default").toString())
            put("application.configuration.security.azure.clientId", "default")
            put("application.configuration.security.azure.enabled", "true")
        }

    return newConf overriding config
}
