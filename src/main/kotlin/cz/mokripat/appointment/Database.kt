package cz.mokripat.appointment

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import cz.mokripat.appointment.model.Appointment
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Database

fun initDatabase() {
    val config = HikariConfig().apply {
        jdbcUrl = "jdbc:postgresql://localhost:5555/appointments"
        driverClassName = "org.postgresql.Driver"
        username = "postgres"
        password = "password"
        maximumPoolSize = 10
    }
    val dataSource = HikariDataSource(config)
    Database.connect(dataSource)
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