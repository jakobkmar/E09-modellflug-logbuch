package de.mfcrossendorf.logbook.util

import de.mfcrossendorf.logbook.config.ConfigManager
import kotlinx.datetime.*

fun Clock.today(): LocalDate {
    return todayIn(ConfigManager.config.timeZone
        ?: TimeZone.currentSystemDefault())
}

fun Clock.time(): LocalTime {
    return now().toLocalDateTime(ConfigManager.config.timeZone
        ?: TimeZone.currentSystemDefault()).time
}

fun LocalDate.currentTimeOrEndOfDay(): LocalTime {
    return if (this == Clock.System.today()) {
        Clock.System.time().simplify()
    } else {
        LocalTime.parse("23:59", LocalTime.Formats.ISO)
    }
}

fun LocalTime.simplify(): LocalTime {
    return LocalTime(hour = hour, minute = minute, second = second)
}
