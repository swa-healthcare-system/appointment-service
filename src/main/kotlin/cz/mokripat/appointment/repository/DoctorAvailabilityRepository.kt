package cz.mokripat.appointment.repository

import cz.mokripat.appointment.model.AvailabilityPayload
import cz.mokripat.appointment.model.DoctorPayload

interface DoctorAvailabilityRepository {
    fun addDoctor(payload: DoctorPayload): Unit
    fun removeDoctor(doctorId: String): Unit
    fun getDoctorAvailability(doctorId: String): List<String>
    fun addAvailability(payload: AvailabilityPayload): Boolean
    fun removeAvailability(payload: AvailabilityPayload): Boolean
}