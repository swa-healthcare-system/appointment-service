package cz.mokripat.appointment.model

import kotlinx.serialization.Serializable

@Serializable
sealed class DoctorEvent {
    abstract val eventType: String
}

@Serializable
data class DoctorUpdateEvent(
    override val eventType: String,
    val payload: DoctorPayload
) : DoctorEvent()

@Serializable
data class AvailabilityChangeEvent(
    override val eventType: String,
    val payload: AvailabilityPayload
) : DoctorEvent()

@Serializable
data class DoctorPayload(
    val id: Int,
    val name: String,
    val surname: String,
    val doctorAvailabilities: List<String> = emptyList(),
)

@Serializable
data class AvailabilityPayload(
    val doctorId: String,
    val availabilityDate: String
)