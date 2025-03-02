package cz.mokripat.appointment.routes

import cz.mokripat.appointment.model.Appointment
import cz.mokripat.appointment.service.AppointmentService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(
    appointmentService: AppointmentService
) {
    routing {
        get("/") {
            call.respondText("AppointmentService is alive!", ContentType.Text.Html)
        }

        get("/appointments") {
            call.respond(appointmentService.getAllAppointments())
        }

        get("/appointment/{id}") {
            val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            val appointment = appointmentService.getAppointmentById(id)
            if (appointment != null) {
                call.respond(appointment)
            } else {
                call.respond(HttpStatusCode.NotFound, "Appointment not found")
            }
        }

        post("/appointment") {
            val dto = call.receive<Appointment>()
            val created = appointmentService.createAppointment(dto)
            call.respond(HttpStatusCode.Created, created)
        }

        put("/appointment/{id}") {
            val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest)
            val dto = call.receive<Appointment>()
            val updated = appointmentService.updateAppointment(id, dto)
            if (updated != null) {
                call.respond(updated)
            } else {
                call.respond(HttpStatusCode.NotFound, "Appointment not found")
            }
        }

        delete("/appointment/{id}") {
            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            val deleted = appointmentService.deleteAppointment(id)
            if (deleted) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.NotFound, "Appointment not found")
            }
        }
    }
}
