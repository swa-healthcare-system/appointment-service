package cz.mokripat.appointment.eureka

import cz.mokripat.appointment.LoggerDelegate
import cz.mokripat.appointment.model.EurekaResponseDto
import cz.mokripat.appointment.model.InstanceDto
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*

object EurekaClient {
    private val logger by LoggerDelegate()
    var registered = false

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

        try {
            val response = client.post("$EUREKA_URL/apps/$APP_NAME") {
                contentType(ContentType.Application.Json)
                setBody(payload)
            }
            registered = true
            logger.info("✅ Registered with Eureka: ${response.status}")
        } catch (e: Exception) {
            logger.info("❌ Failed to register with Eureka", e)
            println("Caught an exception in EurekaClient: ${e.message}")
        }
    }

    suspend fun sendHeartbeat() {
        try {
            val response = client.put("$EUREKA_URL/apps/$APP_NAME/$INSTANCE_ID") {}
            if (response.status == HttpStatusCode.NotFound) {
                register()
            }

            logger.info("💓 Eureka heartbeat: ${response.status}")
        } catch (e: Exception) {
            logger.info("❌ Failed to send heartbeat to Eureka", e)
        }
    }

    suspend fun deregister() {
        try {
            val response = client.delete("$EUREKA_URL/apps/$APP_NAME/$INSTANCE_ID") {}
            logger.error("👽 Deregistered from Eureka: ${response.status}")
        } catch (e: Exception) {
            logger.info("❌ Failed to deregister from Eureka", e)
        }
    }

    suspend fun discoverServiceBaseUrl(
        appName: String
    ): String? {
        return try {
            val response: EurekaResponseDto = client.get("$EUREKA_URL/apps/${appName.uppercase()}") {
                accept(ContentType.Application.Json)
            }.body()

            logger.info("✅ Successfully discovered app ($appName) with Eureka")

            val instance: InstanceDto? = response.application.instance
                .firstOrNull { it.status == "UP" }

            instance?.let {
                // You could use hostName + port instead if needed
                "http://${it.hostName}:${it.port.value}"
            }
        } catch (e: Exception) {
            logger.info("❌ Failed to discover app ($appName)", e)
            null
        }
    }

    suspend fun getDoctorData(doctorHostname: String): String =
        client.get("$doctorHostname/api/doctors").body<String>()
}