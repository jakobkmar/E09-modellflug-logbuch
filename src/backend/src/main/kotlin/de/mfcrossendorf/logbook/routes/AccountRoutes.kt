package de.mfcrossendorf.logbook.routes

import de.mfcrossendorf.logbook.AccountResponse
import de.mfcrossendorf.logbook.CreateAccountRequest
import de.mfcrossendorf.logbook.database.awaitList
import de.mfcrossendorf.logbook.database.awaitSingleOrNull
import de.mfcrossendorf.logbook.database.database
import de.mfcrossendorf.logbook.session.adminSessionOrThrow
import de.mfcrossendorf.logbook.session.handleLoginCall
import de.mfcrossendorf.logbook.session.handleLogoutCall
import de.mfcrossendorf.logbook.validation.validateAndTrim
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
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

    authenticate("auth-session") {
        get("/all") {
            call.adminSessionOrThrow()
            val accounts = database.accountQueries.getAllAccounts()
                .awaitList()
                .map { dbAccount ->
                    AccountResponse(
                        userId = dbAccount.account_id,
                        username = dbAccount.username,
                        firstName = dbAccount.first_name,
                        lastName = dbAccount.last_name,
                        isAdminUnsafe = dbAccount.is_admin,
                        registrationNumber = dbAccount.registration_number,
                    )
                }
            call.respond(HttpStatusCode.OK, accounts)
        }

        post("/create") {
            call.adminSessionOrThrow()
            val createRequest = call.receive<CreateAccountRequest>()
                .validateAndTrim()

            val existingAccount = database.accountQueries.getAccountByUsername(createRequest.username)
                .awaitSingleOrNull()
            if (existingAccount != null) {
                call.respond(HttpStatusCode.Conflict, "Username '${createRequest.username}' is already taken")
                return@post
            }

            val accountId = database.accountQueries.createAccount(
                username = createRequest.username,
                first_name = createRequest.firstName,
                last_name = createRequest.lastName?.ifBlank { null },
                registration_number = createRequest.registrationNumber?.ifBlank { null },
                password_hash = createRequest.password?.let { "\$plain\$${it}" },
                is_admin = createRequest.isAdmin,
                can_see_all_logs = createRequest.canSeeAllLogs,
            ).awaitSingleOrNull()

            if (accountId != null) {
                call.respond(HttpStatusCode.Created, accountId)
            } else {
                call.respond(HttpStatusCode.InternalServerError, "Account could not be created")
            }
        }
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
