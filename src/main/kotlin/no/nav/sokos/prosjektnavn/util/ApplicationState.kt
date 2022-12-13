package no.nav.sokos.prosjektnavn.util

import kotlin.properties.Delegates
import no.nav.sokos.prosjektnavn.metrics.appStateReadyFalse
import no.nav.sokos.prosjektnavn.metrics.appStateRunningFalse

class ApplicationState(
    alive: Boolean = true,
    ready: Boolean = false
) {
    var alive: Boolean by Delegates.observable(alive) { _, _, newValue ->
        if (!newValue) appStateReadyFalse.inc()
    }
    var ready: Boolean by Delegates.observable(ready) { _, _, newValue ->
        if (!newValue) appStateRunningFalse.inc()
    }
}
