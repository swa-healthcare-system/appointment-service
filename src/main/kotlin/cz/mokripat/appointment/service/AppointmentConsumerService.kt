package cz.mokripat.appointment.service

import cz.mokripat.appointment.model.Appointment
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.IntegerDeserializer
import org.apache.kafka.common.serialization.StringDeserializer
import java.time.Duration
import java.util.*

class AppointmentConsumerService {
    private val topic = "appointment_events"

    private val consumer: KafkaConsumer<Int, String>

    init {
        val kafkaHost = System.getenv("KAFKA_BROKER") ?: "localhost:9092"

        val props = Properties().apply {
            put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaHost)
            put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer::class.java.name)
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
                    val event = Json.decodeFromString<Appointment>(record.value())
                    println("Consumed event: $event")
                } catch (e: Exception) {
                    println("Error decoding message: ${e.message}")
                }
            }
        }
    }
}