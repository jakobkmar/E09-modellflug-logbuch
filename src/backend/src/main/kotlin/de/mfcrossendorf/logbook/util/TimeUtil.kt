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
        val now = Clock.System.time()
        LocalTime(hour = now.hour, minute = now.minute, second = now.second)
    } else {
        LocalTime.parse("23:59", LocalTime.Formats.ISO)
    }
}
