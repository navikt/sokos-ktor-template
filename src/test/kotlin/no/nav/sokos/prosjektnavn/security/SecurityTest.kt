package no.nav.sokos.prosjektnavn.security

import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.testApplication

import no.nav.security.mock.oauth2.MockOAuth2Server
import no.nav.security.mock.oauth2.token.DefaultOAuth2TokenCallback
import no.nav.security.mock.oauth2.withMockOAuth2Server
import no.nav.sokos.prosjektnavn.IntegrationSpec
import no.nav.sokos.prosjektnavn.module

class SecurityTest : IntegrationSpec() {
    init {
        test("test http GET endepunkt uten token bør returnere 401") {
            withMockOAuth2Server {
                testApplication {
                    environment {
                        config = dbContainer.getMapAppConfig()
                    }
                    application {
                        module()
                    }
                    startApplication()
                    val response = client.get("/getLucy")
                    response.status shouldBe HttpStatusCode.Unauthorized
                }
            }
        /*    withOauthServer { client ->
                val response = client.get("/getLucy")
                response.status shouldBe HttpStatusCode.Unauthorized
            }*/
        }

/*        test("test http GET endepunkt med token bør returnere 200") {
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
        }*/
    }
}

private fun MockOAuth2Server.tokenFromDefaultProvider() =
    issueToken(
        issuerId = "default",
        clientId = "default",
        tokenCallback = DefaultOAuth2TokenCallback(),
    ).serialize()

private fun MockOAuth2Server.authConfig(): ApplicationConfig =
    MapApplicationConfig().apply {
        put("ktor.environment", "test")
        put("application.properties.security.azure.wellKnownUrl", wellKnownUrl("default").toString())
        put("application.properties.security.azure.clientId", "default")
        put("application.properties.security.azure.enabled", "true")
    }
