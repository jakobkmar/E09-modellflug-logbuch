package de.mfcrossendorf.logbook.data

import kotlinx.datetime.TimeZone
import kotlinx.serialization.Serializable

@Serializable
data class AppConfiguration(
    val timeZone: TimeZone? = null,
) {
    companion object {
        val current = AppConfiguration()
    }
}
