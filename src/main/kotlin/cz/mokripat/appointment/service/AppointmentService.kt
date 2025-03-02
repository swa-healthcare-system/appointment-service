package cz.mokripat.appointment.service

import cz.mokripat.appointment.model.Appointment
import cz.mokripat.appointment.repository.AppointmentRepository

class AppointmentService(private val repository: AppointmentRepository) {
    fun getAllAppointments(): List<Appointment> = repository.getAllAppointments()

    fun getAppointmentById(id: String): Appointment? = repository.getAppointmentById(id)

    fun createAppointment(appointment: Appointment): Appointment {
        return repository.insertAppointment(appointment)
    }

    fun updateAppointment(id: String, appointment: Appointment): Appointment? {
        return repository.updateAppointment(id, appointment)
    }

    fun deleteAppointment(id: String): Boolean = repository.deleteAppointment(id)
}