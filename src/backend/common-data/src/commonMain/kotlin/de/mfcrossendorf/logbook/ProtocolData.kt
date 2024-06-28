package de.mfcrossendorf.logbook

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@Serializable
@JsExport
/**
 * Request to get the aggregated protocol data for a specific day.
 */
class GetDayProtocolRequest(
    @Suppress("NON_EXPORTABLE_TYPE")
    /**
     * The date of the aggregated protocol to get.
     */
    val date: LocalDate,
)

@Serializable
@JsExport
/**
 * The aggregated protocol data for a specific day.
 */
class DayProtocolData(
    /**
     * Flights on the specific day, sorted by start time.
     */
    val sortedFlights: List<FlightData>,
    /**
     * Flight directors on the specific day, sorted by start time.
     */
    val sortedFlightDirectors: List<FlightDirectorData>,
)
