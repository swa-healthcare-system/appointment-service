package cz.mokripat.appointment.repository

import cz.mokripat.appointment.model.Appointment
import cz.mokripat.appointment.Appointments
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class PostgreAppointmentRepository : AppointmentRepository {

    override fun getAllAppointments(): List<Appointment> = transaction {
        Appointments.selectAll().map {
            Appointment(
                id = it[Appointments.id].value,
                doctorId = it[Appointments.doctorId],
                patientId = it[Appointments.patientId],
                fromTS = it[Appointments.fromTS],
                toTS = it[Appointments.toTS],
                note = it[Appointments.note],
                status = it[Appointments.status]
            )
        }
    }

    override fun getAppointmentById(id: Int): Appointment? = transaction {
        Appointments.select { Appointments.id eq id }
            .mapNotNull {
                Appointment(
                    id = it[Appointments.id].value,
                    doctorId = it[Appointments.doctorId],
                    patientId = it[Appointments.patientId],
                    fromTS = it[Appointments.fromTS],
                    toTS = it[Appointments.toTS],
                    note = it[Appointments.note],
                    status = it[Appointments.status]
                )
            }.singleOrNull()
    }

    override fun insertAppointment(appointment: Appointment): Appointment = transaction {
        val id = Appointments
            .insertAndGetId {
                it[doctorId] = appointment.doctorId
                it[patientId] = appointment.patientId
                it[fromTS] = appointment.fromTS
                it[toTS] = appointment.toTS
                it[note] = appointment.note
                it[status] = appointment.status
            }
        appointment.copy(id = id.value)
    }

    override fun updateAppointment(id: Int, appointment: Appointment): Appointment? = transaction {
        val updated = Appointments.update({Appointments.id eq id}) {
            it[doctorId] = appointment.doctorId
            it[patientId] = appointment.patientId
            it[fromTS] = appointment.fromTS
            it[toTS] = appointment.toTS
            it[note] = appointment.note
            it[status] = appointment.status
        }

        if (updated > 0) {
            appointment.copy(id = id)
        } else {
            null
        }
    }

    override fun deleteAppointment(id: Int): Boolean = transaction {
        val deleted = Appointments.deleteWhere { Appointments.id eq id }
        deleted > 0
    }
}
