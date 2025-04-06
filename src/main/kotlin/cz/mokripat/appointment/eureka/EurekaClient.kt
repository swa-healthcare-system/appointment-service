package cz.mokripat.appointment.eureka

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*

object EurekaClient {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            jackson()
        }
    }

    private val EUREKA_URL = System.getenv("EUREKA_URL") ?: "http://localhost:8761/eureka"
    private val APP_NAME = "APPOINTMENT-SERVICE"
    private val INSTANCE_ID = System.getenv("INSTANCE_ID") ?: "appointment-service-1"
    private val HOST = System.getenv("HOSTNAME") ?: "localhost"
    private val IP = System.getenv("HOST_IP") ?: "127.0.0.1"
    private val PORT = (System.getenv("SERVICE_PORT") ?: "8080").toInt()

    suspend fun register() {
        val payload = mapOf(
            "instance" to mapOf(
                "instanceId" to INSTANCE_ID,
                "hostName" to HOST,
                "app" to APP_NAME,
                "ipAddr" to IP,
                "vipAddress" to APP_NAME.lowercase(),
                "secureVipAddress" to APP_NAME.lowercase(),
                "status" to "UP",
                "port" to mapOf("\$" to PORT, "@enabled" to true),
                "dataCenterInfo" to mapOf(
                    "@class" to "com.netflix.appinfo.InstanceInfo\$DefaultDataCenterInfo",
                    "name" to "MyOwn"
                )
            )
        )

        val response = client.post("$EUREKA_URL/apps/$APP_NAME") {
            contentType(ContentType.Application.Json)
            setBody(payload)
        }

        println("✅ Registered with Eureka: ${response.status}")
    }

    suspend fun sendHeartbeat() {
        val response = client.put("$EUREKA_URL/apps/$APP_NAME/$INSTANCE_ID") {}
        println("💓 Eureka heartbeat: ${response.status}")
    }

    suspend fun deregister() {
        val response = client.delete("$EUREKA_URL/apps/$APP_NAME/$INSTANCE_ID") {}
        println("❌ Deregistered from Eureka: ${response.status}")
    }
}