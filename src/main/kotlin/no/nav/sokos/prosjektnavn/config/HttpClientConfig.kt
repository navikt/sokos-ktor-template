package no.nav.sokos.prosjektnavn.config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.jackson.jackson
import org.apache.http.impl.conn.SystemDefaultRoutePlanner
import java.net.ProxySelector

fun ObjectMapper.customConfig() {
    registerModule(JavaTimeModule())
    configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
}

val jsonMapper: ObjectMapper = jacksonObjectMapper().apply { customConfig() }

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
