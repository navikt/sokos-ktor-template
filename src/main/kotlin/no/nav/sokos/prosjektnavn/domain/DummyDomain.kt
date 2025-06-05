package no.nav.sokos.prosjektnavn.domain

import kotlinx.serialization.Serializable

@Serializable
data class Cats(
    val lucy: Lucy,
    val lily: Lily,
)

@Serializable
data class Lucy(
    val name: String,
)

@Serializable
data class Lily(
    val name: String,
)
