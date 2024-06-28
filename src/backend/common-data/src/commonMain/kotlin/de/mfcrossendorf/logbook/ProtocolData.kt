package de.mfcrossendorf.logbook

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@Serializable
@JsExport
class GetDayProtocolRequest(
    @Suppress("NON_EXPORTABLE_TYPE")
    val date: LocalDate,
)

@Serializable
@JsExport
class DayProtocolData(
    val sortedFlights: List<FlightData>,
    val sortedFlightDirectors: List<FlightDirectorData>,
)
