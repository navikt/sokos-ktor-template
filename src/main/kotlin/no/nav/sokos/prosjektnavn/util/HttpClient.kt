package no.nav.sokos.prosjektnavn.util

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache

import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.jackson.jackson
import java.net.ProxySelector
import org.apache.http.impl.conn.SystemDefaultRoutePlanner

fun ObjectMapper.customConfig() {
    configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
}

val httpClient = HttpClient(Apache) {
    expectSuccess = false
    install(ContentNegotiation) {
        jackson {
            customConfig()
        }
    }

    engine {
        customizeClient {
            setRoutePlanner(SystemDefaultRoutePlanner(ProxySelector.getDefault()))
        }
    }
}
