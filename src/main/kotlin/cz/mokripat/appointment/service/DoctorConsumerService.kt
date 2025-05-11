package cz.mokripat.appointment.service

import cz.mokripat.appointment.LoggerDelegate
import cz.mokripat.appointment.model.AvailabilityChangeEvent
import cz.mokripat.appointment.model.DoctorUpdateEvent
import cz.mokripat.appointment.repository.AppointmentRepository
import cz.mokripat.appointment.repository.DoctorAvailabilityRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import java.time.Duration
import java.util.*

class DoctorConsumerService(
    private val appointmentService: AppointmentService,
    private val doctorAvailabilityRepository: DoctorAvailabilityRepository,
    kafkaHost: String
) {
    private val logger by LoggerDelegate()

    private val topic = "doctor-topic"
    private val consumer: KafkaConsumer<String, String>

    init {
        val props = Properties().apply {
            put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaHost)
            put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
            put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
            put(ConsumerConfig.GROUP_ID_CONFIG, "appointment-group")
            put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
        }

        consumer = KafkaConsumer(props)
        consumer.subscribe(listOf(topic))

        GlobalScope.launch {
            consumeEvents()
        }
    }

    private fun consumeEvents() {
        while (true) {
            val records = consumer.poll(Duration.ofMillis(100))
            for (record in records) {
                try {
                    val parser = Json { ignoreUnknownKeys = true }
                    val json = parser.parseToJsonElement(record.value()).jsonObject
                    val eventType = json["eventType"]?.jsonPrimitive?.content

                    when (eventType) {
                        "DOCTOR_CREATED" -> {
                            val event = parser.decodeFromJsonElement<DoctorUpdateEvent>(json)
                            logger.info("Doctor Created: ${event.payload}")
                            doctorAvailabilityRepository.addDoctor(event.payload)
                        }

                        "DOCTOR_DELETED" -> {
                            val event = parser.decodeFromJsonElement<DoctorUpdateEvent>(json)
                            logger.info("Doctor Deleted: ${event.payload}")
                            val doctorId = event.payload.id.toString()
                            doctorAvailabilityRepository.removeDoctor(doctorId)
                            val toCancel = appointmentService.getAppointmentsByDoctorId(doctorId)

                            toCancel.forEach { appointment ->
                                logger.info("Canceled Appointment: $appointment")
                                appointmentService.deleteAppointment(appointment.id!!)
                            }
                        }

                        "AVAILABILITY_ADDED" -> {
                            val event = parser.decodeFromJsonElement<AvailabilityChangeEvent>(json)
                            logger.info("Availability Added: ${event.payload}")
                            doctorAvailabilityRepository.addAvailability(event.payload)
                        }

                        "AVAILABILITY_DELETED" -> {
                            val event = parser.decodeFromJsonElement<AvailabilityChangeEvent>(json)
                            logger.info("Availability Deleted: ${event.payload}")
                            doctorAvailabilityRepository.removeAvailability(event.payload)
                        }

                        else -> logger.warn("Unknown event type: $eventType")
                    }

                } catch (e: Exception) {
                    logger.error("Error decoding message: ${e.message}")
                }
            }
        }
    }
}