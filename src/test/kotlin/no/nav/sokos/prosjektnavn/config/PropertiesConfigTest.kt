package no.nav.sokos.prosjektnavn.config

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.server.config.ApplicationConfig

internal class PropertiesConfigTest :
    FunSpec({

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
