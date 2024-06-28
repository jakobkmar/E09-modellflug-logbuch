package de.mfcrossendorf.logbook.routes

import de.mfcrossendorf.logbook.AppState
import de.mfcrossendorf.logbook.database.awaitList
import de.mfcrossendorf.logbook.database.awaitSingleOrNull
import de.mfcrossendorf.logbook.database.database
import de.mfcrossendorf.logbook.util.time
import de.mfcrossendorf.logbook.util.today
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalTime
import kotlin.time.Duration.Companion.seconds

fun Route.appStateRoutes() = route("/appstate") {
    authenticate("auth-session") {
        val appStateFlow = MutableSharedFlow<AppState>(
            replay = 1)
        CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                appStateFlow.emit(requestAppState())
                delay(10.seconds)
            }
        }

        webSocket("/live") {
            appStateFlow.emit(requestAppState())
            appStateFlow.collect { sendSerialized(it) }
        }
    }
}

private suspend fun requestAppState(): AppState = coroutineScope {
    val today = Clock.System.today().toJavaLocalDate()
    val time = Clock.System.time().toJavaLocalTime()

    val activePilots = async {
        database.flightQueries
            .getActivePilots(date = today, currentTime = time)
            .awaitList().distinct()
    }
    val openPilots = async {
        database.flightQueries
            .getOpenPilots(date = today)
            .awaitList().distinct()
    }
    val flightDirector = async {
        database.flightDirectorQueries
            .getCurrentFlightDirectorAccountId(date = today)
            .awaitSingleOrNull()
    }

    AppState(
        activePilots = activePilots.await(),
        openPilots = openPilots.await(),
        currentFlightDirector = flightDirector.await()
    )
}
