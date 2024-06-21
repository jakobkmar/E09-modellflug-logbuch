package de.mfcrossendorf.logbook.util

import de.mfcrossendorf.logbook.data.AppConfiguration
import kotlinx.datetime.*

fun Clock.today(): LocalDate {
    return todayIn(AppConfiguration.current.timeZone
        ?: TimeZone.currentSystemDefault())
}

fun Clock.time(): LocalTime {
    return now().toLocalDateTime(AppConfiguration.current.timeZone
        ?: TimeZone.currentSystemDefault()).time
}
