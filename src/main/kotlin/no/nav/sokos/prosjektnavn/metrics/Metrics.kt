package no.nav.sokos.prosjektnavn.metrics

import io.micrometer.prometheusmetrics.PrometheusConfig
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
import io.prometheus.metrics.core.metrics.Counter

private const val METRICS_NAMESPACE = "sokos_ktor_template"

object Metrics {
    val prometheusMeterRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

    val appStateRunningFalse: Counter =
        Counter.builder()
            .name("${METRICS_NAMESPACE}_app_state_running_false")
            .help("app state running changed to false")
            .withoutExemplars()
            .register(prometheusMeterRegistry.prometheusRegistry)

    val appStateReadyFalse: Counter =
        Counter.builder()
            .name("${METRICS_NAMESPACE}_app_state_ready_false")
            .help("app state ready changed to false")
            .withoutExemplars()
            .register(prometheusMeterRegistry.prometheusRegistry)
}
