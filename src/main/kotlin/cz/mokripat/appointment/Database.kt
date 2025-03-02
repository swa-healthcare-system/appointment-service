package cz.mokripat.appointment

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import cz.mokripat.appointment.model.Appointment
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Database

private val dbHost = System.getenv("DB_HOST") ?: "localhost"
private val dbPort = System.getenv("DB_PORT") ?: "5555"
private val dbName = System.getenv("DB_NAME") ?: "appointments"
private val dbUser = System.getenv("DB_USER") ?: "postgres"
private val dbPassword = System.getenv("DB_PASSWORD") ?: "password"

fun initDatabase() {
    val config = HikariConfig().apply {
        jdbcUrl = "jdbc:postgresql://$dbHost:$dbPort/$dbName"
        driverClassName = "org.postgresql.Driver"
        username = dbUser
        password = dbPassword
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