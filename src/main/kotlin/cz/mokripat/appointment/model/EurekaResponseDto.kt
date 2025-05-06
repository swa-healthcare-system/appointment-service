package cz.mokripat.appointment.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

data class EurekaResponseDto(
    val application: ApplicationDto
)

data class ApplicationDto(
    val name: String,
    val instance: List<InstanceDto>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class InstanceDto(
    val instanceId: String,
    val hostName: String,
    val ipAddr: String,
    val status: String,
    val port: PortDto,
    val homePageUrl: String
)

data class PortDto(
    @JsonProperty("\$")
    val value: Int,
    @JsonProperty("@enabled")
    val enabled: Boolean
)