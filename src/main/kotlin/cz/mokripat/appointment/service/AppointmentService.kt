package cz.mokripat.appointment.service

import cz.mokripat.appointment.model.Appointment
import cz.mokripat.appointment.repository.AppointmentRepository
import cz.mokripat.appointment.repository.DoctorAvailabilityRepository
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class AppointmentService(
    private val repository: AppointmentRepository,
    private val availabilityRepository: DoctorAvailabilityRepository,
    private val producer: AppointmentProducerService,
) {
    fun getAllAppointments(): List<Appointment> = repository.getAllAppointments()

    fun getAppointmentById(id: Int): Appointment? = repository.getAppointmentById(id)

    fun getAppointmentsByDoctorId(doctorId: String): List<Appointment> = repository.getAppointmentsByDoctorId(doctorId)

    fun createAppointment(appointment: Appointment): Appointment {
        // Convert fromTS to LocalDate for availability comparison
        val appointmentDate = ZonedDateTime.parse(appointment.fromTS, DateTimeFormatter.ISO_DATE_TIME)
            .toLocalDate()
            .toString()

        // Get list of available dates for the doctor
        val availableDates = availabilityRepository.getDoctorAvailability(appointment.doctorId)

        // Check if the date is available
        if (!availableDates.contains(appointmentDate)) {
            throw IllegalArgumentException("Doctor is not available on $appointmentDate")
        }

        // Proceed with creating the appointment
        val created = repository.insertAppointment(appointment)
        producer.produceAppointmentCreated(created)
        return created
    }

    fun updateAppointment(id: Int, appointment: Appointment): Appointment? {
        return repository.updateAppointment(id, appointment)
    }

    fun deleteAppointment(id: Int): Boolean {
        val appointment = repository.getAppointmentById(id)

        appointment?.let {
            repository.deleteAppointment(id)
            producer.produceAppointmentCanceled(it)
            return true
        }

        return false
    }
}