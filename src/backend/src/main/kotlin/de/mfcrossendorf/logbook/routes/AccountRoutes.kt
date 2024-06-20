package de.mfcrossendorf.logbook.routes

import de.mfcrossendorf.logbook.database.awaitSingleOrNull
import de.mfcrossendorf.logbook.database.database
import de.mfcrossendorf.logbook.session.handleLoginCall
import de.mfcrossendorf.logbook.session.handleLogoutCall
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.accountRoutes() = route("/account") {
    post("/login") {
        handleLoginCall()
    }
    post("/logout") {
        handleLogoutCall()
    }

    route("/{userId}") {
        // GET request to show the profile page of the user with the given ID
        get {
            val userId = call.parameters["userId"] !!

            // Fetch user profile details from the database based on the user ID
            val userProfile = database.accountQueries.getAccountById(userId.toInt()).executeAsOneOrNull()
                ?: return@get call.respond(HttpStatusCode.NotFound, "Nutzer nicht gefunden")

            // Render and display the user profile page
            call.respond(HttpStatusCode.OK, "Profile page of user $userId: $userProfile")
        }

        // POST request to change the password of the user with the given ID
        post("/change-password") {
            val userId = call.parameters["userId"] !!

            // Extract new password from the request body
            val params = call.receiveParameters()
            val newPassword = params["newPassword"]
                ?: return@post call.respond(HttpStatusCode.BadRequest, "Neues Passwort eingeben")

            // Update the password for the user with the given ID in the database
            val changedAccount = database.accountQueries.changePasswordForAccount(userId, newPassword.toInt())
                .awaitSingleOrNull()
            if (changedAccount != null) {
                call.respond(HttpStatusCode.OK, "Passwort erfolgreich für Nutzer $userId geändert")
            } else {
                call.respond(HttpStatusCode.InternalServerError, "Passwort konnte nicht geändert werden")
            }
        }
    }
}
