package cz.mokripat.appointment

import cz.mokripat.appointment.routes.configureRouting
import cz.mokripat.appointment.model.configureSerialization
import cz.mokripat.appointment.repository.AppointmentRepository
import cz.mokripat.appointment.repository.PostgreAppointmentRepository
import cz.mokripat.appointment.service.AppointmentService
import io.ktor.server.application.*
import io.ktor.server.netty.*

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    initDatabase()

    val appointmentRepository: AppointmentRepository = PostgreAppointmentRepository()
    val appointmentService = AppointmentService(appointmentRepository)

    configureHTTP()
    configureSerialization()
    configureRouting(appointmentService)
}
