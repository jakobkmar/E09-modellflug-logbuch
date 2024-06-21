package de.mfcrossendorf.logbook.session

import io.ktor.http.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.util.date.*

sealed class SessionAuthException(val msg: String) : Exception(msg) {

    class InvalidCredentials(msg: String = "Wrong username or password") : SessionAuthException(msg)
    class NotAuthenticated(msg: String = "Not authenticated") : SessionAuthException(msg)
    class InvalidSession(msg: String = "Invalid session") : SessionAuthException(msg)
    class UnknownUser(msg: String = "Unknown user") : SessionAuthException(msg)
    class InsufficientPermissions(msg: String = "Insufficient permissions") : SessionAuthException(msg)
    class NoAdmin(msg: String = "Admin rights required") : SessionAuthException(msg)
}

fun StatusPagesConfig.sessionAuthExceptionHandler() {
    exception<SessionAuthException> { call, cause ->
        when (cause) {
            is SessionAuthException.InvalidCredentials -> call.respond(HttpStatusCode.Unauthorized, cause.msg)
            is SessionAuthException.UnknownUser -> call.respond(HttpStatusCode.Unauthorized, cause.msg)
            is SessionAuthException.NoAdmin -> call.respond(HttpStatusCode.Forbidden, cause.msg)
            is SessionAuthException.InsufficientPermissions -> call.respond(HttpStatusCode.Forbidden, cause.msg)
            is SessionAuthException.NotAuthenticated -> call.respond(HttpStatusCode.Unauthorized, cause.msg)
            is SessionAuthException.InvalidSession -> {
                call.response.cookies.append(Cookie(
                    name = sessionCookieName,
                    value = "",
                    path = "/",
                    maxAge = 0,
                    expires = GMTDate.START,
                    secure = true,
                    httpOnly = true,
                ))
                call.respond(HttpStatusCode.Unauthorized, cause.msg)
            }
        }
    }
}
