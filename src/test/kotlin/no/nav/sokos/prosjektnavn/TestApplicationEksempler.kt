package no.nav.sokos.prosjektnavn

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.testApplication

import no.nav.security.mock.oauth2.MockOAuth2Server
import no.nav.security.mock.oauth2.token.DefaultOAuth2TokenCallback
import no.nav.security.mock.oauth2.withMockOAuth2Server
import no.nav.sokos.prosjektnavn.config.PropertiesConfig
import no.nav.sokos.prosjektnavn.util.CompositeApplicationConfig
import no.nav.sokos.prosjektnavn.util.MapOverridingConfigSource
import no.nav.sokos.prosjektnavn.util.configSourceFrom

class TestApplicationEksempler : FunSpec(
    {

        test("metode 1") {
            withMockOAuth2Server {
                testApplication {
                    environment {
                        config = ApplicationConfig("application.conf")
                    }

                    application {
                        val config = PropertiesConfig.Configuration(authConfig(environment.config))
                        attributes.put(ConfigAttributeKey, config)
                        module() // leser fra attributes
                    }
                    val response = client.get("$API_BASE_PATH/helloKatt1")
                    response.status shouldBe HttpStatusCode.Unauthorized
                }
            }
        }
        test("metode 2") {
            withMockOAuth2Server {
                testApplication {
                    environment {
                        val overrides =
                            MapApplicationConfig().apply {
                                put("AZURE_APP_WELL_KNOWN_URL", wellKnownUrl("default").toString())
                                put("AZURE_APP_CLIENT_ID", "default")
                            }

                        config = CompositeApplicationConfig(overrides, ApplicationConfig("application.conf"))
                    }

                    application {
                        module() // leser fra environment.config, som er defaultverdi i module()
                    }
                    val response = client.get("$API_BASE_PATH/helloKatt1")
                    response.status shouldBe HttpStatusCode.Unauthorized
                }
            }
        }
        test("metode 3") {
            withMockOAuth2Server {
                testApplication {

                    application {
                        val overrides =
                            MapApplicationConfig().apply {
                                put("AZURE_APP_WELL_KNOWN_URL", wellKnownUrl("default").toString())
                                put("AZURE_APP_CLIENT_ID", "default")
                            }

                        val config = CompositeApplicationConfig(overrides, ApplicationConfig("application.conf"))
                        module(config) // ren injection
                    }
                    val response = client.get("$API_BASE_PATH/helloKatt1")
                    response.status shouldBe HttpStatusCode.Unauthorized
                }
            }
        }
    },
)

private fun MockOAuth2Server.tokenFromDefaultProvider() =
    issueToken(
        issuerId = "default",
        clientId = "default",
        tokenCallback = DefaultOAuth2TokenCallback(),
    ).serialize()

private fun MockOAuth2Server.authConfig(config: ApplicationConfig) =
    MapOverridingConfigSource(
        mapOf(
            "AZURE_APP_WELL_KNOWN_URL" to wellKnownUrl("default").toString(),
            "AZURE_APP_CLIENT_ID" to "default",
        ),
        configSourceFrom(config),
    )
