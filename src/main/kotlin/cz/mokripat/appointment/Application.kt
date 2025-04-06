package cz.mokripat.appointment

import cz.mokripat.appointment.eureka.EurekaClient
import cz.mokripat.appointment.model.configureSerialization
import cz.mokripat.appointment.routes.configureRouting
import cz.mokripat.appointment.service.AppointmentConsumerService
import cz.mokripat.appointment.service.AppointmentService
import io.ktor.server.application.*
import io.ktor.server.netty.*
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
    startKoin {
        modules(appModule)
    }

    initDatabase()

    environment.monitor.subscribe(ApplicationStarted) {
        runBlocking {
            EurekaClient.register()
            launch {
                while (isActive) {
                    delay(30_000)
                    EurekaClient.sendHeartbeat()
                }
            }
        }
    }

    Runtime.getRuntime().addShutdownHook(Thread {
        runBlocking {
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
    val appointmentConsumerService: AppointmentConsumerService = AppointmentConsumerService()

    configureHTTP()
    configureSerialization()
    configureRouting(appointmentService)
}
