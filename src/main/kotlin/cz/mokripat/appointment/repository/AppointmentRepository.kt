package cz.mokripat.appointment.repository

import cz.mokripat.appointment.model.Appointment

interface AppointmentRepository {
    fun getAllAppointments(): List<Appointment>
    fun getAppointmentById(id: String): Appointment?
    fun insertAppointment(appointment: Appointment): Appointment
    fun updateAppointment(id: String, appointment: Appointment): Appointment?
    fun deleteAppointment(id: String): Boolean
}