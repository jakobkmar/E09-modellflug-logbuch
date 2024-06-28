package de.mfcrossendorf.logbook.routes

import de.mfcrossendorf.logbook.AccountResponse
import de.mfcrossendorf.logbook.CreateAccountRequest
import de.mfcrossendorf.logbook.database.awaitList
import de.mfcrossendorf.logbook.database.awaitSingleOrNull
import de.mfcrossendorf.logbook.database.database
import de.mfcrossendorf.logbook.session.adminSessionOrThrow
import de.mfcrossendorf.logbook.session.argon2Encoder
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
    // Login call for non-authenticated users
    post("/login") {
        handleLoginCall()
    }
    // Logout call for any users
    post("/logout") {
        handleLogoutCall()
    }

    authenticate("auth-session") {
        // Get all accounts, only for admins
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
                        registrationNumber = dbAccount.registration_number,
                        isAdminUnsafe = dbAccount.is_admin,
                        canSeeAllLogsUnsafe = dbAccount.can_see_all_logs,
                    )
                }
            call.respond(HttpStatusCode.OK, accounts)
        }

        // Create a new account, only for admins
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
                password_hash = createRequest.password?.let { argon2Encoder.encode(it) },
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
}
