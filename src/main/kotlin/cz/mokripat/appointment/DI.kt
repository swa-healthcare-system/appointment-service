package cz.mokripat.appointment

import cz.mokripat.appointment.config.AppConfig
import cz.mokripat.appointment.repository.AppointmentRepository
import cz.mokripat.appointment.repository.DoctorAvailabilityRepository
import cz.mokripat.appointment.repository.InMemoryDoctorAvailabilityRepository
import cz.mokripat.appointment.repository.PostgreAppointmentRepository
import cz.mokripat.appointment.service.*
import org.koin.core.module.Module
import org.koin.dsl.module


fun appModule(appConfig: AppConfig): Module = module {
    single<AppointmentRepository> { PostgreAppointmentRepository() }
    single<DoctorAvailabilityRepository> { InMemoryDoctorAvailabilityRepository() }
    single<AppointmentProducerService> { AppointmentProducerServiceImpl(appConfig.kafkaBroker) }
    single (createdAtStart = true) { DoctorConsumerService(get(), get(), appConfig.kafkaBroker) }
    single { AppointmentService(get(), get(), get()) }
}