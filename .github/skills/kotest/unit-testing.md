# Enhetstester og konfig-tester

## PropertiesConfig-tester

Test at HOCON-konfig lastes korrekt fra `application-test.conf`:

```kotlin
internal class PropertiesConfigTest : FunSpec({

    beforeSpec {
        PropertiesConfig.load(ApplicationConfig("application-test.conf"))
    }

    test("applicationProperties skal lastes fra application-test.conf") {
        val props = PropertiesConfig.applicationProperties
        props.profile shouldBe Profile.TEST
        props.appName shouldBe "sokos-ktor-template"
        props.namespace shouldBe "okonomi-test"
        props.useAuthentication shouldBe false
        props.isLocal shouldBe false
    }

    test("azureAdProperties skal lastes fra application-test.conf") {
        val props = PropertiesConfig.azureAdProperties
        props.clientId shouldBe "test-client-id"
        props.wellKnownUrl shouldBe "test-well-known-url"
    }

    test("isLocal skal være false for TEST-profil") {
        PropertiesConfig.isLocal shouldBe false
    }
})
```

## MockK — service-mocking

```kotlin
// Lag mock øverst i filen (utenfor testklassen)
val dummyService: DummyService = mockk()

internal class DummyServiceTest : FunSpec({

    test("sayHello returnerer forventet domene-objekt") {
        every { dummyService.sayHello() } returns DummyDomain("Hello")
        val result = dummyService.sayHello()
        result.message shouldBe "Hello"
    }

    test("sayHello kaster exception ved feil") {
        every { dummyService.sayHello() } throws RuntimeException("Noe gikk galt")
        shouldThrow<RuntimeException> {
            dummyService.sayHello()
        }
    }
})
```

## application-test.conf

Struktur for testkonfig (`src/test/resources/application-test.conf`):

```hocon
ktor {
    environment = test
}

application {
    profile = TEST
    appName = "sokos-ktor-template"
    namespace = "okonomi-test"
    useAuthentication = false
}

azureAd {
    clientId = "test-client-id"
    wellKnownUrl = "test-well-known-url"
}
```

> Legg til seksjoner for nye konfig-blokker (f.eks. `postgres`, `kafka`) etter samme mønster.

## TestUtil

Felles hjelpemetoder legges i `TestUtil.kt`:

```kotlin
object TestUtil {
    // Legg til felles testhjelpere her, f.eks.:
    // fun lagTestToken(...): String = ...
    // fun lagTestRequest(...): HttpRequestBuilder = ...
}
```

## Nødvendige imports

```kotlin
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.assertions.throwables.shouldThrow
import io.ktor.server.config.ApplicationConfig
import io.mockk.every
import io.mockk.mockk
import no.nav.sokos.prosjektnavn.config.Profile
import no.nav.sokos.prosjektnavn.config.PropertiesConfig
```
