package de.mfcrossendorf.logbook

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@Serializable
@JsExport
data class NewFlightLog(
    val protocolId: Int,
    val creatorId: Int,
    @Suppress("NON_EXPORTABLE_TYPE") // fallback to any is okay here
    val flightStart: LocalDateTime,          // since it is represented by an ISO 8601 string
    @Suppress("NON_EXPORTABLE_TYPE")
    val flightEnd: LocalDateTime,
    val signature: String,
    val checkedFirstAid: Boolean,
    val remarks: String,
    val model: String,
)
