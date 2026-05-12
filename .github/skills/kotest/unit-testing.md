# Enhetstester og konfig-tester

## PropertiesConfig-tester

`PropertiesConfig` lastes globalt av `ProjectConfig : AbstractProjectConfig` — ingen `beforeSpec` nødvendig i testklassen:

```kotlin
internal class PropertiesConfigTest : FunSpec({

    test("applicationProperties skal lastes fra application-test.conf") {
        val props = PropertiesConfig.applicationProperties
        props.profile shouldBe Profile.TEST
        props.appName shouldBe "sokos-ktor-template"
        props.namespace shouldBe "okonomi"
        props.useAuthentication shouldBe true
        props.isTest shouldBe true
        props.isLocal shouldBe false
        props.isProd shouldBe false
    }

    test("azureAdProperties skal lastes fra application-test.conf") {
        val props = PropertiesConfig.azureAdProperties
        props.clientId shouldBe "default"
        props.wellKnownUrl shouldBe ""
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

Konfig-filen ligger i `src/main/resources/application-test.conf`:

```hocon
include "application.conf"

application {
    profile = TEST
}

azureAd {
    clientId = "default"
    wellKnownUrl = ""
}
```

> Legg til seksjoner for nye konfig-blokker (f.eks. `postgres`, `kafka`) etter samme mønster.

## Nødvendige imports

```kotlin
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.assertions.throwables.shouldThrow
import io.mockk.every
import io.mockk.mockk
import no.nav.sokos.prosjektnavn.config.Profile
import no.nav.sokos.prosjektnavn.config.PropertiesConfig
```
