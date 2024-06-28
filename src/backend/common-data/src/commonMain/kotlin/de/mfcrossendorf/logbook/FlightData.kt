package de.mfcrossendorf.logbook

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@Serializable
@JsExport
/**
 * Request to create a new flight log entry.
 */
class CreateFlightLogRequest(
    // fallback to any is okay here, since it is represented by an ISO 8601 string
    @Suppress("NON_EXPORTABLE_TYPE")
    /**
     * The date of the flight.
     */
    val date: LocalDate,
    @Suppress("NON_EXPORTABLE_TYPE")
    /**
     * The time the flight started.
     */
    val flightStart: LocalTime,
    @Suppress("NON_EXPORTABLE_TYPE")
    /**
     * The time the flight ended or null if the flight is new / still ongoing.
     */
    val flightEnd: LocalTime?,
    /**
     * The signature (base64 img url) of the pilot.
     */
    val signature: String,
    /**
     * Whether the first aid kit was checked and/or is present.
     */
    val checkedFirstAid: Boolean,
    /**
     * The type of the model used for the flight.
     */
    val modelType: String,
)

@Serializable
@JsExport
/**
 * Request to complete a specific flight log entry.
 */
class CompleteFlightLogRequest(
    /**
     * The ID of the flight log entry.
     */
    val flightId: Int,
    /**
     * Remarks about the flight. E.g., special occurrences or problems.
     */
    val remarks: String?,
)

@Serializable
@JsExport
/**
 * Flight log entry data, useful for displaying a flight log at any place.
 */
class FlightData(
    /**
     * The ID of the flight log entry.
     */
    val flightId: Int,
    /**
     * The ID of the account that created the flight log entry.
     */
    val accountId: Int,
    /**
     * The full name of the pilot, put together by the backend.
     */
    val fullPilotName: String,
    @Suppress("NON_EXPORTABLE_TYPE")
    /**
     * The date of the flight.
     */
    val date: LocalDate,
    @Suppress("NON_EXPORTABLE_TYPE")
    /**
     * The time the flight started.
     */
    val flightStart: LocalTime,
    @Suppress("NON_EXPORTABLE_TYPE")
    /**
     * The time the flight ended or null if the flight is still ongoing.
     */
    val flightEnd: LocalTime?,
    /**
     * The signature of the pilot.
     */
    val signature: String?,
    /**
     * Whether the first aid kit was checked and/or is present.
     */
    val checkedFirstAid: Boolean,
    /**
     * Remarks about the flight. E.g., special occurrences or problems.
     */
    val remarks: String?,
    /**
     * The type of the model used for the flight.
     */
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

    /**
     * The date of the flight as a formatted string.
     */
    val dateString = date.format(dateFormat)

    /**
     * The time period of the flight as a formatted string.
     */
    val timePeriodString = run {
        val start = flightStart.format(timeFormat)
        val end = flightEnd?.format(timeFormat) ?: "..."
        "$start ➔ $end"
    }
}

@Serializable
@JsExport
/**
 * Request to filter flight logs by a time period.
 */
class FlightLogFilterRequest(
    @Suppress("NON_EXPORTABLE_TYPE")
    /**
     * The start date of the time period to filter for.
     */
    val startDate: LocalDate,
    @Suppress("NON_EXPORTABLE_TYPE")
    /**
     * The end date of the time period to filter for.
     */
    val endDate: LocalDate,
)
