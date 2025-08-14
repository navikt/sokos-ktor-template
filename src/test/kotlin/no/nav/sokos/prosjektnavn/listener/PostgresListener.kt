package no.nav.sokos.prosjektnavn.listener

import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Spec
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.ext.ScriptUtils
import org.testcontainers.jdbc.JdbcDatabaseDelegate
import org.testcontainers.utility.DockerImageName

object PostgresListener : TestListener {
    private val dockerImageName = "postgres:latest"
    val dbContainer =
        PostgreSQLContainer<Nothing>(DockerImageName.parse(dockerImageName)).apply {
            withReuse(false)
            withUsername("$username-admin")
            start()
        }

    fun migrate(script: String) = loadInitScript(script)

    private fun loadInitScript(name: String) = ScriptUtils.runInitScript(JdbcDatabaseDelegate(dbContainer, ""), name)

    override suspend fun afterSpec(spec: Spec) {
        dbContainer.stop()
    }
}
