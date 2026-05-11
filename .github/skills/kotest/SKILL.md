---
name: kotest
description: "Skriv og endre tester for Ktor-tjenester med Kotest FunSpec, MockK og mock-oauth2-server. Bruk for sikkerhetstester (testApplication + JWT), API-tester (embeddedServer + RestAssured + OpenAPI-validering) og RBAC-testing. Aksepterer spørsmål på norsk og engelsk. (Tester, Kotest, FunSpec, BehaviorSpec, MockK, testApplication, RestAssured)"
---

# Kotest — testmønstre for sokos-ktor-template

Bruk denne skillen når du skal skrive tester for Ktor-tjenester i dette prosjektet.

## Stack

```
io.kotest:kotest-runner-junit5          // test-runner
io.kotest:kotest-assertions-core-jvm   // shouldBe, shouldNotBe osv.
io.ktor:ktor-server-test-host-jvm      // testApplication { } — sikkerhetstester
io.ktor:ktor-server-netty-jvm          // embeddedServer — API-tester med OpenAPI-validering
io.mockk:mockk                          // mock av services
no.nav.security:mock-oauth2-server      // JWT-tokens i test
com.atlassian.oai:swagger-request-validator-restassured  // OpenAPI-validering mot swagger-spec
io.rest-assured:rest-assured            // HTTP-klient for API-tester
```

## To testmønstre

Det brukes to distinkte mønstre, avhengig av hva du tester:

### 1. Sikkerhetstester — `testApplication { }`

Bruk `testApplication { }` fra `ktor-server-test-host-jvm` for å teste JWT-autentisering (ikke HTTP-server på port):

```kotlin
internal class SecurityTest : FunSpec({

    beforeSpec {
        PropertiesConfig.load(ApplicationConfig(TestUtil.APPLICATION_TEST_CONFIG))
    }

    test("forespørsel uten token skal returnere 401") {
        withMockOAuth2Server {
            testApplication {
                application {
                    securityConfig(mockAuthConfig())
                    routing {
                        authenticate(PropertiesConfig.applicationProperties.useAuthentication, AUTHENTICATION_NAME) {
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

### 2. API-tester med OpenAPI-validering — `embeddedServer` + RestAssured

Bruk ekte Netty-server med RestAssured og `OpenApiValidationFilter` for å verifisere at responser samsvarer med swagger-spesifikasjonen:

```kotlin
private const val PORT = 9091
private lateinit var server: EmbeddedServer<NettyApplicationEngine, NettyApplicationEngine.Configuration>
private val validationFilter = OpenApiValidationFilter("openapi/sokos-ktor-template-v1-swagger.yaml")
private val dummyService = mockk<DummyService>()

internal class DummyApiTest : FunSpec({

    beforeTest {
        server = embeddedServer(Netty, PORT, module = Application::applicationTestModule).start()
    }

    afterTest {
        server.stop(5, 5)
    }

    test("GET /hello returnerer 200 OK med DummyDomain") {
        every { dummyService.sayHello() } returns DummyDomain("This is a template")

        val response =
            RestAssured
                .given()
                .filter(validationFilter)
                .header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                .header(HttpHeaders.Authorization, "Bearer test-token")
                .port(PORT)
                .get("$API_BASE_PATH/hello")
                .then()
                .assertThat()
                .statusCode(HttpStatusCode.OK.value)
                .extract()
                .response()

        Json.decodeFromString<DummyDomain>(response.body.asString()) shouldBe expected
    }
})

private fun Application.applicationTestModule() {
    commonConfig()
    routing {
        authenticate(false, AUTHENTICATION_NAME) {
            dummyApi(dummyService)
        }
    }
}
```

> **Viktig:** `applicationTestModule()` setter `authenticate(false, ...)` direkte — den leser ikke fra `PropertiesConfig`.
> `validationFilter` verifiserer at request/response samsvarer med OpenAPI-spesifikasjonen automatisk.

## Sub-filer

| Fil | Innhold |
|-----|---------|
| [security-testing.md](security-testing.md) | JWT-autentisering og sikkerhetstesting med `withMockOAuth2Server` + `testApplication` |
| [api-testing.md](api-testing.md) | API-logikktester med `embeddedServer(Netty)` + RestAssured + `OpenApiValidationFilter` |
| [rbac-testing.md](rbac-testing.md) | RBAC-tester med `context()`-blokker: OBO (scope), M2M (role), cross-contamination |
| [unit-testing.md](unit-testing.md) | Enhetstester, konfig-tester og MockK-mønstre |

## Boundaries

### ✅ Always
- Bruk `FunSpec` (ikke `BehaviorSpec`)
- Bruk `testApplication { }` for sikkerhetstester (SecurityTest)
- Bruk `embeddedServer(Netty, PORT)` + RestAssured + `OpenApiValidationFilter` for API-tester (DummyApiTest)
- Bruk `internal class` for testklasser
- Bruk `mockk<T>()` for avhengigheter, ikke `spyk`

### ⚠️ Ask First
- `BehaviorSpec` (akseptabelt for komplekse scenarier med mange kontekster)

### 🚫 Never
- Hard-kode tokens eller secrets i tester
- Bruk `@Autowired` eller Spring-annotasjoner (dette er Ktor, ikke Spring)
- Last `PropertiesConfig` i `beforeSpec` for API-tester (DummyApiTest) — auth styres via `applicationTestModule()`
