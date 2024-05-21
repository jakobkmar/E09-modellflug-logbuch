package de.mfcrossendorf.logbook.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

data class NewFlightLog(
    val name: String,
)

data class UpdatedFlightLog(
    val name: String,
    val description: String)

val flightLogs = mutableListOf<NewFlightLog>()

fun getUserFlightLogs(userId: String): MutableList<NewFlightLog> {
    return flightLogs
};
fun getFlightLogById(id: String) {};
fun createFlightLog(requestBody: NewFlightLog) {};
fun updateFlightLog(id: String, updatedFlightLog: UpdatedFlightLog) {};
fun deleteFlightLog(id: String) {};
fun getAllFlightLogs(): MutableList<NewFlightLog> {
    return flightLogs
}

fun Routing.flightlogRoutes() = route("/flightlogs") {
    // GET request to fetch logs for a specific user
    route("/{userId}") {
        get {
            val userId = call.parameters["userId"] ?: return@get call.respond(HttpStatusCode.BadRequest, "User ID fehlt")

            // Retrieve flight logs for the specified user from the database
            val userFlightLogs = getUserFlightLogs(userId)

            // Check if flight logs were found for the user
            if (userFlightLogs.isNotEmpty()) {
                // Respond with the list of flight logs for the user
                call.respond(HttpStatusCode.OK, userFlightLogs)
            } else {
                // If no flight logs were found, respond with a message
                call.respond(HttpStatusCode.NotFound, "Keine Flugprotokolle für Nutzer $userId gefunden")
            }
        }

        // GET request to fetch details of a specific flight log by ID
        get("/{id}") {
            val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Keine Protokoll ID übergeben")

            // Retrieve flight log details from the database based on the provided ID
            val flightLog = getFlightLogById(id)

            // Check if flight log was found
            if (flightLog != null) {
                // Respond with the flight log details
                call.respond(HttpStatusCode.OK, flightLog)
            } else {
                // If flight log was not found, respond with a message
                call.respond(HttpStatusCode.NotFound, "Flugprotokoll mit ID $id nicht gefunden")
            }
        }

        // POST request to create a new flight log
        post("/") {
            // Receive data for the new flight log from the request body
            val requestBody = call.receive<NewFlightLog>()

            // Validate the received data (e.g., ensure all required fields are present)

            // Create a new flight log entry in the database
            val newFlightLogId = createFlightLog(requestBody)

            // Respond with the ID of the newly created flight log
            call.respond(HttpStatusCode.Created, mapOf("id" to newFlightLogId))
        }

        // PUT request to update details of a specific flight log by ID
        put("/{id}") {
            val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest, "Missing flight log ID")

            // Receive updated data for the flight log from the request body
            val updatedFlightLog = call.receive<UpdatedFlightLog>()

            // Validate the received data (e.g., ensure all required fields are present)

            // Update the flight log entry in the database
            updateFlightLog(id, updatedFlightLog)

            // Respond with a success message
            call.respond(HttpStatusCode.OK, "Flight log updated successfully")
        }

        // DELETE request to delete a specific flight log by ID
        delete("/{id}") {
            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest, "Missing flight log ID")

            // Delete the flight log entry from the database
            deleteFlightLog(id)

            // Respond with a success message
            call.respond(HttpStatusCode.OK, "Flight log deleted successfully")
        }

        // GET request to fetch weather information
        get("/weather") {
            // Logic to call weather station and fetch weather information
            // This could involve making an external API call to a weather service
        }
    }
}
