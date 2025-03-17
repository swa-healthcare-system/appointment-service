package cz.mokripat.appointment

import cz.mokripat.appointment.repository.AppointmentRepository
import cz.mokripat.appointment.repository.PostgreAppointmentRepository
import cz.mokripat.appointment.service.AppointmentProducerService
import cz.mokripat.appointment.service.AppointmentProducerServiceImpl
import cz.mokripat.appointment.service.AppointmentService
import org.koin.core.module.Module
import org.koin.dsl.module


val appModule: Module = module {
    single<AppointmentRepository> { PostgreAppointmentRepository() }
    single<AppointmentProducerService> { AppointmentProducerServiceImpl() }
    single { AppointmentService(get(), get()) }
}