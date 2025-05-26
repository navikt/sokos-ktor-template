package no.nav.sokos.prosjektnavn.service

import no.nav.sokos.prosjektnavn.config.DatabaseConfig

class DatabaseService {
    fun read(): Pair<Int, String?> {
        DatabaseConfig.dataSource.connection.use { connection ->
            connection.createStatement().use { statement ->

                val resultSet = statement.executeQuery("SELECT * FROM test_table")

                resultSet.next()
                val id = resultSet.getInt("id")
                val name = resultSet.getString("name")
                return Pair(id, name)
            }
        }
    }
}
