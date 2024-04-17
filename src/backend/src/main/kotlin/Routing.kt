import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.setupRoutes() {

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

    route("/flightlogs") {
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

    route("/admin") {
        // GET request to fetch all flight logs with filtering options
        get("/flightlogs") {
            // Retrieve all flight logs from the database
            val allFlightLogs = getAllFlightLogs()

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
