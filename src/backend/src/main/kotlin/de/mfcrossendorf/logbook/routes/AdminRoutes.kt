package de.mfcrossendorf.logbook.routes

import de.mfcrossendorf.logbook.database
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Routing.adminRoutes() = route("/admin") {
    // GET request to fetch all flight logs with filtering options
    get("/flightlogs") {
        // Retrieve all flight logs from the database
        val allFlightLogs = database.protocolQueries.getProtocols().executeAsList()

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
