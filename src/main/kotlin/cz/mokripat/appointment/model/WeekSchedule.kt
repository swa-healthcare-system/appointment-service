package cz.mokripat.appointment.model

import io.ktor.util.date.*
import java.time.LocalTime

// Represents a time interval within a day
data class TimeInterval(
    val start: LocalTime,
    val end: LocalTime
)

// Represents a daily schedule with working intervals and possible breaks
data class DaySchedule(
    val day: WeekDay,
    val workIntervals: List<TimeInterval> = emptyList()
)

// Represents a weekly schedule with unique identification
data class WeekSchedule(
    val id: String,
    val name: String,
    val week: List<DaySchedule>
)
