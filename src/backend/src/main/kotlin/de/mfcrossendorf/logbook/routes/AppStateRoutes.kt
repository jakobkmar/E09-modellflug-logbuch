package de.mfcrossendorf.logbook.routes

import de.mfcrossendorf.logbook.AppState
import de.mfcrossendorf.logbook.database.awaitList
import de.mfcrossendorf.logbook.database.awaitSingleOrNull
import de.mfcrossendorf.logbook.database.database
import de.mfcrossendorf.logbook.util.today
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlinx.coroutines.async
import kotlinx.datetime.Clock
import kotlinx.datetime.toJavaLocalDate

fun Route.appStateRoutes() = route("/appstate") {
    authenticate("auth-session") {
        webSocket("/live") {
            val activePilots = async {
                database.flightQueries.getActivePilots(date = Clock.System.today().toJavaLocalDate())
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
}
