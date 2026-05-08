---
name: kotest
description: "Skriv og endre tester for Ktor-tjenester med Kotest FunSpec, MockK og mock-oauth2-server. Bruk for BehaviorSpec-mønstre, testApplication-oppsett, JWT-testing og RBAC-testing. Aksepterer spørsmål på norsk og engelsk. (Tester, Kotest, FunSpec, BehaviorSpec, MockK, testApplication)"
---

# Kotest — testmønstre for sokos-ktor-template

Bruk denne skillen når du skal skrive tester for Ktor-tjenester i dette prosjektet.

## Stack

```
io.kotest:kotest-runner-junit5          // test-runner
io.kotest:kotest-assertions-core-jvm   // shouldBe, shouldNotBe osv.
io.ktor:ktor-server-test-host-jvm      // testApplication { }
io.mockk:mockk                          // mock av services
no.nav.security:mock-oauth2-server      // JWT-tokens i test
```

## Teststil: FunSpec

Alle tester i dette prosjektet bruker `FunSpec` — ikke `BehaviorSpec`:

```kotlin
internal class DummyApiTest : FunSpec({

    beforeSpec {
        PropertiesConfig.load(ApplicationConfig("application-test.conf"))
    }

    test("GET /api/v1/hello skal returnere 200 OK") {
        testApplication {
            application {
                commonConfig()
                routing { dummyApi() }
            }
            val response = client.get("$API_BASE_PATH/hello")
            response.status shouldBe HttpStatusCode.OK
        }
    }
})
```

## Ktor-konfig i tester

Tester må laste `PropertiesConfig` manuelt — det skjer ikke automatisk i testmiljøet:

```kotlin
beforeSpec {
    PropertiesConfig.load(ApplicationConfig("application-test.conf"))
}
```

`application-test.conf` setter:
- `profile = TEST`
- `useAuthentication = false`
- `azureAd.clientId = "test-client-id"`

> **Viktig:** `PropertiesConfig.load()` er idempotent — kaller du den igjen med samme config skjer ingenting (guard `if (!::config.isInitialized)`).

## Sub-filer

| Fil | Innhold |
|-----|---------|
| [security-testing.md](security-testing.md) | Autentisering og JWT-testing med `withMockOAuth2Server` |
| [rbac-testing.md](rbac-testing.md) | RBAC-tester med `context()`-blokker: OBO (scope), M2M (role), cross-contamination |
| [unit-testing.md](unit-testing.md) | Enhetstester, konfig-tester og MockK-mønstre |

## Boundaries

### ✅ Always
- Bruk `FunSpec` (ikke `BehaviorSpec`)
- Last `PropertiesConfig` i `beforeSpec` for tester som trenger konfig
- Bruk `internal class` for testklasser
- Bruk `mockk<T>()` for avhengigheter, ikke `spyk`

### ⚠️ Ask First
- `BehaviorSpec` (akseptabelt for komplekse scenarier med mange kontekster)

### 🚫 Never
- Hard-kode tokens eller secrets i tester
- Bruk `@Autowired` eller Spring-annotasjoner (dette er Ktor, ikke Spring)
- Start ekte HTTP-server i tester — bruk `testApplication { }`
