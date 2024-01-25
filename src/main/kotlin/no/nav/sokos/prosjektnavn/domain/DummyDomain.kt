package no.nav.sokos.prosjektnavn.domain

import kotlinx.serialization.Serializable

@Serializable
data class DummyDomain(
    val id: String,
    val navn: String,
    val yrke: String,
)