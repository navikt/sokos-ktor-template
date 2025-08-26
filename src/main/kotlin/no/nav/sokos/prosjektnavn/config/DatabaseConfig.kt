package no.nav.sokos.prosjektnavn.config

import java.time.Duration

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import mu.KotlinLogging
import org.flywaydb.core.Flyway
import org.postgresql.ds.PGSimpleDataSource

import no.nav.sokos.prosjektnavn.config.PropertiesConfig.DatabaseType

private val logger = KotlinLogging.logger {}

object DatabaseConfig {
    fun hikariConfig(properties: PropertiesConfig.ApplicationProperties = PropertiesConfig.getApplicationProperties()) =
        when (properties.databaseType) {
            DatabaseType.H2 -> h2Config(PropertiesConfig.getH2Properties()!!)
            else -> postgresqlConfig(PropertiesConfig.getPostgresProperties()!!)
        }

    fun databaseMigrate(dataSource: HikariDataSource = HikariDataSource(hikariConfig())) {
        dataSource.use { connection ->
            Flyway
                .configure()
                .dataSource(connection)
                .baselineOnMigrate(true)
                .lockRetryCount(-1)
                .validateMigrationNaming(true)
                .sqlMigrationSeparator("__")
                .sqlMigrationPrefix("V")
                .load()
                .migrate()
                .migrationsExecuted
            logger.info { "Migration finished" }
        }
    }

    private fun h2Config(properties: PropertiesConfig.H2Properties): HikariConfig {
        logger.info { "Using H2 database" }
        return HikariConfig().apply {
            jdbcUrl = properties.jdbcUrl
            username = properties.username
            password = properties.password
            driverClassName = "org.h2.Driver"
            maximumPoolSize = 10
            isAutoCommit = true
        }
    }

    private fun postgresqlConfig(properties: PropertiesConfig.PostgresProperties): HikariConfig {
        logger.info { "Using Postgres database" }
        return HikariConfig().apply {
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
    }
}
