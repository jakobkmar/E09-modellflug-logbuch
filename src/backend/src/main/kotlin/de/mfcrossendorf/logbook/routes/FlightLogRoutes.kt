package de.mfcrossendorf.logbook.routes

import de.mfcrossendorf.logbook.CompleteFlightLogRequest
import de.mfcrossendorf.logbook.CreateFlightLogRequest
import de.mfcrossendorf.logbook.FlightData
import de.mfcrossendorf.logbook.database.awaitSingleOrNull
import de.mfcrossendorf.logbook.database.database
import de.mfcrossendorf.logbook.session.sessionOrThrow
import de.mfcrossendorf.logbook.util.time
import de.mfcrossendorf.logbook.util.today
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.*

fun Route.flightLogRoutes() = route("/flightlog") {
    // fetch details of a specific flight log by ID
    get("/{id}") {
        val id = call.parameters["id"]!!.toInt()

        val isAdmin = database.accountQueries.checkAdminById(id).executeAsOne()

        val protocol = database.flightQueries.getFlight(id).executeAsOneOrNull()
        if (protocol == null) {
            call.respond(HttpStatusCode.NotFound, "Could not find protocol with ID $id")
            return@get
        }
        call.respond(HttpStatusCode.OK, protocol)
    }

    authenticate("auth-session") {
        // creates a new flight log entry
        post("/create") {
            val session = call.sessionOrThrow()
            val createRequest = call.receive<CreateFlightLogRequest>()

            val openFlight = database.flightQueries.getOpenFlightByAccountId(
                accountId = session.sharedData.userId,
                date = Clock.System.today().toJavaLocalDate(),
            ).awaitSingleOrNull()

            if (openFlight != null) {
                call.respond(HttpStatusCode.BadRequest, "You already have an open flight log")
                return@post
            }

            if (createRequest.date > Clock.System.today()) {
                call.respond(HttpStatusCode.BadRequest, "Cannot create flight log for future date")
                return@post
            }

            val flightId = database.flightQueries.createFlight(
                account_id = session.sharedData.userId,
                date = createRequest.date.toJavaLocalDate(),
                flight_start = createRequest.flightStart.toJavaLocalTime(),
                flight_end = null,
                signature = createRequest.signature.toByteArray(Charsets.UTF_8),
                checked_first_aid = createRequest.checkedFirstAid,
                remarks = null,
                model_type = createRequest.modelType,
                model = null,
            ).awaitSingleOrNull()

            if (flightId == null) {
                call.respond(HttpStatusCode.InternalServerError, "Failed to create flight log")
            } else {
                call.respond(HttpStatusCode.Created, flightId)
            }
        }

        get("/getActive") {
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
                Clock.System.time()
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

        // DELETE request to delete a specific flight log by ID
        delete("/{id}") {
            val id = call.parameters["id"]?.toInt()
                ?: return@delete call.respond(HttpStatusCode.BadRequest, "Missing flight log ID")

            // Delete the flight log entry from the database
            database.flightQueries.deleteFlight(id)

            // Respond with a success message
            call.respond(HttpStatusCode.OK, "Flight log deleted successfully")
        }
    }

    authenticate("auth-session") {
        // GET request to fetch logs for a specific user
        get("all/{userId}") {
            val userId = call.parameters["userId"]!!

            // Retrieve flight logs for the specified user from the database
            val userFlightLogs = database.flightQueries.getFlightsByAccountId(id = userId.toInt()).executeAsList()

            // Check if flight logs were found for the user
            if (userFlightLogs.isNotEmpty()) {
                // Respond with the list of flight logs for the user
                call.respond(HttpStatusCode.OK, userFlightLogs)
            } else {
                // If no flight logs were found, respond with a message
                call.respond(HttpStatusCode.NotFound, "No flight logs found for user $userId")
            }
        }

        get("/all") {
            // Retrieve all flight logs from the database
            val allFlightLogs = database.flightQueries.getFlights().executeAsList()

            // Check if flight logs were found
            if (allFlightLogs.isNotEmpty()) {
                // Respond with the list of flight logs
                call.respond(HttpStatusCode.OK, allFlightLogs)
            } else {
                // If no flight logs were found, respond with a message
                call.respond(HttpStatusCode.NotFound, "Keine Flugprotokolle gefunden")
            }
        }
    }
}
