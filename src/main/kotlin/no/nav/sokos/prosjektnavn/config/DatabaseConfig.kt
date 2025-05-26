package no.nav.sokos.prosjektnavn.config

import java.time.Duration

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import mu.KotlinLogging
import org.flywaydb.core.Flyway
import org.postgresql.ds.PGSimpleDataSource

import no.nav.sokos.prosjektnavn.config.PropertiesConfig.configuration
import no.nav.vault.jdbc.hikaricp.HikariCPVaultUtil.createHikariDataSourceWithVaultIntegration

object DatabaseConfig {
    @Volatile
    private lateinit var _dataSource: HikariDataSource
    private val logger = KotlinLogging.logger {}

    val dataSource: HikariDataSource
        get() {
            return _dataSource
        }

    init {
        init()
    }

    fun init() {
        val postgresProperties = configuration.postgresProperties

        val hikariConfig = hikariConfig(postgresProperties)
        _dataSource = dataSource(hikariConfig)

        if (!PropertiesConfig.environment.isLocal) {
            migrate(postgresProperties)
        }
    }

    private fun dataSource(
        hikariConfig: HikariConfig,
        role: String = configuration.postgresProperties.user,
    ): HikariDataSource =
        if (PropertiesConfig.environment.isLocal) {
            HikariDataSource(hikariConfig).apply {
                username = configuration.postgresProperties.username
                password = configuration.postgresProperties.password
            }
        } else {
            createHikariDataSourceWithVaultIntegration(
                hikariConfig,
                configuration.postgresProperties.vaultMountPath,
                role,
            )
        }

    private fun hikariConfig(postgresProperties: PropertiesConfig.PostgresProperties): HikariConfig =
        HikariConfig().apply {
            maximumPoolSize = 5
            minimumIdle = 1
            isAutoCommit = false
            dataSource =
                PGSimpleDataSource()
                    .apply {
                        if (PropertiesConfig.environment.isLocal) {
                            user = postgresProperties.username
                            password = postgresProperties.password
                        }
                        serverNames = arrayOf(postgresProperties.host)
                        databaseName = postgresProperties.name
                        portNumbers = intArrayOf(postgresProperties.port.toInt())
                        connectionTimeout = Duration.ofSeconds(10).toMillis()
                        maxLifetime = Duration.ofMinutes(30).toMillis()
                        initializationFailTimeout = Duration.ofMinutes(30).toMillis()
                        transactionIsolation = "TRANSACTION_SERIALIZABLE"
                    }
        }

    fun migrate(postgresProperties: PropertiesConfig.PostgresProperties = configuration.postgresProperties) {
        val dataSourceAdmin = dataSource(hikariConfig(postgresProperties), role = postgresProperties.adminUser)
        logger.info { "Flyway migration" }
        Flyway
            .configure()
            .dataSource(dataSourceAdmin)
            .initSql("""SET ROLE "${postgresProperties.adminUser}"""")
            .lockRetryCount(-1)
            .validateMigrationNaming(true)
            .load()
            .migrate()
            .migrationsExecuted

        logger.info { "Migration finished" }
    }
}
