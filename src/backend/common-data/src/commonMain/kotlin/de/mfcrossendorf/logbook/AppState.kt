package de.mfcrossendorf.logbook

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@Serializable
@JsExport
data class AppState(
    val activePilots: List<Int>,
    val openPilots: List<Int>,
    val currentFlightDirector: Int?,
)
