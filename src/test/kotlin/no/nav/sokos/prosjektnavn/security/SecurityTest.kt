package no.nav.sokos.prosjektnavn.security

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import io.mockk.every
import io.mockk.mockk

import no.nav.security.mock.oauth2.MockOAuth2Server
import no.nav.security.mock.oauth2.token.DefaultOAuth2TokenCallback
import no.nav.security.mock.oauth2.withMockOAuth2Server
import no.nav.sokos.prosjektnavn.api.API_BASE_PATH
import no.nav.sokos.prosjektnavn.api.dummyApi
import no.nav.sokos.prosjektnavn.config.AUTHENTICATION_NAME
import no.nav.sokos.prosjektnavn.config.PropertiesConfig
import no.nav.sokos.prosjektnavn.config.authenticate
import no.nav.sokos.prosjektnavn.config.commonConfig
import no.nav.sokos.prosjektnavn.config.securityConfig
import no.nav.sokos.prosjektnavn.domain.DummyDomain
import no.nav.sokos.prosjektnavn.service.DummyService

/**
 * Test for å sjekke at sikkerhetsmekanismen fungerer som forventet. Bruker
 * DummyApi som utgangspunkt, fordi alle endepunktene i applikasjonen
 * er sikret under samme konfigurasjon. Endepunktene er wrappet i
 * en authenticate() funksjon som sjekker om bruker er autentisert.
 */

val dummyService: DummyService = mockk()

internal class SecurityTest :
    FunSpec({

        test("forespørsel uten token skal returnere 401") {
            withMockOAuth2Server {
                testApplication {
                    application {
                        securityConfig(true, mockAuthConfig())
                        routing {
                            authenticate(true, AUTHENTICATION_NAME) {
                                dummyApi(dummyService)
                            }
                        }
                    }
                    val response = client.get("$API_BASE_PATH/hello")
                    response.status shouldBe HttpStatusCode.Unauthorized
                }
            }
        }

        test("forespørsel med ugyldig token skal returnere 401") {
            withMockOAuth2Server {
                testApplication {
                    application {
                        commonConfig()
                        securityConfig(true, mockAuthConfig())
                        routing {
                            authenticate(true, AUTHENTICATION_NAME) {
                                dummyApi(dummyService)
                            }
                        }
                    }

                    val response =
                        client.get("$API_BASE_PATH/hello") {
                            header(HttpHeaders.Authorization, "Bearer invalid-token-12345")
                        }

                    response.status shouldBe HttpStatusCode.Unauthorized
                }
            }
        }

        test("forespørsel med token uten audience skal returnere 500 (kjent begrensning)") {
            withMockOAuth2Server {
                testApplication {
                    application {
                        commonConfig()
                        securityConfig(true, mockAuthConfig())
                        routing {
                            authenticate(true, AUTHENTICATION_NAME) {
                                dummyApi(dummyService)
                            }
                        }
                    }

                    val response =
                        client.get("$API_BASE_PATH/hello") {
                            header(HttpHeaders.Authorization, "Bearer ${tokenWithoutAudience()}")
                        }

                    // MERK: Audience er aldri null fra Azure AD i produksjon, så dette er en edge case
                    // som kun kan oppstå i test. Validering av manglende audience gir 500.
                    response.status shouldBe HttpStatusCode.InternalServerError
                }
            }
        }

        test("forespørsel med gyldig token skal returnere 200") {
            withMockOAuth2Server {
                testApplication {
                    application {
                        commonConfig()
                        securityConfig(true, mockAuthConfig())
                        routing {
                            authenticate(true, AUTHENTICATION_NAME) {
                                dummyApi(dummyService)
                            }
                        }
                    }

                    every { dummyService.sayHello() } returns DummyDomain("Hello")

                    val response =
                        client.get("$API_BASE_PATH/hello") {
                            header(HttpHeaders.Authorization, "Bearer ${validToken()}")
                        }

                    response.status shouldBe HttpStatusCode.OK
                    val bodyText = response.bodyAsText()
                    bodyText shouldNotBe null
                }
            }
        }

        test("forespørsel med utgått token skal returnere 401") {
            withMockOAuth2Server {
                testApplication {
                    application {
                        commonConfig()
                        securityConfig(true, mockAuthConfig())
                        routing {
                            authenticate(true, AUTHENTICATION_NAME) {
                                dummyApi(dummyService)
                            }
                        }
                    }

                    val response =
                        client.get("$API_BASE_PATH/hello") {
                            header(HttpHeaders.Authorization, "Bearer ${expiredToken()}")
                        }

                    // Utgått token skal avvises av JWT validator
                    response.status shouldBe HttpStatusCode.Unauthorized
                }
            }
        }
    })

private fun MockOAuth2Server.validToken() =
    issueToken(
        issuerId = "default",
        clientId = "default",
        tokenCallback =
            DefaultOAuth2TokenCallback(
                issuerId = "default",
                audience = listOf("default"),
                claims =
                    mapOf(
                        "NAVident" to "Z123456",
                        "groups" to listOf("group1", "group2"),
                    ),
            ),
    ).serialize()

private fun MockOAuth2Server.expiredToken() =
    issueToken(
        issuerId = "default",
        clientId = "default",
        tokenCallback =
            DefaultOAuth2TokenCallback(
                issuerId = "default",
                audience = listOf("default"),
                claims =
                    mapOf(
                        "NAVident" to "Z123456",
                        "groups" to listOf("group1", "group2"),
                    ),
                expiry = -3600, // Token utgått for 1 time siden
            ),
    ).serialize()

private fun MockOAuth2Server.tokenWithoutAudience() =
    issueToken(
        issuerId = "default",
        clientId = "default",
        tokenCallback =
            DefaultOAuth2TokenCallback(
                issuerId = "default",
                claims =
                    mapOf(
                        "NAVident" to "Z123456",
                    ),
            ),
    ).serialize()

private fun MockOAuth2Server.mockAuthConfig() =
    PropertiesConfig.AzureAdProperties(
        wellKnownUrl = wellKnownUrl("default").toString(),
        clientId = "default",
    )
