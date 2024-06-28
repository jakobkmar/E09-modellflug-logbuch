package de.mfcrossendorf.logbook

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@Serializable
@JsExport
/**
 * Flight director data, containing info for useful for displaying a history.
 */
class FlightDirectorData(
    /**
     * The user ID of the flight director.
     */
    val username: String,
    /**
     * The full name of the flight director.
     */
    val fullName: String,
    @Suppress("NON_EXPORTABLE_TYPE")
    /**
     * The date of the flight director assignment.
     */
    val date: LocalDate,
    @Suppress("NON_EXPORTABLE_TYPE")
    /**
     * The time the flight director assignment started.
     */
    val startTime: LocalTime,
    @Suppress("NON_EXPORTABLE_TYPE")
    /**
     * The time the flight director assignment ended or null if the assignment is still ongoing.
     */
    val endTime: LocalTime?,
)

@Serializable
@JsExport
/**
 * Response to with the currently active flight director account info.
 */
class CurrentFlightDirectorResponse(
    /**
     * The user ID of the flight director.
     */
    val userId: Int,
    /**
     * The username of the flight director.
     */
    val username: String,
    /**
     * The full name of the flight director, put together in the backend.
     */
    val fullName: String,
)

@Serializable
@JsExport
/**
 * Request to get flight director history.
 */
class FlightDirectorFilterRequest(
    /**
     * The number of flight director entries to query.
     */
    val limit: Int,
)
