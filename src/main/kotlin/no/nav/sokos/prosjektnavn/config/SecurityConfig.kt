package no.nav.sokos.prosjektnavn.config

import com.auth0.jwk.JwkProvider
import com.auth0.jwk.JwkProviderBuilder
import com.fasterxml.jackson.annotation.JsonProperty
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.server.application.Application
import io.ktor.server.auth.authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import mu.KotlinLogging
import java.net.URL
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.runBlocking
import no.nav.sokos.prosjektnavn.util.httpClient

private val log = KotlinLogging.logger {}
const val AUTHENTICATION_NAME = "azureAd"

fun Application.configureSecurity(
    azureAdConfig: PropertiesConfig.AzureAdConfig,
    useAuthentication: Boolean = true
) {
    log.info("Use authentication: $useAuthentication")
    if (useAuthentication) {
        val openIdMetadata: OpenIdMetadata = wellKnowConfig(azureAdConfig.wellKnownUrl)
        val jwkProvider = cachedJwkProvider(openIdMetadata.jwksUri)

        authentication {
            jwt(AUTHENTICATION_NAME) {
                realm = PropertiesConfig.Configuration().naisAppName
                verifier(
                    jwkProvider = jwkProvider,
                    issuer = openIdMetadata.issuer
                )
                validate { credential ->
                    try {
                        requireNotNull(credential.payload.audience) {
                            log.info("Auth: Missing audience in token")
                            "Auth: Missing audience in token"
                        }
                        require(credential.payload.audience.contains(azureAdConfig.clientId)) {
                            log.info("Auth: Valid audience not found in claims")
                            "Auth: Valid audience not found in claims"
                        }
                        JWTPrincipal(credential.payload)
                    } catch (e: Exception) {
                        log.warn(e) { "Client authentication failed" }
                        null
                    }
                }
            }
        }
    }
}

private fun cachedJwkProvider(jwksUri: String): JwkProvider {
    return JwkProviderBuilder(URL(jwksUri))
        .cached(10, 24, TimeUnit.HOURS) // cache up to 10 JWKs for 24 hours
        .rateLimited(10, 1, TimeUnit.MINUTES) // if not cached, only allow max 10 different keys per minute to be fetched from external provider
        .build()
}

data class OpenIdMetadata(
    @JsonProperty("jwks_uri") val jwksUri: String,
    @JsonProperty("issuer") val issuer: String,
    @JsonProperty("token_endpoint") val tokenEndpoint: String,
)

private fun wellKnowConfig(wellKnownUrl: String): OpenIdMetadata {
    val openIdMetadata: OpenIdMetadata by lazy {
        runBlocking { httpClient.get(wellKnownUrl).body() }
    }
    return openIdMetadata
}