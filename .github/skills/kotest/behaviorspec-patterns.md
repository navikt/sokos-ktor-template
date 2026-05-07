# BehaviorSpec Patterns

## Canonical BehaviorSpec

```kotlin
class MyServiceTest : BehaviorSpec({
    val repositoryMock = mockk<MyRepository> {
        justRun { saveResults(any<List<ProcessingResult>>()) }
    }

    Given("en ny forespørsel som skal behandles") {
        val request = mockk<MyRequest>(relaxed = true) {
            every { id } returns "test-id"
            every { type } returns "STANDARD"
        }

        When("ekstern tjeneste svarer med 200 OK") {
            val externalClientMock = mockk<ExternalClient> {
                coEvery { submit(any(), any()) } returns
                    HttpResponse(status = HttpStatusCode.OK, body = """{"ref": "123"}""")
            }
            val service = MyService(externalClientMock, repositoryMock)

            val results = service.processAll(listOf(request))

            Then("skal det returneres ett resultat med status COMPLETED") {
                results.shouldHaveSize(1)
                results.first().status shouldBe Status.COMPLETED
                results.first().reference shouldBe "123"
            }
            And("repository skal lagre resultatet") {
                verify(exactly = 1) { repositoryMock.saveResults(any<List<ProcessingResult>>()) }
            }
        }
    }
})
```

## Conventions

- Norwegian Given/When/Then/And text
- Test data in `Given`, scenario-specific mocks in `When`, shared fixtures on spec top-level
- One `When` per causal step; multiple `Then`/`And` for independent assertions
- Never `runBlocking` inside test blocks — use `coEvery` / `coVerify`
