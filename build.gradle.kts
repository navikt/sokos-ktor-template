import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

val ktorVersion = "2.1.3"
val junitJupiterVersion = "5.9.1"
val logbackVersion = "1.4.5"
val logstashVersion = "7.2"
val jacksonVersion = "2.14.1"
val prometheusVersion = "1.10.2"
val kotlinLoggingVersion = "3.0.4"

plugins {
    kotlin("jvm") version "1.7.22"
    kotlin("plugin.serialization") version "1.7.22"
    id("org.openapi.generator") version "6.2.1"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "no.nav.sokos.prosjektnavn"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
}

dependencies {

    // Ktor
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    implementation("io.ktor:ktor-client-apache:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-server-metrics-micrometer:$ktorVersion")
    implementation("io.ktor:ktor-server:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")

    // Jackson
    implementation("io.ktor:ktor-serialization-jackson:$ktorVersion")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")

    // Monitorering
    implementation("io.micrometer:micrometer-registry-prometheus:$prometheusVersion")

    // Logging
    implementation("ch.qos.logback:logback-core:$logbackVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("net.logstash.logback:logstash-logback-encoder:$logstashVersion")
    implementation("io.github.microutils:kotlin-logging-jvm:$kotlinLoggingVersion")

    // Test
    testImplementation("io.ktor:ktor-server-tests-jvm:$ktorVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitJupiterVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitJupiterVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitJupiterVersion")
}

sourceSets {
    main {
        java {
            srcDirs("$buildDir/generated/src/main/kotlin")
        }
    }
}

tasks.named<GenerateTask>("openApiGenerate") {
    generatorName.set("kotlin")
    generateModelDocumentation.set(false)
    inputSpec.set("$rootDir/specs/pets.json")
    outputDir.set("$buildDir/generated")
    globalProperties.set(
        mapOf(
            "models" to ""
        )
    )
    configOptions.set(
        mapOf(
            "library" to "jvm-ktor",
            "serializationLibrary" to "jackson"
        )
    )
}

tasks.named<ShadowJar>("shadowJar") {
    archiveFileName.set("app.jar")
    manifest {
        attributes["Main-Class"] = "no.nav.sokos.prosjektnavn.BootstrapKt"
    }
}

tasks.named<Jar>("jar") {
    isEnabled = false
}

tasks.withType<KotlinCompile> {
    dependsOn("openApiGenerate")
    kotlinOptions.jvmTarget = "17"
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        exceptionFormat = FULL
        events("passed", "skipped", "failed")
    }

    // For å øke hastigheten på build kan vi benytte disse metodene
    maxParallelForks = Runtime.getRuntime().availableProcessors() / 2
    reports.forEach { report -> report.required.value(false) }
}
