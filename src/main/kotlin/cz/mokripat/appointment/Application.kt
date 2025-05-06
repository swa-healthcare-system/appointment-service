package cz.mokripat.appointment

import cz.mokripat.appointment.config.loadRemoteConfig
import cz.mokripat.appointment.eureka.EurekaClient
import cz.mokripat.appointment.model.configureSerialization
import cz.mokripat.appointment.routes.configureRouting
import cz.mokripat.appointment.service.AppointmentConsumerService
import cz.mokripat.appointment.service.AppointmentService
import io.ktor.server.application.*
import io.ktor.server.metrics.micrometer.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.micrometer.prometheusmetrics.PrometheusConfig
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import org.koin.ktor.ext.inject

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    val config = runBlocking {
        loadRemoteConfig("appointment-service", "default")
    }

    startKoin {
        modules(appModule(config))
    }

    initDatabase(config)

    val prometheusRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

    install(MicrometerMetrics) {
        registry = prometheusRegistry
    }

    routing {
        get("/metrics") {
            call.respondText(prometheusRegistry.scrape())
        }
    }

    environment.monitor.subscribe(ApplicationStarted) {
        launch {
            EurekaClient.register()

            while (!EurekaClient.registered) {
                delay(30_000)
                EurekaClient.register()
            }

            val doctorHostname = EurekaClient.discoverServiceBaseUrl("DOCTOR-SERVICE-SPRING-BOOT")
            doctorHostname?.let {
                val doctorData = EurekaClient.getDoctorData(doctorHostname)
                updateDatabase(doctorData)
            }

            while (isActive) {
                delay(30_000)
                EurekaClient.sendHeartbeat()
            }
        }
    }

    Runtime.getRuntime().addShutdownHook(Thread {
        launch {
            EurekaClient.deregister()
        }
    })

    applicationBase()
}

/**
 * Separated base which can be used also for testing.
 */
fun Application.applicationBase() {
    val appointmentService: AppointmentService by inject()
    val appointmentConsumerService: AppointmentConsumerService by inject()

    configureHTTP()
    configureSerialization()
    configureRouting(appointmentService)
}
