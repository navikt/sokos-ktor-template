# RBAC-sikkerhetstesting med context()-blokker

> **Forutsetning:** Appen har RBAC implementert via `azure-rbac-ktor`-skillen (se `.github/skills/azure-rbac-ktor/`).
> Bruk denne filen som mal når du skal teste `requireScope()` (OBO) og `requireRole()` (M2M) i endepunktene dine.

Referanse: [sokos-spk-mottak/SecurityTest.kt](https://github.com/navikt/sokos-spk-mottak/blob/main/src/test/kotlin/no/nav/sokos/spk/mottak/security/SecurityTest.kt)

---

## Teststruktur med `context()`-blokker

Del sikkerhetstester inn i fire kontekster:

| Context | Hva testes |
|---------|-----------|
| `Authentication` | Ingen token / ugyldig token → 401 |
| `OBO Pattern (requireScope)` | Saksbehandler-token med/uten riktig scope → 403 / 200 |
| `M2M Pattern (requireRole)` | System-token med/uten riktig rolle → 403 / 200 |
| `Cross-Contamination` | OBO-token på M2M-endepunkt (og vice versa) → 403 |

---

## Komplett mal

```kotlin
internal class SecurityTest : FunSpec({

    val myService = mockk<MyService>()

    beforeSpec {
        // Last Ktor-konfig manuelt — skjer ikke automatisk i testmiljøet
        PropertiesConfig.load(ApplicationConfig(TestUtil.APPLICATION_TEST_CONFIG))
    }

    context("Authentication - Token Validation (401 Unauthorized)") {

        test("GET endpoint uten token skal returnere 401") {
            withMockOAuth2Server {
                testApplication {
                    application {
                        securityConfig(mockAuthConfig())
                        routing { myApi(myService) }
                    }
                    val response = client.get("$API_BASE_PATH/resource")
                    response.status shouldBe HttpStatusCode.Unauthorized
                }
            }
        }

        test("GET endpoint med ugyldig token skal returnere 401") {
            withMockOAuth2Server {
                testApplication {
                    application {
                        securityConfig(mockAuthConfig())
                        routing { myApi(myService) }
                    }
                    val response = client.get("$API_BASE_PATH/resource") {
                        header("Authorization", "Bearer invalid-token")
                    }
                    response.status shouldBe HttpStatusCode.Unauthorized
                }
            }
        }
    }

    context("Authorization - OBO Pattern (requireScope)") {
        // OBO = On-Behalf-Of. Brukes av saksbehandlere via Azure AD.
        // Token inneholder "scp"-claim med liste over scopes.

        test("OBO token uten required scope skal returnere 403 Forbidden") {
            withMockOAuth2Server {
                val mockOAuth2Server = this
                testApplication {
                    val client = createClient {
                        install(ContentNegotiation) { json() }
                    }
                    application {
                        commonConfig()
                        securityConfig(mockAuthConfig())
                        routing { myApi(myService) }
                    }
                    val response = client.get("$API_BASE_PATH/resource") {
                        header("Authorization", "Bearer ${mockOAuth2Server.oboTokenWithoutRequiredScope()}")
                    }
                    response.status shouldBe HttpStatusCode.Forbidden
                    val apiError = response.body<ApiError>()
                    apiError.status shouldBe HttpStatusCode.Forbidden.value
                    apiError.message shouldContain "Missing required scope"
                }
            }
        }

        test("OBO token med required scope skal returnere 200 OK") {
            every { myService.getResource() } returns emptyList()

            withMockOAuth2Server {
                val mockOAuth2Server = this
                testApplication {
                    val client = createClient {
                        install(ContentNegotiation) { json() }
                    }
                    application {
                        commonConfig()
                        securityConfig(mockAuthConfig())
                        routing { myApi(myService) }
                    }
                    val response = client.get("$API_BASE_PATH/resource") {
                        header("Authorization", "Bearer ${mockOAuth2Server.oboTokenWithRequiredScope()}")
                    }
                    response.status shouldBe HttpStatusCode.OK
                }
            }
        }
    }

    context("Authorization - M2M Pattern (requireRole)") {
        // M2M = Machine-to-Machine. Brukes av andre tjenester via client_credentials.
        // Token inneholder "roles"-claim med liste over app-roller.
        // M2M-tokens har IKKE "NAVident".

        test("M2M token uten required role skal returnere 403 Forbidden") {
            withMockOAuth2Server {
                val mockOAuth2Server = this
                testApplication {
                    val client = createClient {
                        install(ContentNegotiation) { json() }
                    }
                    application {
                        commonConfig()
                        securityConfig(mockAuthConfig())
                        routing { myApi(myService) }
                    }
                    val response = client.get("$API_BASE_PATH/m2m-resource") {
                        header("Authorization", "Bearer ${mockOAuth2Server.m2mTokenWithoutRequiredRole()}")
                    }
                    response.status shouldBe HttpStatusCode.Forbidden
                    val apiError = response.body<ApiError>()
                    apiError.status shouldBe HttpStatusCode.Forbidden.value
                    apiError.message shouldContain "Missing required role"
                }
            }
        }

        test("M2M token med required role skal returnere 200 OK") {
            every { myService.getM2MResource() } returns emptyList()

            withMockOAuth2Server {
                val mockOAuth2Server = this
                testApplication {
                    val client = createClient {
                        install(ContentNegotiation) { json() }
                    }
                    application {
                        commonConfig()
                        securityConfig(mockAuthConfig())
                        routing { myApi(myService) }
                    }
                    val response = client.get("$API_BASE_PATH/m2m-resource") {
                        header("Authorization", "Bearer ${mockOAuth2Server.m2mTokenWithRequiredRole()}")
                    }
                    response.status shouldBe HttpStatusCode.OK
                }
            }
        }
    }

    context("Authorization - Cross-Contamination (Token Separation)") {
        // Verifiserer at token-typer IKKE kan brukes på feil endepunkter.
        // OBO-token på M2M-endepunkt → 403 "Missing required role"
        // M2M-token på OBO-endepunkt  → 403 "Missing required scope"

        test("OBO token på M2M-only endpoint skal returnere 403") {
            withMockOAuth2Server {
                val mockOAuth2Server = this
                testApplication {
                    val client = createClient {
                        install(ContentNegotiation) { json() }
                    }
                    application {
                        commonConfig()
                        securityConfig(mockAuthConfig())
                        routing { myApi(myService) }
                    }
                    val response = client.get("$API_BASE_PATH/m2m-resource") {
                        header("Authorization", "Bearer ${mockOAuth2Server.oboTokenWithRequiredScope()}")
                    }
                    response.status shouldBe HttpStatusCode.Forbidden
                    response.body<ApiError>().message shouldContain "Missing required role"
                }
            }
        }

        test("M2M token på OBO-only endpoint skal returnere 403") {
            withMockOAuth2Server {
                val mockOAuth2Server = this
                testApplication {
                    val client = createClient {
                        install(ContentNegotiation) { json() }
                    }
                    application {
                        commonConfig()
                        securityConfig(mockAuthConfig())
                        routing { myApi(myService) }
                    }
                    val response = client.get("$API_BASE_PATH/resource") {
                        header("Authorization", "Bearer ${mockOAuth2Server.m2mTokenWithRequiredRole()}")
                    }
                    response.status shouldBe HttpStatusCode.Forbidden
                    response.body<ApiError>().message shouldContain "Missing required scope"
                }
            }
        }
    }
})
```

---

## Token-hjelpere

```kotlin
// NB: Bruk AzureAdProperties direkte (top-level class i config-pakken).
// spk-mottak bruker PropertiesConfig.AzureAdProperties (nested) — her er det ANNERLEDES.
private fun MockOAuth2Server.mockAuthConfig() =
    AzureAdProperties(
        wellKnownUrl = wellKnownUrl("default").toString(),
        clientId = "default",
    )

// OBO-token: saksbehandler med NAVident og riktig scope
// "scp"-claim inneholder ett scope som string (ikke liste)
private fun MockOAuth2Server.oboTokenWithRequiredScope() =
    issueToken(
        issuerId = "default",
        clientId = "default",
        tokenCallback = DefaultOAuth2TokenCallback(
            issuerId = "default",
            claims = mapOf(
                "NAVident" to "Z123456",
                "scp" to "myapp.read",          // ← tilpass til ditt scope
            ),
        ),
    ).serialize()

// OBO-token: saksbehandler med NAVident men FEIL scope
private fun MockOAuth2Server.oboTokenWithoutRequiredScope() =
    issueToken(
        issuerId = "default",
        clientId = "default",
        tokenCallback = DefaultOAuth2TokenCallback(
            issuerId = "default",
            claims = mapOf(
                "NAVident" to "Z123456",
                "scp" to "other.scope",
            ),
        ),
    ).serialize()

// M2M-token: systembruker med riktig rolle, ingen NAVident
// "roles"-claim er en liste
private fun MockOAuth2Server.m2mTokenWithRequiredRole() =
    issueToken(
        issuerId = "default",
        clientId = "default",
        tokenCallback = DefaultOAuth2TokenCallback(
            issuerId = "default",
            claims = mapOf(
                "roles" to listOf("myapp.read"),  // ← tilpass til din rolle
            ),
        ),
    ).serialize()

// M2M-token: systembruker UTEN riktig rolle
private fun MockOAuth2Server.m2mTokenWithoutRequiredRole() =
    issueToken(
        issuerId = "default",
        clientId = "default",
        tokenCallback = DefaultOAuth2TokenCallback(
            issuerId = "default",
            claims = mapOf(
                "roles" to listOf("other.role"),
            ),
        ),
    ).serialize()
```

---

## Viktige forskjeller: OBO vs M2M

| | OBO (saksbehandler) | M2M (system) |
|---|---|---|
| Claim for tilgang | `scp` (string) | `roles` (liste) |
| Har `NAVident` | ✅ Ja | ❌ Nei |
| Azure AD flow | On-Behalf-Of | client_credentials |
| RBAC-funksjon | `requireScope()` | `requireRole()` |

---

## Nødvendige imports

```kotlin
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import io.mockk.every
import io.mockk.mockk
import no.nav.security.mock.oauth2.MockOAuth2Server
import no.nav.security.mock.oauth2.token.DefaultOAuth2TokenCallback
import no.nav.security.mock.oauth2.withMockOAuth2Server
import no.nav.sokos.prosjektnavn.config.AzureAdProperties
import no.nav.sokos.prosjektnavn.config.PropertiesConfig
import no.nav.sokos.prosjektnavn.config.commonConfig
import no.nav.sokos.prosjektnavn.config.securityConfig
```
