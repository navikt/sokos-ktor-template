package no.nav.sokos.prosjektnavn.domain

import kotlinx.serialization.Serializable

@Serializable
data class Cats(
    val lily: Lily,
    val lucy: Lucy,
)

@Serializable
data class Lucy(
    val name: String,
)

@Serializable
data class Lily(
    val name: String,
)

@Serializable
data class NullableCats(
    val nullableLily: Lily? = null,
    // Move nullableLucy to above nullableLily and the test will pass
    val nullableLucy: Lucy? = null,
)
