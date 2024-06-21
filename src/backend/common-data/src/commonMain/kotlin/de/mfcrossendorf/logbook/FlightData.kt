package de.mfcrossendorf.logbook

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@Serializable
@JsExport
class CreateFlightLogRequest(
    @Suppress("NON_EXPORTABLE_TYPE") // fallback to any is okay here
    val date: LocalDate,                     // since it is represented by an ISO 8601 string
    @Suppress("NON_EXPORTABLE_TYPE")
    val flightStart: LocalTime,
    val signature: String,
    val checkedFirstAid: Boolean,
    val modelType: String,
)

@Serializable
@JsExport
class CompleteFlightLogRequest(
    val flightId: Int,
    val remarks: String?,
)

@Serializable
@JsExport
class FlightData(
    val flightId: Int,
    val accountId: Int,
    val fullPilotName: String,
    @Suppress("NON_EXPORTABLE_TYPE")
    val date: LocalDate,
    @Suppress("NON_EXPORTABLE_TYPE")
    val flightStart: LocalTime,
    @Suppress("NON_EXPORTABLE_TYPE")
    val flightEnd: LocalTime?,
    val signature: String?,
    val checkedFirstAid: Boolean,
    val remarks: String?,
    val modelType: String,
) {
    companion object {
        private val dateFormat = LocalDate.Format {
            dayOfMonth()
            char('.')
            monthNumber()
            char('.')
            year()
        }
        private val timeFormat = LocalTime.Format {
            hour()
            char(':')
            minute()
        }
    }

    val dateString = date.format(dateFormat)
    val timePeriodString = run {
        val start = flightStart.format(timeFormat)
        val end = flightEnd?.format(timeFormat) ?: "..."
        "$start ➔ $end"
    }
}
