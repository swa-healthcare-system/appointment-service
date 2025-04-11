package cz.mokripat.appointment

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import cz.mokripat.appointment.config.AppConfig
import cz.mokripat.appointment.model.Appointment
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