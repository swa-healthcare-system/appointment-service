package cz.mokripat.appointment

import cz.mokripat.appointment.routes.configureRouting
import cz.mokripat.appointment.model.configureSerialization
import cz.mokripat.appointment.repository.AppointmentRepository
import cz.mokripat.appointment.repository.DummyAppointmentRepositoryImpl
import cz.mokripat.appointment.service.AppointmentService
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    val appointmentRepository: AppointmentRepository = DummyAppointmentRepositoryImpl()
    val appointmentService = AppointmentService(appointmentRepository)

    configureHTTP()
    configureSerialization()
    configureRouting(appointmentService)
}
