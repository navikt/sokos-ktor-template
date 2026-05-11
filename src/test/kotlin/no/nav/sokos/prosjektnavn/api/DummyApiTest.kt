package no.nav.sokos.prosjektnavn.api

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

private const val PORT = 9091

private lateinit var server: EmbeddedServer<NettyApplicationEngine, NettyApplicationEngine.Configuration>

private val validationFilter = OpenApiValidationFilter("openapi/sokos-ktor-template-v1-swagger.yaml")
private val dummyService = mockk<DummyService>()

internal class DummyApiTest :
    FunSpec({

        beforeTest {
            server = embeddedServer(Netty, PORT, module = Application::applicationTestModule).start()
        }

        afterTest {
            server.stop(5, 5)
        }

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
    })

private fun Application.applicationTestModule() {
    commonConfig()
    routing {
        authenticate(false, AUTHENTICATION_NAME) {
            dummyApi(dummyService)
        }
    }
}
