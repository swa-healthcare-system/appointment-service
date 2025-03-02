package cz.mokripat.appointment.repository

import cz.mokripat.appointment.model.Appointment

class DummyAppointmentRepositoryImpl() : AppointmentRepository {
    override fun getAllAppointments(): List<Appointment> {
        return listOf()
    }

    override fun getAppointmentById(id: String): Appointment? {
        return null
    }

    override fun insertAppointment(appointment: Appointment): Appointment {
        return appointment
    }

    override fun updateAppointment(id: String, appointment: Appointment): Appointment? {
        return appointment
    }

    override fun deleteAppointment(id: String): Boolean {
        return true
    }
}