package cz.mokripat.appointment.config

/**
 * Configuration of the service.
 * It is meant to be externally read from centralized server.
 */
data class AppConfig(
    val dbHost: String,
    val dbPort: String,
    val dbName: String,
    val dbUser: String,
    val dbPassword: String,
    val kafkaBroker: String,
    val eurekaUrl: String,
    val hostname: String,
    val hostIp: String,
    val servicePort: Int
)