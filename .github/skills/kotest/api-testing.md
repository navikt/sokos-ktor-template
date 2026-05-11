# API-testing med RestAssured og OpenAPI-validering

API-tester kjører mot en ekte Netty-server på en lokal port. `OpenApiValidationFilter` verifiserer automatisk at alle requests og responses samsvarer med OpenAPI-spesifikasjonen i `src/main/resources/openapi/`.

**Bruk dette mønsteret for:** logikktester, statuskoder, responsbody, feilhåndtering.
**Bruk ikke dette mønsteret for:** JWT-validering — se [security-testing.md](security-testing.md).

## Oppsett

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
})
```

> `beforeTest`/`afterTest` — ikke `beforeSpec`/`afterSpec` — sikrer at serveren startes og stoppes per test, slik at mockk-state ikke lekker mellom scenarier.

## `applicationTestModule()` — testmodul uten auth

Auth deaktiveres direkte i testmodulen — les **ikke** fra `PropertiesConfig`:

```kotlin
private fun Application.applicationTestModule() {
    commonConfig()
    routing {
        authenticate(false, AUTHENTICATION_NAME) {
            dummyApi(dummyService)
        }
    }
}
```

## Testscenarier

### Vellykket respons

```kotlin
test("GET /hello returnerer 200 OK med DummyDomain") {
    val expected = DummyDomain("This is a template for Team Motta og Beregne")
    every { dummyService.sayHello() } returns expected

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
```

### Feilhåndtering

```kotlin
test("GET /hello returnerer 500 Internal Server Error når service kaster exception") {
    every { dummyService.sayHello() } throws RuntimeException("Noe gikk galt")

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
            .statusCode(HttpStatusCode.InternalServerError.value)
            .extract()
            .response()

    Json.decodeFromString<ApiError>(response.asString()) shouldBe
        ApiError(
            status = HttpStatusCode.InternalServerError.value,
            error = HttpStatusCode.InternalServerError.description,
            message = "Noe gikk galt",
            path = "$API_BASE_PATH/hello",
            timestamp = Instant.parse(response.jsonPath().getString("timestamp")),
        )
}
```

## `validationFilter` — hva den sjekker automatisk

`OpenApiValidationFilter` validerer at:
- Request-path, metode og parametere finnes i spesifikasjonen
- Request-body samsvarer med schema
- Response-statuskode er definert i spesifikasjonen
- Response-body samsvarer med schema

Testen feiler automatisk hvis noe avviker fra OpenAPI-spec — ingen ekstra assertions trengs for schema-validering.

## Port-konflikter

Bruk en dedikert port per testklasse (f.eks. `9091`, `9092`) for å unngå konflikter ved parallell kjøring. Sett aldri port `8080` (produksjonsporten).

## Nødvendige imports

```kotlin
import kotlin.time.Instant
import kotlinx.serialization.json.Json

import com.atlassian.oai.validator.restassured.OpenApiValidationFilter
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import io.ktor.server.routing.routing
import io.mockk.every
import io.mockk.mockk
import io.restassured.RestAssured
import no.nav.sokos.prosjektnavn.config.AUTHENTICATION_NAME
import no.nav.sokos.prosjektnavn.config.ApiError
import no.nav.sokos.prosjektnavn.config.authenticate
import no.nav.sokos.prosjektnavn.config.commonConfig
import no.nav.sokos.prosjektnavn.domain.DummyDomain
import no.nav.sokos.prosjektnavn.service.DummyService
```
