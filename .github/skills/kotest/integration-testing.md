# Integration Testing

## Database integration tests

Use a `TestListener` that manages a TestContainers database instance. Example pattern:

```kotlin
internal class MyServiceIntegrationTest : BehaviorSpec({
    extensions(DBListener)
    beforeEach { CircuitBreakerManager.circuitBreaker.reset() }

    val dbService = MyDatabaseService(DBListener.dataSource)

    Given("2 ubehandlede oppføringer i databasen") {
        DBListener.clearDB()
        DBListener.loadInitScript("SQLscript/two-pending-entries.sql")

        val entries = dbService.getAllPending()
        entries.shouldHaveSize(2)

        When("ekstern tjeneste svarer med OK") {
            val httpClient = MockHttpClient.client(
                MockResponse(Endpoint.SUBMIT, successResponse("ref-123"), HttpStatusCode.OK),
            )
            val externalClient = ExternalClient("", httpClient, mockk(relaxed = true))
            val results = MyService(externalClient, dbService).processAll(entries)

            Then("alle oppføringer skal være behandlet") {
                results.shouldHaveSize(2)
                dbService.getAllPending().shouldBeEmpty()
            }
        }
    }
})
```

### Typical DBListener API

| Helper | Purpose |
|---|---|
| `extensions(DBListener)` | Registers TestContainers PostgreSQL |
| `DBListener.dataSource` | `HikariDataSource` with Flyway migrations applied |
| `DBListener.clearDB()` | `TRUNCATE ... RESTART IDENTITY CASCADE` |
| `DBListener.loadInitScript("SQLscript/...")` | Loads fixture from `src/test/resources/` |

Always `clearDB()` before `loadInitScript(...)` inside each `Given` — listener state leaks between scenarios otherwise.

## SFTP integration tests

```kotlin
internal class FtpServiceIntegrationTest : BehaviorSpec({
    extensions(SftpListener)
    // ...
})
```

## Mock HTTP clients

Build a mock HTTP client that matches by endpoint path and optionally installs plugins (e.g. circuit breaker) so tests exercise production wiring:

```kotlin
val client = MockHttpClient.client(
    MockResponse(Endpoint.SUBMIT, successResponse("ref-123"), HttpStatusCode.OK),
    MockResponse(Endpoint.STATUS, statusResponse(), HttpStatusCode.OK),
)
```

For unit tests that don't need HTTP wiring, mock the client class directly:

```kotlin
val externalClientMock = mockk<ExternalClient> {
    coEvery { submit(any(), any()) } returns mockHttpResponse(body = successResponse("123"))
}
```

## Circuit breaker

Every test that eventually reaches an external HTTP client with a circuit breaker must reset it — otherwise state leaks between tests:

```kotlin
beforeEach { CircuitBreakerManager.circuitBreaker.reset() }
```

When an open breaker is expected (e.g. after multiple error responses), verify with `coVerify(exactly = 1) { ... }` that further calls were suppressed.
