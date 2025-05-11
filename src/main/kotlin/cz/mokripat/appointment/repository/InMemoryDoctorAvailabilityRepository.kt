package cz.mokripat.appointment.repository

import cz.mokripat.appointment.model.AvailabilityPayload
import cz.mokripat.appointment.model.DoctorPayload

class InMemoryDoctorAvailabilityRepository : DoctorAvailabilityRepository {

    private val data: MutableMap<String, MutableList<String>> = mutableMapOf()

    override fun addDoctor(payload: DoctorPayload) {
        if (!data.containsKey(payload.name)) {
            data[payload.id.toString()] = mutableListOf()
            println("Adding ${payload.id}")
        }
    }

    override fun removeDoctor(doctorId: String) {
        data.remove(doctorId)
    }

    override fun getDoctorAvailability(doctorId: String): List<String> {
        return data[doctorId].orEmpty()
    }

    override fun addAvailability(payload: AvailabilityPayload): Boolean {
        return data[payload.doctorId]?.add(payload.availabilityDate) ?: false
    }

    override fun removeAvailability(payload: AvailabilityPayload): Boolean {
        return data[payload.doctorId]?.remove(payload.availabilityDate) ?: false
    }
}