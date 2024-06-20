package de.mfcrossendorf.logbook.routes

import de.mfcrossendorf.logbook.database.database
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.flightDirectorRoutes() = route("/flightdirector") {
    // TODO: Implement Flighthead in Database and implement the following route
    get {
        println("GET /head")

        // TODO
        fun getCurrentFlightHead(): Unit? = null

        // get current Flugleiter
        val currentHead = getCurrentFlightHead()

        // If a current flight head is found, respond with the information
        if (currentHead != null) {
            call.respond(HttpStatusCode.OK, currentHead)
        } else {
            call.respond(HttpStatusCode.NotFound, "Kein Flugleiter gefunden")
        }
    }

    put("/{userId}") {
        val userId = call.parameters["userId"] !!

        println("PUT /head/${userId}")


        // Check if the user exists and has necessary permissions to be registered as the flight head
        val user = database.accountQueries.getAccountById(userId.toInt()).executeAsOneOrNull()
            ?: return@put call.respond(HttpStatusCode.NotFound, "Nutzer nicht gefunden")

        // Check additional conditions for the user to be registered as the flight head (e.g., admin rights)
        val isAdmin = database.accountQueries.checkAdminById(userId.toInt()).executeAsOne()
        if (!isAdmin) {
            return@put call.respond(HttpStatusCode.Forbidden, "Keine Flugleiterrechte")
        }

        //TODO: Implement Flighthead in Database
        // Register the user as the current flight head
        //registerAsFlightHead(userId)

        call.respond(HttpStatusCode.OK, "Nutzer $userId ist als Flugleiter registriert")
    }
}
