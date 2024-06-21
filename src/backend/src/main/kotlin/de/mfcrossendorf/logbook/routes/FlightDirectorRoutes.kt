package de.mfcrossendorf.logbook.routes

import de.mfcrossendorf.logbook.CurrentFlightDirectorResponse
import de.mfcrossendorf.logbook.FlightDirectorFilterRequest
import de.mfcrossendorf.logbook.FlightDirectorResponse
import de.mfcrossendorf.logbook.database.awaitList
import de.mfcrossendorf.logbook.database.awaitSingleOrNull
import de.mfcrossendorf.logbook.database.database
import de.mfcrossendorf.logbook.session.canSeeAllLogsSessionOrThrow
import de.mfcrossendorf.logbook.session.sessionOrThrow
import de.mfcrossendorf.logbook.util.currentTimeOrEndOfDay
import de.mfcrossendorf.logbook.util.time
import de.mfcrossendorf.logbook.util.today
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.*
import org.postgresql.util.PSQLException

fun Route.flightDirectorRoutes() = route("/flightdirector") {
    authenticate("auth-session") {
        // Get the current flight director
        get {
            call.sessionOrThrow()

            val currentDirector = database.flightDirectorQueries
                .getCurrentFlightDirector(date = Clock.System.today().toJavaLocalDate())
                .awaitSingleOrNull()

            if (currentDirector != null) {
                // If a current flight director is found, respond with the information
                call.respond(HttpStatusCode.OK, CurrentFlightDirectorResponse(
                    userId = currentDirector.account_id,
                    username = currentDirector.username,
                    fullName = "${currentDirector.first_name} ${currentDirector.last_name.orEmpty()}".trim(),
                ))
            } else {
                // If no flight director is found, respond with a 404
                call.respond(HttpStatusCode.NotFound, "No flight director found")
            }
        }

        // Register the logged-in user as the flight director
        post("/login") {
            val session = call.sessionOrThrow()

            val directorId = try {
                database.flightDirectorQueries.addFlightDirector(
                    account_id = session.sharedData.userId,
                    date = Clock.System.today().toJavaLocalDate(),
                    start_time = Clock.System.time()
                        .let { now -> LocalTime(hour = now.hour, minute = now.minute, second = now.second) }
                        .toJavaLocalTime(),
                ).awaitSingleOrNull()
            } catch (exc: PSQLException) {
                call.application.log.warn("Failed to set flight director, ${exc.message}")
                call.respond(HttpStatusCode.Conflict, "Cannot set flight director since one is already set")
                return@post
            }

            if (directorId != null) {
                call.respond(HttpStatusCode.OK, directorId)
            } else {
                call.respond(HttpStatusCode.InternalServerError, "Failed to set flight director")
            }
        }

        // Unregister the logged-in user as the flight director
        post("/logout") {
            val session = call.sessionOrThrow()

            val directorId = database.flightDirectorQueries.removeFlightDirector(
                account_id = session.sharedData.userId,
                date = Clock.System.today().toJavaLocalDate(),
                end_time = Clock.System.today().currentTimeOrEndOfDay().toJavaLocalTime(),
            ).awaitSingleOrNull()

            if (directorId != null) {
                call.respond(HttpStatusCode.OK, directorId)
            } else {
                call.respond(HttpStatusCode.NotFound, "The user is not the flight director")
            }
        }

        route("/all") {
            // Fetch all flight logs by the logged-in user
            post("/filtered") {
                call.canSeeAllLogsSessionOrThrow()
                val filterRequest = call.receive<FlightDirectorFilterRequest>()

                val flightLogs = database.flightDirectorQueries
                    .getFlightDirectors(limit = filterRequest.limit.toLong())
                    .awaitList()
                    .map { dbDirector ->
                        FlightDirectorResponse(
                            username = dbDirector.username,
                            date = dbDirector.date.toKotlinLocalDate(),
                            startTime = dbDirector.start_time.toKotlinLocalTime(),
                            endTime = dbDirector.end_time?.toKotlinLocalTime(),
                        )
                    }

                call.respond(HttpStatusCode.OK, flightLogs)
            }
        }
    }
}
