package no.nav.sokos.prosjektnavn

import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

class TestContainer() {
    private val dockerImageName = "postgres:latest"
    val container =
        PostgreSQLContainer<Nothing>(DockerImageName.parse(dockerImageName)).apply {
            withReuse(false)
            withInitScript("init.sql")
            start()
        }
}
