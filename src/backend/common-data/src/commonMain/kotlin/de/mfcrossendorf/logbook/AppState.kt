package de.mfcrossendorf.logbook

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@Serializable
@JsExport
/**
 * The current state of the application, the same for all users, meant to be sent live.
 */
data class AppState(
    /**
     * The user IDs of the pilots that are currently active.
     */
    val activePilots: List<Int>,
    /**
     * The user IDs of the pilots that are currently having an active flight session.
     */
    val openPilots: List<Int>,
    /**
     * The ID of the pilot that is currently the flight director.
     */
    val currentFlightDirector: Int?,
)
