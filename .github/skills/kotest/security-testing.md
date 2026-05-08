# Sikkerhetstesting med MockOAuth2Server

Alle endepunkter i applikasjonen er sikret via `authenticate(useAuthentication, AUTHENTICATION_NAME)`.
Sikkerhetstester bruker `withMockOAuth2Server { testApplication { ... } }` for å verifisere JWT-validering.

## Grunnmønster

```kotlin
internal class SecurityTest : FunSpec({

    beforeSpec {
        PropertiesConfig.load(ApplicationConfig("application-test.conf"))
    }

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
})
```

## `mockAuthConfig()` — koble mock-server til securityConfig

```kotlin
private fun MockOAuth2Server.mockAuthConfig() =
    AzureAdProperties(
        wellKnownUrl = wellKnownUrl("default").toString(),
        clientId = "default",
    )
```

> `wellKnownUrl("default")` returnerer URL-en til mock-serverens `.well-known`-endepunkt for issuer `"default"`.
> `securityConfig` bruker denne til å hente JWKS og validere tokens.

## Token-hjelpere

```kotlin
// Gyldig token med audience og NAVident
private fun MockOAuth2Server.validToken() =
    issueToken(
        issuerId = "default",
        clientId = "default",
        tokenCallback = DefaultOAuth2TokenCallback(
            issuerId = "default",
            audience = listOf("default"),
            claims = mapOf(
                "NAVident" to "Z123456",
                "groups" to listOf("group1", "group2"),
            ),
        ),
    ).serialize()

// Utgått token (expiry = negativt antall sekunder fra nå)
private fun MockOAuth2Server.expiredToken() =
    issueToken(
        issuerId = "default",
        clientId = "default",
        tokenCallback = DefaultOAuth2TokenCallback(
            issuerId = "default",
            audience = listOf("default"),
            claims = mapOf("NAVident" to "Z123456"),
            expiry = -3600,
        ),
    ).serialize()

// Token uten audience (edge case — gir 500, ikke 401)
private fun MockOAuth2Server.tokenWithoutAudience() =
    issueToken(
        issuerId = "default",
        clientId = "default",
        tokenCallback = DefaultOAuth2TokenCallback(
            issuerId = "default",
            claims = mapOf("NAVident" to "Z123456"),
        ),
    ).serialize()
```

## Komplett testscenario-sett

```kotlin
test("forespørsel med ugyldig token skal returnere 401") {
    withMockOAuth2Server {
        testApplication {
            application {
                commonConfig()
                securityConfig(true, mockAuthConfig())
                routing {
                    authenticate(true, AUTHENTICATION_NAME) { dummyApi(dummyService) }
                }
            }
            val response = client.get("$API_BASE_PATH/hello") {
                header(HttpHeaders.Authorization, "Bearer invalid-token-12345")
            }
            response.status shouldBe HttpStatusCode.Unauthorized
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
                    authenticate(true, AUTHENTICATION_NAME) { dummyApi(dummyService) }
                }
            }
            every { dummyService.sayHello() } returns DummyDomain("Hello")

            val response = client.get("$API_BASE_PATH/hello") {
                header(HttpHeaders.Authorization, "Bearer ${validToken()}")
            }
            response.status shouldBe HttpStatusCode.OK
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
                    authenticate(true, AUTHENTICATION_NAME) { dummyApi(dummyService) }
                }
            }
            val response = client.get("$API_BASE_PATH/hello") {
                header(HttpHeaders.Authorization, "Bearer ${expiredToken()}")
            }
            response.status shouldBe HttpStatusCode.Unauthorized
        }
    }
}

// MERK: Token uten audience gir 500, ikke 401.
// Azure AD utsteder alltid audience i produksjon — dette er kun en test-edge-case.
test("forespørsel med token uten audience skal returnere 500") {
    withMockOAuth2Server {
        testApplication {
            application {
                commonConfig()
                securityConfig(true, mockAuthConfig())
                routing {
                    authenticate(true, AUTHENTICATION_NAME) { dummyApi(dummyService) }
                }
            }
            val response = client.get("$API_BASE_PATH/hello") {
                header(HttpHeaders.Authorization, "Bearer ${tokenWithoutAudience()}")
            }
            response.status shouldBe HttpStatusCode.InternalServerError
        }
    }
}
```

## API-testing uten autentisering

For tester der du vil teste API-logikken isolert (ikke sikkerhet), deaktiver auth:

```kotlin
internal class DummyApiTest : FunSpec({

    beforeSpec {
        PropertiesConfig.load(ApplicationConfig("application-test.conf"))
        // application-test.conf setter useAuthentication = false
    }

    test("GET /api/v1/hello returnerer DummyDomain") {
        testApplication {
            application {
                commonConfig()
                routing { dummyApi(dummyService) }
                // Ingen securityConfig — auth er av i test-config
            }
            every { dummyService.sayHello() } returns DummyDomain("Hello")

            val response = client.get("$API_BASE_PATH/hello")
            response.status shouldBe HttpStatusCode.OK
        }
    }
})
```

## Nødvendige imports

```kotlin
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import io.mockk.every
import io.mockk.mockk
import no.nav.security.mock.oauth2.MockOAuth2Server
import no.nav.security.mock.oauth2.token.DefaultOAuth2TokenCallback
import no.nav.security.mock.oauth2.withMockOAuth2Server
import no.nav.sokos.prosjektnavn.config.AUTHENTICATION_NAME
import no.nav.sokos.prosjektnavn.config.AzureAdProperties
import no.nav.sokos.prosjektnavn.config.PropertiesConfig
import no.nav.sokos.prosjektnavn.config.authenticate
import no.nav.sokos.prosjektnavn.config.commonConfig
import no.nav.sokos.prosjektnavn.config.securityConfig
```
