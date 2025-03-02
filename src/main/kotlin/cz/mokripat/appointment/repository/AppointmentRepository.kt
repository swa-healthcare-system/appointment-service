package cz.mokripat.appointment.repository

import cz.mokripat.appointment.model.Appointment

interface AppointmentRepository {
    fun getAllAppointments(): List<Appointment>
    fun getAppointmentById(id: Int): Appointment?
    fun insertAppointment(appointment: Appointment): Appointment
    fun updateAppointment(id: Int, appointment: Appointment): Appointment?
    fun deleteAppointment(id: Int): Boolean
}