package de.mfcrossendorf.logbook.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun getUserByUsername(username: String): User {
    // fetch user from database
    return User("username", "password", false, false)
}
fun getCurrentFlightHead() {}
fun getUserById(userId: String) {}
fun checkAdminRights(user: Unit): Boolean {
    return true
}
fun registerAsFlightHead(userId: String) {}
fun getUserProfile(userId: String) {}
fun changeUserPassword(userId: String, newPassword: String): Boolean {
    return true
}


// temp data types
data class User(
    val username: String,
    val password: String,
    val twoFactorEnabled: Boolean,
    val isAdmin: Boolean
)

fun Routing.profileRoutes() {
    post("/login") {
        val params = call.receiveParameters()
        val username = params["username"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Eingabe Nutzername fehlt")
        val password = params["password"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Eingabe Passwort fehlt")

        //check User
        val user = getUserByUsername(username)
        if (user == null) {
            call.respond(HttpStatusCode.Unauthorized, "Invalid username or password")
            return@post
        }

        // check password
        if (user.password != password) {
            call.respond(HttpStatusCode.Unauthorized, "Invalid username or password")
            return@post
        }

        // check 2 FA
        if (user.twoFactorEnabled) {
            // Hier würde die Überprüfung des zweiten Faktors erfolgen
            // In diesem Beispiel ist die Zwei-Faktor-Authentifizierung immer erfolgreich
            // In einer realen Anwendung müsstest du die Zwei-Faktor-Authentifizierung implementieren
            // und das Ergebnis hier überprüfen.
        }

        // auth successful
        call.respond(HttpStatusCode.OK, "Login successful")
    }

    get("/head") {
        // get current Flugleiter
        val currentHead = getCurrentFlightHead()

        // If a current flight head is found, respond with the information
        if (currentHead != null) {
            call.respond(HttpStatusCode.OK, currentHead)
        } else {
            call.respond(HttpStatusCode.NotFound, "Kein Flugleiter gefunden")
        }
    }

    put("/head/{userId}") {
        val userId = call.parameters["userId"] ?: return@put call.respond(HttpStatusCode.BadRequest, "User ID fehlt")

        // Check if the user exists and has necessary permissions to be registered as the flight head
        val user = getUserById(userId)
        if (user == null) {
            return@put call.respond(HttpStatusCode.NotFound, "Nutzer nicht gefunden")
        }

        // Check additional conditions for the user to be registered as the flight head (e.g., admin rights)
        val isAdmin = checkAdminRights(user)
        if (!isAdmin) {
            return@put call.respond(HttpStatusCode.Forbidden, "Keine Flugleiterrechte")
        }

        // Register the user as the current flight head
        registerAsFlightHead(userId)

        call.respond(HttpStatusCode.OK, "Nutzer $userId ist als Flugleiter registriert")
    }

    route("/profile/{userId}") {
        // GET request to show the profile page of the user with the given ID
        get {
            val userId = call.parameters["userId"] ?: return@get call.respond(HttpStatusCode.BadRequest, "User ID fehlt")

            // Fetch user profile details from the database based on the user ID
            val userProfile = getUserProfile(userId)
            if (userProfile == null) {
                return@get call.respond(HttpStatusCode.NotFound, "Nutzer nicht gefunden")
            }

            // Render and display the user profile page
            call.respond(HttpStatusCode.OK, "Profile page of user $userId: $userProfile")
        }

        // POST request to change the password of the user with the given ID
        post("/change-password") {
            val userId = call.parameters["userId"] ?: return@post call.respond(HttpStatusCode.BadRequest, "User ID fehlt")

            // Extract new password from the request body
            val params = call.receiveParameters()
            val newPassword = params["newPassword"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Neues Passwort eingeben")

            // Update the password for the user with the given ID in the database
            val passwordChanged = changeUserPassword(userId, newPassword)
            if (passwordChanged) {
                call.respond(HttpStatusCode.OK, "Passwort erfolgreich für Nutzer $userId geändert")
            } else {
                call.respond(HttpStatusCode.InternalServerError, "Passwort konnte nicht geändert werden")
            }
        }
    }
}
