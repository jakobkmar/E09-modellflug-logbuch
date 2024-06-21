package de.mfcrossendorf.logbook.routes

import de.mfcrossendorf.logbook.AppState
import de.mfcrossendorf.logbook.database.awaitList
import de.mfcrossendorf.logbook.database.awaitSingleOrNull
import de.mfcrossendorf.logbook.database.database
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlinx.coroutines.async

fun Route.appStateRoutes() = route("/appstate") {
    webSocket {
        val activePilots = async {
            database.flightQueries.getActivePilots()
                .awaitList().distinct()
        }
        val flightDirector = async {
            database.flightDirectorQueries.getCurrentFlightDirector()
                .awaitSingleOrNull()
        }

        sendSerialized(AppState(
            activePilots = activePilots.await(),
            currentFlightDirector = flightDirector.await()
        ))
    }
}
