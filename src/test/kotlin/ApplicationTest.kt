package cz.mokripat

import cz.mokripat.appointment.model.Appointment
import cz.mokripat.appointment.module
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import org.koin.core.context.GlobalContext.stopKoin
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {

    private val testAppointment = Appointment(
        id = 1,
        doctorId = "doc123",
        patientId = "pat456",
        fromTS = "2025-03-15T09:00:00",
        toTS = "2025-03-15T10:00:00",
        note = "Follow-up appointment",
        status = Appointment.Status.BOOKED
    )

    @BeforeTest
    fun setup() {
        stopKoin() // Stop any running Koin instances before starting tests
    }

    @AfterTest
    fun teardown() {
        stopKoin() // Ensure Koin is stopped after tests
    }

    @Test
    fun testRoot() = testApplication {
        application { module() }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("AppointmentService is alive!", bodyAsText())
        }
    }

    @Test
    fun testGetAllAppointments() = testApplication {
        application { module() }
        client.get("/appointments").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }

    @Test
    fun testGetAppointmentById() = testApplication {
        application { module() }
        client.get("/appointment/1").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }

    @Test
    fun testCreateAppointment() = testApplication {
        application { module() }
        val response = client.post("/appointment") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(Appointment.serializer(), testAppointment))
        }
        assertEquals(HttpStatusCode.Created, response.status)
    }

    @Test
    fun testUpdateAppointment() = testApplication {
        application { module() }
        val updatedAppointment = testAppointment.copy(note = "Updated note")
        val response = client.put("/appointment/1") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(Appointment.serializer(), updatedAppointment))
        }
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun testDeleteAppointment() = testApplication {
        application { module() }
        val response = client.delete("/appointment/1")
        assertEquals(HttpStatusCode.NoContent, response.status)
    }

    @Test
    fun testGetNonExistingAppointment() = testApplication {
        application { module() }
        client.get("/appointment/999").apply {
            assertEquals(HttpStatusCode.NotFound, status)
        }
    }

    @Test
    fun testDeleteNonExistingAppointment() = testApplication {
        application { module() }
        val response = client.delete("/appointment/999")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }
}