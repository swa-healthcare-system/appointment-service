package cz.mokripat.appointment

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KProperty

class LoggerDelegate {
    operator fun getValue(thisRef: Any, property: KProperty<*>): Logger {
        return LoggerFactory.getLogger(thisRef.javaClass)
    }
}