package no.nav.sokos.prosjektnavn.security

import kotlinx.serialization.json.Json

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.testApplication

import no.nav.security.mock.oauth2.MockOAuth2Server
import no.nav.security.mock.oauth2.token.DefaultOAuth2TokenCallback
import no.nav.security.mock.oauth2.withMockOAuth2Server
import no.nav.sokos.prosjektnavn.API_BASE_PATH
import no.nav.sokos.prosjektnavn.ConfigAttributeKey
import no.nav.sokos.prosjektnavn.config.PropertiesConfig
import no.nav.sokos.prosjektnavn.module
import no.nav.sokos.prosjektnavn.util.CompositeApplicationConfig
import no.nav.sokos.prosjektnavn.util.MapOverridingConfigSource
import no.nav.sokos.prosjektnavn.util.configSourceFrom

class SecurityTest : FunSpec({

    test("test http GET endepunkt uten token bør returnere 401") {
        withMockOAuth2Server {
            //    val wellKnownUrl = this.wellKnownUrl("default").toString()

            testApplication {
                environment {
                    config = ApplicationConfig("application.conf")
                }

                application {
                    val config = PropertiesConfig.Configuration(authConfig(environment.config))
                    attributes.put(ConfigAttributeKey, config)
                    module() // will now read from application.conf
                }
                val response = client.get("$API_BASE_PATH/helloKatt1")
                response.status shouldBe HttpStatusCode.Unauthorized
            }
        }
    }

    test("test http GET endepunkt med token bør returnere 200") {
        withMockOAuth2Server {
            val mockOAuth2Server = this
            testApplication {
                val client =
                    createClient {
                        install(ContentNegotiation) {
                            json(
                                Json {
                                    prettyPrint = true
                                    ignoreUnknownKeys = true
                                    encodeDefaults = true
                                    explicitNulls = false
                                },
                            )
                        }
                    }
                environment {
                    val overrides =
                        MapApplicationConfig().apply {
                            put("AZURE_APP_WELL_KNOWN_URL", wellKnownUrl("default").toString())
                            put("AZURE_APP_CLIENT_ID", "default")
                        }

                    config = CompositeApplicationConfig(overrides, ApplicationConfig("application.conf"))
                }

                application {
                    module(environment.config) // bruker composite. Eksempel på injection
                }
                val response =
                    client.get("$API_BASE_PATH/helloKatt1") {
                        header("Authorization", "Bearer ${mockOAuth2Server.tokenFromDefaultProvider()}")
                        contentType(ContentType.Application.Json)
                    }

                response.status shouldBe HttpStatusCode.OK
                println(response.bodyAsText())

                val response2 =
                    client.get("$API_BASE_PATH/helloKatt2") {
                        header("Authorization", "Bearer ${mockOAuth2Server.tokenFromDefaultProvider()}")
                        contentType(ContentType.Application.Json)
                    }

                println(response2.bodyAsText())
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

private fun MockOAuth2Server.authConfig(config: ApplicationConfig) =
    MapOverridingConfigSource(
        mapOf(
            "AZURE_APP_WELL_KNOWN_URL" to wellKnownUrl("default").toString(),
            "AZURE_APP_CLIENT_ID" to "default",
        ),
        configSourceFrom(config),
    )
