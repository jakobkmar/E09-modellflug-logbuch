package de.mfcrossendorf.logbook.routes

import de.mfcrossendorf.logbook.database
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.LocalTime

data class NewFlightLog(
    val protocolId: String = "",
    val creatorId: Int,
    val flightStart: LocalTime,
    val flightEnd: LocalTime,
    val signature: String,
    val checkedFirstAid: Boolean,
    val remarks: String = "",
    val model: String
)

data class UpdatedFlightLog(
    val protocolId: String,
    val creatorId: Int,
    val flightStart: LocalTime,
    val flightEnd: LocalTime,
    val signature: String,
    val checkedFirstAid: Boolean,
    val remarks: String = "",
    val model: String
)

    // TODO Anpassung dass man Zeiträume auswählen kann
    // TODO check ob Local Time richtig funktioniert
    // TODO eventuell Admin Route nach get("/{id}") migrieren

fun Routing.flightlogRoutes() = route("/flightlogs") {
    // GET request to fetch logs for a specific user
    route("/{userId}") {
        get {
            val userId = call.parameters["userId"]!!

            // Retrieve flight logs for the specified user from the database
            val userFlightLogs = database.protocolQueries.getProtocolsByCreator(id = userId.toInt()).executeAsList()

            // Check if flight logs were found for the user
            if (userFlightLogs.isNotEmpty()) {
                // Respond with the list of flight logs for the user
                call.respond(HttpStatusCode.OK, userFlightLogs)
            } else {
                // If no flight logs were found, respond with a message
                call.respond(HttpStatusCode.NotFound, "No flight logs found for user $userId")
            }
        }

        // fetch details of a specific flight log by ID
        get("/{id}") {
             val id = call.parameters["id"]!!

            val isAdmin = database.accountQueries.checkAdmin(account_id = id.toInt()).executeAsOne()

            val protocol = database.protocolQueries.getProtocol(id).executeAsOneOrNull()
            if (protocol == null) {
                call.respond(HttpStatusCode.NotFound, "Could not find protocol with ID $id")
                return@get
            }
            call.respond(HttpStatusCode.OK, protocol)
        }

        // POST request to create a new flight log
        post("/") {
            // Receive data for the new flight log from the request body
            val requestBody = call.receive<NewFlightLog>()

            // Validate the received data (e.g., ensure all required fields are present)
            if (requestBody.creatorId.toString().isEmpty() || requestBody.flightStart.toString().isEmpty() ||
                requestBody.flightEnd.toString().isEmpty() || requestBody.signature.isEmpty() ||
                requestBody.checkedFirstAid.toString().isEmpty() || requestBody.model.isEmpty()) {
                call.respond(HttpStatusCode.BadRequest, "All required fields must be filled")
                return@post
            }

            // Create a new flight log entry in the database
            database.protocolQueries.createProtocol(
                protocol_id = requestBody.protocolId,
                creator_id = requestBody.creatorId,
                flight_start = requestBody.flightStart,
                flight_end = requestBody.flightEnd,
                signature = requestBody.signature.toByteArray(),
                checked_first_aid = requestBody.checkedFirstAid,
                remarks = requestBody.remarks,
                model = requestBody.model
            )

            // Respond with the ID of the newly created flight log
            call.respond(HttpStatusCode.Created,
                mapOf("id" to database.protocolQueries.getLatestProtocolId(requestBody.creatorId)))
        }

        // PUT request to update details of a specific flight log by ID
        put("/{id}") {
            val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest, "Missing flight log ID")

            // Receive updated data for the flight log from the request body
            val updatedFlightLog = call.receive<UpdatedFlightLog>()

            // Validate the received data (e.g., ensure all required fields are present)
            if (updatedFlightLog.protocolId.isEmpty() ||updatedFlightLog.creatorId.toString().isEmpty() ||
                updatedFlightLog.flightStart.toString().isEmpty() || updatedFlightLog.flightEnd.toString().isEmpty() ||
                updatedFlightLog.signature.isEmpty() || updatedFlightLog.checkedFirstAid.toString().isEmpty() ||
                updatedFlightLog.model.isEmpty()) {
                call.respond(HttpStatusCode.BadRequest, "All required fields must be filled")
                return@put
            }
            // Update the flight log entry in the database
            database.protocolQueries.updateProtocol(
                flight_start = updatedFlightLog.flightStart,
                flight_end = updatedFlightLog.flightEnd,
                signature = updatedFlightLog.signature.toByteArray(),
                checked_first_aid = updatedFlightLog.checkedFirstAid,
                remarks = updatedFlightLog.remarks,
                model = updatedFlightLog.model,
                id = id
            )

            // Respond with a success message
            call.respond(HttpStatusCode.OK, "Flight log updated successfully")
        }

        // DELETE request to delete a specific flight log by ID
        delete("/{id}") {
            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest, "Missing flight log ID")

            // Delete the flight log entry from the database
            database.protocolQueries.deleteProtocol(id)

            // Respond with a success message
            call.respond(HttpStatusCode.OK, "Flight log deleted successfully")
        }

        // GET request to fetch weather information
        get("/weather") {
            // TODO Logic to call weather station and fetch weather information
            // This could involve making an external API call to a weather service
        }
    }
}
