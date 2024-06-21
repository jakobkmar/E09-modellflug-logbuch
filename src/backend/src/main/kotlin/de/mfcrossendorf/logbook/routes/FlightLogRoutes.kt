package de.mfcrossendorf.logbook.routes

import de.mfcrossendorf.logbook.CompleteFlightLogRequest
import de.mfcrossendorf.logbook.CreateFlightLogRequest
import de.mfcrossendorf.logbook.FlightData
import de.mfcrossendorf.logbook.FlightLogFilterRequest
import de.mfcrossendorf.logbook.database.awaitList
import de.mfcrossendorf.logbook.database.awaitSingleOrNull
import de.mfcrossendorf.logbook.database.database
import de.mfcrossendorf.logbook.session.sessionOrThrow
import de.mfcrossendorf.logbook.util.time
import de.mfcrossendorf.logbook.util.today
import de.mfcrossendorf.logbook.validation.validateAndTrim
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.*

fun Route.flightLogRoutes() = route("/flightlog") {
    authenticate("auth-session") {
        // Create a new flight log entry
        post("/create") {
            val session = call.sessionOrThrow()
            val createRequest = call.receive<CreateFlightLogRequest>()
                .validateAndTrim()

            val openFlight = database.flightQueries.getOpenFlightByAccountId(
                accountId = session.sharedData.userId,
                date = Clock.System.today().toJavaLocalDate(),
            ).awaitSingleOrNull()

            if (openFlight != null) {
                call.respond(HttpStatusCode.Conflict, "You already have an open flight log")
                return@post
            }

            val flightId = database.flightQueries.createFlight(
                account_id = session.sharedData.userId,
                date = createRequest.date.toJavaLocalDate(),
                flight_start = createRequest.flightStart.toJavaLocalTime(),
                flight_end = null,
                checked_first_aid = createRequest.checkedFirstAid,
                remarks = null,
                model_type = createRequest.modelType,
                model = null,
                signature = createRequest.signature.toByteArray(Charsets.UTF_8),
            ).awaitSingleOrNull()

            if (flightId == null) {
                call.respond(HttpStatusCode.InternalServerError, "Failed to create flight log")
            } else {
                call.respond(HttpStatusCode.Created, flightId)
            }
        }

        // Complete the currently active flight log for the logged-in user
        post("/complete") {
            val session = call.sessionOrThrow()
            val completeRequest = call.receive<CompleteFlightLogRequest>()

            val flight = database.flightQueries.getFlight(completeRequest.flightId)
                .awaitSingleOrNull()
            if (flight == null) {
                call.respond(HttpStatusCode.NotFound, "Flight log not found")
                return@post
            }
            if (flight.account_id != session.sharedData.userId) {
                call.respond(HttpStatusCode.Forbidden, "You are not allowed to complete this flight log")
                return@post
            }

            val endTime = if (flight.date.toKotlinLocalDate() == Clock.System.today()) {
                val now = Clock.System.time()
                LocalTime(hour = now.hour, minute = now.minute, second = now.second)
            } else {
                LocalTime.parse("23:59", LocalTime.Formats.ISO)
            }

            val updatedFlightId = database.flightQueries.completeFlight(
                flightId = completeRequest.flightId,
                accountId = session.sharedData.userId,
                flightEnd = endTime.toJavaLocalTime(),
                remarks = completeRequest.remarks ?: flight.remarks,
            ).awaitSingleOrNull()

            if (updatedFlightId == null) {
                call.respond(HttpStatusCode.InternalServerError, "Failed to complete flight log")
            } else {
                call.respond(HttpStatusCode.OK, updatedFlightId)
            }
        }

        route("/active") {
            // Fetch the currently active flight log for the logged-in user
            get {
                val session = call.sessionOrThrow()

                val openFlight = database.flightQueries.getOpenFlightByAccountId(
                    accountId = session.sharedData.userId,
                    date = Clock.System.today().toJavaLocalDate(),
                ).awaitSingleOrNull()

                if (openFlight == null) {
                    call.respond(HttpStatusCode.NotFound, "No open flight log found")
                } else {
                    call.respond(HttpStatusCode.OK, FlightData(
                        flightId = openFlight.flight_id,
                        accountId = openFlight.account_id,
                        fullPilotName = "${openFlight.first_name} ${openFlight.last_name.orEmpty()}".trim(),
                        date = openFlight.date.toKotlinLocalDate(),
                        flightStart = openFlight.flight_start.toKotlinLocalTime(),
                        flightEnd = openFlight.flight_end?.toKotlinLocalTime(),
                        signature = openFlight.signature.toString(Charsets.UTF_8),
                        checkedFirstAid = openFlight.checked_first_aid,
                        remarks = openFlight.remarks,
                        modelType = openFlight.model_type,
                    ))
                }
            }

            // Fetch all active flight logs by all users
            get("/allUsers") {
                call.sessionOrThrow()

                val openFlights = database.flightQueries
                    .getOpenFlights(date = Clock.System.today().toJavaLocalDate())
                    .awaitList()
                    .map { dbFlight ->
                        FlightData(
                            flightId = dbFlight.flight_id,
                            accountId = dbFlight.account_id,
                            fullPilotName = "${dbFlight.first_name} ${dbFlight.last_name.orEmpty()}".trim(),
                            date = dbFlight.date.toKotlinLocalDate(),
                            flightStart = dbFlight.flight_start.toKotlinLocalTime(),
                            flightEnd = dbFlight.flight_end?.toKotlinLocalTime(),
                            signature = null,
                            checkedFirstAid = dbFlight.checked_first_aid,
                            remarks = dbFlight.remarks,
                            modelType = dbFlight.model_type,
                        )
                    }

                call.respond(HttpStatusCode.OK, openFlights)
            }
        }

        route("/completed") {
            route("/allUsers") {
                get("/today") {
                    call.sessionOrThrow()

                    val completedFlights = database.flightQueries
                        .getCompletedFlights(date = Clock.System.today().toJavaLocalDate())
                        .awaitList()
                        .map { dbFlight ->
                            FlightData(
                                flightId = dbFlight.flight_id,
                                accountId = dbFlight.account_id,
                                fullPilotName = "${dbFlight.first_name} ${dbFlight.last_name.orEmpty()}".trim(),
                                date = dbFlight.date.toKotlinLocalDate(),
                                flightStart = dbFlight.flight_start.toKotlinLocalTime(),
                                flightEnd = dbFlight.flight_end.toKotlinLocalTime(),
                                signature = null,
                                checkedFirstAid = dbFlight.checked_first_aid,
                                remarks = dbFlight.remarks,
                                modelType = dbFlight.model_type,
                            )
                        }

                    call.respond(HttpStatusCode.OK, completedFlights)
                }
            }
        }

        get("/count") {
            val session = call.sessionOrThrow()

            val openFlightsCount = database.flightQueries
                .countByAccountId(accountId = session.sharedData.userId)
                .awaitSingleOrNull()

            if (openFlightsCount == null) {
                call.respond(HttpStatusCode.InternalServerError, "Failed to count flight logs")
                return@get
            }

            call.respond(HttpStatusCode.OK, openFlightsCount)
        }

        // Get a specific flight log entry
        get("/{id}") {
            // TODO: Implement this route
        }

        route("/all") {
            // Fetch all flight logs by the logged-in user
            post("/filtered") {
                val session = call.sessionOrThrow()
                val filterRequest = call.receive<FlightLogFilterRequest>()

                val flightLogs = database.flightQueries.getFlightsByAccountId(
                    accountId = session.sharedData.userId,
                    startDate = filterRequest.startDate.toJavaLocalDate(),
                    endDate = filterRequest.endDate.toJavaLocalDate(),
                ).awaitList().map { dbFlight ->
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
                }

                call.respond(HttpStatusCode.OK, flightLogs)
            }

            // Fetch all flight logs by a specific user
            get("/{userId}") {
                TODO("Implement this route")
            }
        }

        // Delete a specific flight log entry
        delete("/{id}") {
            TODO("Implement this route")
        }
    }
}
