package cz.mokripat.appointment.repository

import cz.mokripat.appointment.model.Appointment

class DummyAppointmentRepository() : AppointmentRepository {
    override fun getAllAppointments(): List<Appointment> {
        return listOf()
    }

    override fun getAppointmentById(id: Int): Appointment? {
        return null
    }

    override fun insertAppointment(appointment: Appointment): Appointment {
        return appointment
    }

    override fun updateAppointment(id: Int, appointment: Appointment): Appointment? {
        return appointment
    }

    override fun deleteAppointment(id: Int): Boolean {
        return true
    }
}