package cz.mokripat.appointment.service

import cz.mokripat.appointment.model.Appointment
import kotlinx.serialization.json.Json
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.IntegerSerializer
import org.apache.kafka.common.serialization.StringSerializer
import java.util.*


interface AppointmentProducerService {
    fun produceServiceStarted()
    fun produceAppointmentCreated(appointment: Appointment)
}

class AppointmentProducerServiceImpl(kafkaHost: String): AppointmentProducerService {
    private val topic = "appointments-topic"

    private val producer: KafkaProducer<Int, String>

    init {
        val props = Properties().apply {
            put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaHost)
            put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer::class.java.name)
            put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java.name)
        }

        producer = KafkaProducer(props)
        produceServiceStarted()
    }

    override fun produceServiceStarted() {
        val record = ProducerRecord(topic, 123, "AppointmentService started")
        producer.send(record) { metadata, exception ->
            if (exception == null) {
                println("Event sent to topic: ${metadata.topic()}, partition: ${metadata.partition()}, offset: ${metadata.offset()}")
            } else {
                println("Error sending event: ${exception.message}")
            }
        }
    }

    override fun produceAppointmentCreated(appointment: Appointment) {
        val eventJson = Json.encodeToString(appointment)
        val record = ProducerRecord(topic, requireNotNull(appointment.id), eventJson)
        producer.send(record) { metadata, exception ->
            if (exception == null) {
                println("Event sent to topic: ${metadata.topic()}, partition: ${metadata.partition()}, offset: ${metadata.offset()}")
            } else {
                println("Error sending event: ${exception.message}")
            }
        }
    }
}