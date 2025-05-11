package cz.mokripat

import cz.mokripat.appointment.configureHTTP
import cz.mokripat.appointment.model.Appointment
import cz.mokripat.appointment.model.AvailabilityPayload
import cz.mokripat.appointment.model.DoctorPayload
import cz.mokripat.appointment.model.configureSerialization
import cz.mokripat.appointment.repository.AppointmentRepository
import cz.mokripat.appointment.repository.DoctorAvailabilityRepository
import cz.mokripat.appointment.repository.MockAppointmentRepository
import cz.mokripat.appointment.routes.configureRouting
import cz.mokripat.appointment.service.AppointmentProducerService
import cz.mokripat.appointment.service.AppointmentService
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.dsl.module
import org.koin.ktor.ext.inject
import kotlin.test.*

class ApplicationTest {

    private val mockRepository = MockAppointmentRepository()

    private val testModule = module {
        single<AppointmentRepository> { mockRepository }
        single<DoctorAvailabilityRepository> {
            object : DoctorAvailabilityRepository {
                override fun addDoctor(payload: DoctorPayload) {
                    Unit
                }

                override fun removeDoctor(doctorId: String) {
                    Unit
                }

                override fun getDoctorAvailability(doctorId: String): List<String> {
                    return listOf("2025-05-11")
                }

                override fun addAvailability(payload: AvailabilityPayload): Boolean {
                    return false
                }

                override fun removeAvailability(payload: AvailabilityPayload): Boolean {
                    return false
                }
            }
        }
        single<AppointmentProducerService> {
            object : AppointmentProducerService {
                override fun produceServiceStarted() {
                    Unit
                }

                override fun produceAppointmentCreated(appointment: Appointment) {
                    Unit
                }

                override fun produceAppointmentCanceled(appointment: Appointment) {
                    Unit
                }

            } }
        single { AppointmentService(get(), get(), get()) }
    }

    private fun Application.testingModule() {
        startKoin {
            modules(testModule)
        }

        val appointmentService: AppointmentService by inject()

        configureHTTP()
        configureSerialization()
        configureRouting(appointmentService)
    }

    private val testAppointment = Appointment(
        id = 1,
        doctorId = "doc123",
        patientId = "pat456",
        fromTS = "2025-05-11T07:19:51.796Z",
        toTS = "2025-05-11T07:19:51.796Z",
        note = "Follow-up appointment",
        status = Appointment.Status.BOOKED
    )

    private val testAppointment2 = Appointment(
        id = 2,
        doctorId = "doc123",
        patientId = "pat457",
        fromTS = "2025-05-11T07:19:51.796Z",
        toTS = "2025-05-11T07:19:51.796Z",
        note = "Follow-up appointment",
        status = Appointment.Status.BOOKED
    )

    @AfterTest
    fun teardown() {
        mockRepository.clear()
        stopKoin()
    }

    @Test
    fun testRoot() = testApplication {
        application { testingModule() }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("AppointmentService is alive!", bodyAsText())
        }
    }

    @Test
    fun testGetAllAppointments() = testApplication {
        application { testingModule() }
        mockRepository.addAppointment(testAppointment)
        mockRepository.addAppointment(testAppointment2)
        val response = client.get("/appointments").apply {
            assertEquals(HttpStatusCode.OK, status)
        }

        val responseBody = response.bodyAsText()
        val returnedAppointments = Json.decodeFromString<List<Appointment>>(responseBody)

        assertContains(returnedAppointments, testAppointment)
        assertContains(returnedAppointments, testAppointment2)
    }

    @Test
    fun testCreateAppointment() = testApplication {
        application { testingModule() }
        val response = client.post("/appointment") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(Appointment.serializer(), testAppointment))
        }
        assertEquals(HttpStatusCode.Created, response.status)
    }

    @Test
    fun testGetAppointmentById() = testApplication {
        application { testingModule() }
        mockRepository.addAppointment(testAppointment)
        val response = client.get("/appointment/1")

        val responseBody = response.bodyAsText()
        val returnedAppointment = Json.decodeFromString(Appointment.serializer(), responseBody)

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(testAppointment, returnedAppointment)
    }

    @Test
    fun testUpdateAppointment() = testApplication {
        application { testingModule() }
        mockRepository.addAppointment(testAppointment)
        val updatedAppointment = testAppointment.copy(note = "Updated note")
        val response = client.put("/appointment/1") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(Appointment.serializer(), updatedAppointment))
        }

        val responseBody = response.bodyAsText()
        val returnedAppointment = Json.decodeFromString(Appointment.serializer(), responseBody)

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(updatedAppointment, returnedAppointment)
    }

    @Test
    fun testDeleteAppointment() = testApplication {
        mockRepository.addAppointment(testAppointment)
        application { testingModule() }
        val response = client.delete("/appointment/1")
        assertEquals(HttpStatusCode.NoContent, response.status)
    }

    @Test
    fun testGetNonExistingAppointment() = testApplication {
        application { testingModule() }
        client.get("/appointment/999").apply {
            assertEquals(HttpStatusCode.NotFound, status)
        }
    }

    @Test
    fun testDeleteNonExistingAppointment() = testApplication {
        application { testingModule() }
        val response = client.delete("/appointment/999")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }
}
