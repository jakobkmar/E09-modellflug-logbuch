package de.mfcrossendorf.logbook

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@Serializable
@JsExport
class CreateFlightLogRequest(
    @Suppress("NON_EXPORTABLE_TYPE") // fallback to any is okay here
    val flightStart: LocalDateTime,          // since it is represented by an ISO 8601 string
    val signature: String,
    val checkedFirstAid: Boolean,
    val modelType: String,
)
