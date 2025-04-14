package cz.mokripat.appointment

import cz.mokripat.appointment.config.AppConfig
import cz.mokripat.appointment.repository.AppointmentRepository
import cz.mokripat.appointment.repository.PostgreAppointmentRepository
import cz.mokripat.appointment.service.AppointmentConsumerService
import cz.mokripat.appointment.service.AppointmentProducerService
import cz.mokripat.appointment.service.AppointmentProducerServiceImpl
import cz.mokripat.appointment.service.AppointmentService
import org.koin.core.module.Module
import org.koin.dsl.module


fun appModule(appConfig: AppConfig): Module = module {
    single<AppointmentRepository> { PostgreAppointmentRepository() }
    single<AppointmentProducerService> { AppointmentProducerServiceImpl(appConfig.kafkaBroker) }
    single { AppointmentConsumerService(appConfig.kafkaBroker) }
    single { AppointmentService(get(), get()) }
}