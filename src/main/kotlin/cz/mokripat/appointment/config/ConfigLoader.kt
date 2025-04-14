package cz.mokripat.appointment.config

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import kotlinx.serialization.json.*

private val configServerUrl = System.getenv("CONFIG_URL") ?: "http://config-server:8888"

suspend fun loadRemoteConfig(serviceName: String, profile: String): AppConfig {
    val client = HttpClient(CIO)

    val props = mutableMapOf<String, String>()

    try {
        val response: String = client.get("$configServerUrl/$serviceName/$profile").body()
        val json = Json.decodeFromString<JsonObject>(response)

        json["propertySources"]?.jsonArray?.forEach { source ->
            source.jsonObject["source"]?.jsonObject?.forEach { (k, v) ->
                props[k] = v.jsonPrimitive.content
            }
        }
        println("Configuration fetched $props")
    } catch (e: Exception) {
        Unit
    }

    client.close()

    return AppConfig(
        dbHost = props["DB_HOST"] ?: System.getenv("DB_HOST") ?: "localhost",
        dbPort = props["DB_PORT"] ?: System.getenv("DB_PORT") ?: "5555",
        dbName = props["DB_NAME"] ?: System.getenv("DB_NAME") ?: "appointments",
        dbUser = props["DB_USER"] ?: System.getenv("DB_USER") ?: "postgres",
        dbPassword = props["DB_PASSWORD"] ?: System.getenv("DB_PASSWORD") ?: "password",
        kafkaBroker = props["KAFKA_BROKER"] ?: System.getenv("KAFKA_BROKER") ?: "localhost:9092",
        eurekaUrl = props["EUREKA_URL"] ?: System.getenv("EUREKA_URL") ?: "http://localhost:8761/eureka/v2",
        hostname = props["HOSTNAME"] ?: System.getenv("HOSTNAME") ?: "appointment-service",
        hostIp = props["HOST_IP"] ?: System.getenv("HOST_IP") ?: "localhost",
        servicePort = props["SERVICE_PORT"]?.toIntOrNull() ?: System.getenv("SERVICE_PORT").toIntOrNull() ?: 8089
    )
}