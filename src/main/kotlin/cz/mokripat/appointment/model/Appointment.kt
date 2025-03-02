package cz.mokripat.appointment.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Data class representing an Appointment
@Serializable
data class Appointment(
    val id: Int? = null,
    val doctorId: String,
    val patientId: String,
    val fromTS: String,
    val toTS: String,
    val note: String?,
    val status: Status
) {
    @Serializable
    enum class Status {
        @SerialName("booked") BOOKED,
        @SerialName("canceled") CANCELED,
        @SerialName("completed") COMPLETED
    }
}
