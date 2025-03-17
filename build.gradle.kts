val kotlin_version: String by project
val logback_version: String by project
val exposed_version: String by project
val postgresql_version: String by project
val hikaricp_version: String by project
val koin_version: String by project
val kotlin_coroutines_version: String by project
val kafka_version: String by project
val pact_version: String by project
val kotlin_serialization_version: String by project
val mockk_version: String by project
val junit_version: String  by project

plugins {
    kotlin("jvm") version "2.1.10"
    id("io.ktor.plugin") version "3.1.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.10"
}

group = "cz.mokripat.appointment"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
    maven { url = uri("https://jcenter.bintray.com/") }
}

dependencies {
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-swagger")
    implementation("io.ktor:ktor-server-content-negotiation")
    implementation("io.ktor:ktor-serialization-kotlinx-json")
    implementation("io.ktor:ktor-server-netty")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-server-config-yaml")
    testImplementation("io.ktor:ktor-server-test-host")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlin_coroutines_version")

    implementation("org.jetbrains.exposed:exposed-core:$exposed_version") // Exposed core
    implementation("org.jetbrains.exposed:exposed-dao:$exposed_version")  // Exposed DAO
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version") // Exposed JDBC support
    implementation("org.postgresql:postgresql:$postgresql_version") // PostgreSQL JDBC Driver
    implementation("com.zaxxer:HikariCP:$hikaricp_version") // HikariCP for connection pooling

    implementation("io.insert-koin:koin-ktor:$koin_version") // Koin for Ktor
    implementation("io.insert-koin:koin-core:$koin_version") // Koin core functionality

    implementation("org.apache.kafka:kafka-clients:$kafka_version") // Kafka
}