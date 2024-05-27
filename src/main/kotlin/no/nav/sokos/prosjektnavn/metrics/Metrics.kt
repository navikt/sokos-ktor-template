package no.nav.sokos.prosjektnavn.metrics

import io.micrometer.prometheusmetrics.PrometheusConfig
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
import io.prometheus.metrics.core.metrics.Counter

private const val METRICS_NAMESPACE = "sokos_ktor_template"

object Metrics {
    val prometheusMeterRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

    /**
     * This is an example counter. It is used to demonstrate how to create a counter metric.
     * To use this counter metric, you can call `exampleCounter.inc()` to increment the counter by 1.
     */
    val exampleCounter: Counter =
        Counter.builder()
            .name("${METRICS_NAMESPACE}_example_counter")
            .help("Example counter")
            .withoutExemplars()
            .register(prometheusMeterRegistry.prometheusRegistry)
}
