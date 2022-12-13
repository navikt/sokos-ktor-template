package no.nav.sokos.prosjektnavn.config

import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache

import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.jackson.jackson
import java.net.ProxySelector
import org.apache.http.impl.conn.SystemDefaultRoutePlanner

object HttpClientConfig {
    val httpClient = HttpClient(Apache) {
        expectSuccess = false
        install(ContentNegotiation) {
            jackson {
                serializationConfig()
            }
        }

        engine {
            customizeClient {
                setRoutePlanner(SystemDefaultRoutePlanner(ProxySelector.getDefault()))
            }
        }
    }
}
