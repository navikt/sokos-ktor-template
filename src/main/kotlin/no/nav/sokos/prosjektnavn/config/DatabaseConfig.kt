package no.nav.sokos.prosjektnavn.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.postgresql.ds.PGSimpleDataSource

object DatabaseConfig {
    private lateinit var applicationProperties: PropertiesConfig.ApplicationProperties
    private lateinit var postgresProperties: PropertiesConfig.PostgresProperties
    private lateinit var dataSourcePriv: HikariDataSource

    val dataSource: HikariDataSource by lazy {
        dataSourcePriv
    }

    fun init(config: PropertiesConfig.Configuration) {
        this.applicationProperties = config.applicationProperties
        this.postgresProperties = config.postgresProperties
        try {
            check(::postgresProperties.isInitialized) { "PostgresProperties not initialized" }
            check(::applicationProperties.isInitialized) { "ApplicationProperties not initialized" }
            dataSourcePriv = dataSource()
        } catch (e: Exception) {
            throw e
        }
    }

    private fun dataSource(hikariConfig: HikariConfig = hikariConfig()): HikariDataSource = HikariDataSource(hikariConfig)

    private fun hikariConfig(): HikariConfig =
        HikariConfig().apply {
            maximumPoolSize = 5
            minimumIdle = 1
            isAutoCommit = false
            dataSource =
                PGSimpleDataSource().apply {
                    user = postgresProperties.username
                    password = postgresProperties.password
                    serverNames = arrayOf(postgresProperties.host)
                    databaseName = postgresProperties.name
                    portNumbers = intArrayOf(postgresProperties.port.toInt())
                }
        }
}
