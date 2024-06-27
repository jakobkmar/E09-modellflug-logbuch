package de.mfcrossendorf.logbook

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@Serializable
@JsExport
class FlightDirectorData(
    val username: String,
    val fullName: String,
    @Suppress("NON_EXPORTABLE_TYPE")
    val date: LocalDate,
    @Suppress("NON_EXPORTABLE_TYPE")
    val startTime: LocalTime,
    @Suppress("NON_EXPORTABLE_TYPE")
    val endTime: LocalTime?,
)

@Serializable
@JsExport
class CurrentFlightDirectorResponse(
    val userId: Int,
    val username: String,
    val fullName: String,
)

@Serializable
@JsExport
class FlightDirectorFilterRequest(
    val limit: Int,
)
