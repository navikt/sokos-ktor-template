package no.nav.sokos.prosjektnavn.config

import java.time.Duration

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStopped
import mu.KotlinLogging
import org.flywaydb.core.Flyway
import org.postgresql.ds.PGSimpleDataSource

import no.nav.vault.jdbc.hikaricp.HikariCPVaultUtil.createHikariDataSourceWithVaultIntegration

private val logger = KotlinLogging.logger {}

// Extension functions kan bli forenklet når kotlin får context parameters: https://blog.jetbrains.com/kotlin/2025/04/update-on-context-parameters/
fun Application.setUpDatabase(appConfig: AppConfig): HikariDataSource {
    val hikariConfig = hikariConfig(appConfig.properties.database)

    if (appConfig.currentProfile.isLocal) return localSetup(hikariConfig)

    val dbProperties = appConfig.properties.database
    flyway(dbProperties.adminUser, adminSetup(hikariConfig, dbProperties))

    return vaultSetup(dbProperties.user, hikariConfig, dbProperties.vaultMountPath)
}

private fun Application.localSetup(hikariConfig: HikariConfig) = autoClose(HikariDataSource(hikariConfig))

private fun Application.adminSetup(
    hikariConfig: HikariConfig,
    databaseProperties: DatabaseProperties,
) = vaultSetup(databaseProperties.adminUser, hikariConfig, databaseProperties.vaultMountPath)

private fun Application.vaultSetup(
    role: String,
    hikariConfig: HikariConfig,
    mountPath: String,
): HikariDataSource =
    autoClose(
        createHikariDataSourceWithVaultIntegration(
            hikariConfig,
            mountPath,
            role,
        ),
    )

private fun flyway(
    role: String,
    dataSource: HikariDataSource,
) {
    logger.info { "Flyway migration" }
    Flyway
        .configure()
        .dataSource(dataSource)
        .initSql("""SET ROLE "$role"""")
        .lockRetryCount(-1)
        .validateMigrationNaming(true)
        .load()
        .migrate()
        .migrationsExecuted

    logger.info { "Migration finished" }
}

private fun <A : AutoCloseable> Application.autoClose(autoCloseable: A): A {
    monitor.subscribe(ApplicationStopped) {
        autoCloseable.close()
    }
    return autoCloseable
}

private fun hikariConfig(properties: DatabaseProperties): HikariConfig =
    HikariConfig().apply {
        maximumPoolSize = 5
        minimumIdle = 1
        idleTimeout = Duration.ofMinutes(4).toMillis()
        maxLifetime = Duration.ofMinutes(5).toMillis()
        isAutoCommit = false
        dataSource =
            PGSimpleDataSource()
                .apply {
                    user = properties.username
                    password = properties.password
                    serverNames = arrayOf(properties.host)
                    databaseName = properties.name
                    portNumbers = intArrayOf(properties.port.toInt())
                    connectionTimeout = Duration.ofSeconds(10).toMillis()
                    initializationFailTimeout = Duration.ofMinutes(30).toMillis()
                    transactionIsolation = "TRANSACTION_SERIALIZABLE"
                }
    }
