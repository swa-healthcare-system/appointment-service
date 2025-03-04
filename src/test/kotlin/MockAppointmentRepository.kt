package cz.mokripat

import cz.mokripat.appointment.model.Appointment
import cz.mokripat.appointment.repository.AppointmentRepository

class MockAppointmentRepository : AppointmentRepository {
    private val appointments = mutableListOf<Appointment>()

    override fun getAllAppointments(): List<Appointment> = appointments

    override fun getAppointmentById(id: Int): Appointment? = appointments.find { it.id == id }

    override fun insertAppointment(appointment: Appointment): Appointment {
        appointments.add(appointment)
        return appointment
    }

    override fun updateAppointment(id: Int, appointment: Appointment): Appointment? {
        val index = appointments.indexOfFirst { it.id == id }
        if (index != -1) {
            appointments[index] = appointment
            return appointment
        }
        return null
    }

    override fun deleteAppointment(id: Int): Boolean = appointments.removeIf { it.id == id }

    fun addAppointment(appointment: Appointment) = appointments.add(appointment)

    fun clear() = appointments.clear()
}