package de.mfcrossendorf.logbook.session

import io.ktor.http.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

sealed class SessionAuthException(val msg: String) : Exception(msg) {

    class InvalidCredentials(msg: String = "Wrong username or password") : SessionAuthException(msg)

    sealed class Unauthorized(msg: String = "Not authenticated or invalid session") : SessionAuthException(msg) {
        class NotAuthenticated(msg: String = "Not authenticated") : Unauthorized(msg)
        class InvalidSession(msg: String = "Invalid session") : Unauthorized(msg)
    }

    class UnknownUser(msg: String = "Unknown user") : SessionAuthException(msg)
    class NoAdmin(msg: String = "Admin rights required") : SessionAuthException(msg)
}

fun StatusPagesConfig.sessionAuthExceptionHandler() {
    exception<SessionAuthException> { call, cause ->
        when (cause) {
            is SessionAuthException.InvalidCredentials -> call.respond(HttpStatusCode.Unauthorized, cause.msg)
            is SessionAuthException.UnknownUser -> call.respond(HttpStatusCode.Unauthorized, cause.msg)
            is SessionAuthException.NoAdmin -> call.respond(HttpStatusCode.Forbidden, cause.msg)
            is SessionAuthException.Unauthorized -> call.respond(HttpStatusCode.Unauthorized, cause.msg)
        }
    }
}
