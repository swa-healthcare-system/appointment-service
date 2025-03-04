package cz.mokripat.appointment

import cz.mokripat.appointment.model.configureSerialization
import cz.mokripat.appointment.routes.configureRouting
import cz.mokripat.appointment.service.AppointmentService
import io.ktor.server.application.*
import io.ktor.server.netty.*
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

    applicationBase()
}

/**
 * Separated base which can be used also for testing.
 */
fun Application.applicationBase() {
    val appointmentService: AppointmentService by inject()

    configureHTTP()
    configureSerialization()
    configureRouting(appointmentService)
}
