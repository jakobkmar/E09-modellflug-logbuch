package de.mfcrossendorf.logbook.routes

import de.mfcrossendorf.logbook.DayProtocolData
import de.mfcrossendorf.logbook.FlightData
import de.mfcrossendorf.logbook.FlightDirectorData
import de.mfcrossendorf.logbook.GetDayProtocolRequest
import de.mfcrossendorf.logbook.database.awaitList
import de.mfcrossendorf.logbook.database.database
import de.mfcrossendorf.logbook.session.canSeeAllLogsSessionOrThrow
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.async
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import kotlinx.datetime.toKotlinLocalTime

fun Route.protocolRoutes() = route("/protocol") {
    // Fetches aggregated flight data for a specific day
    post("/day") {
        call.canSeeAllLogsSessionOrThrow()

        val getRequest = call.receive<GetDayProtocolRequest>()

        val flights = async {
            database.flightQueries.getFlightsOnDate(getRequest.date.toJavaLocalDate())
                .awaitList()
        }
        val flightDirectors = async {
            database.flightDirectorQueries.getFlightDirectorsOnDate(getRequest.date.toJavaLocalDate())
                .awaitList()
        }

        call.respond(HttpStatusCode.OK, DayProtocolData(
            sortedFlights = flights.await().map { dbFlight ->
                FlightData(
                    flightId = dbFlight.flight_id,
                    accountId = dbFlight.account_id,
                    fullPilotName = "${dbFlight.first_name} ${dbFlight.last_name.orEmpty()}".trim(),
                    date = dbFlight.date.toKotlinLocalDate(),
                    flightStart = dbFlight.flight_start.toKotlinLocalTime(),
                    flightEnd = dbFlight.flight_end?.toKotlinLocalTime(),
                    signature = dbFlight.signature.toString(Charsets.UTF_8),
                    checkedFirstAid = dbFlight.checked_first_aid,
                    remarks = dbFlight.remarks,
                    modelType = dbFlight.model_type,
                )
            },
            sortedFlightDirectors = flightDirectors.await().map { dbDirector ->
                FlightDirectorData(
                    username = dbDirector.username,
                    fullName = "${dbDirector.first_name} ${dbDirector.last_name.orEmpty()}".trim(),
                    date = dbDirector.date.toKotlinLocalDate(),
                    startTime = dbDirector.start_time.toKotlinLocalTime(),
                    endTime = dbDirector.end_time?.toKotlinLocalTime(),
                )
            },
        ))
    }
}
