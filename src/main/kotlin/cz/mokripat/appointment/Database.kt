package cz.mokripat.appointment

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import cz.mokripat.appointment.config.AppConfig
import cz.mokripat.appointment.model.Appointment
import cz.mokripat.appointment.model.AvailabilityPayload
import cz.mokripat.appointment.model.DoctorPayload
import cz.mokripat.appointment.repository.DoctorAvailabilityRepository
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Database

fun initDatabase(appConfig: AppConfig) {
    val config = HikariConfig().apply {
        jdbcUrl = "jdbc:postgresql://${appConfig.dbHost}:${appConfig.dbPort}/${appConfig.dbName}"
        driverClassName = "org.postgresql.Driver"
        username = appConfig.dbUser
        password = appConfig.dbPassword
        maximumPoolSize = 10
    }
    val dataSource = HikariDataSource(config)
    Database.connect(dataSource)
}

fun updateDatabase(data: String, doctorAvailabilityRepository: DoctorAvailabilityRepository) {
    println("Updating database with $data")
    val json = Json { ignoreUnknownKeys = true }
    val doctors = json.decodeFromString<List<DoctorPayload>>(data)
    val availabilityMap: Map<DoctorPayload, List<String>> = doctors.associateWith { it.doctorAvailabilities }

    availabilityMap.entries.forEach { entry ->
        doctorAvailabilityRepository.addDoctor(entry.key)
        entry.value.forEach { availability ->
            doctorAvailabilityRepository.addAvailability(AvailabilityPayload(entry.key.id.toString(), availability))
        }
    }
}

object Appointments : IdTable<Int>() {
    override val id = integer("id").autoIncrement().entityId()
    val doctorId = varchar("doctorId", 36)
    val patientId = varchar("patientId", 36)
    val fromTS = varchar("fromTS", 50)
    val toTS = varchar("toTS", 50)
    val note = varchar("note", 255).nullable()
    val status = enumerationByName("status", 50, Appointment.Status::class)

    override val primaryKey = PrimaryKey(id)
}