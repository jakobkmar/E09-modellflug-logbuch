package de.mfcrossendorf.logbook.session

import de.mfcrossendorf.logbook.data.UserSession
import de.mfcrossendorf.logbook.database.awaitSingleOrNull
import de.mfcrossendorf.logbook.database.database
import io.ktor.server.application.*
import io.ktor.server.sessions.*

fun ApplicationCall.sessionOrThrow(): UserSession {
    return sessions.get<UserSession>()
        ?: throw SessionAuthException.Unauthorized.NotAuthenticated()
}

suspend fun ApplicationCall.adminSessionOrThrow(): UserSession {
    val session = sessionOrThrow()
    if (!session.isAdminUnsafe) {
        throw SessionAuthException.NoAdmin()
    }
    val isAdmin = database.accountQueries.checkAdminById(session.userId).awaitSingleOrNull()
        ?: throw SessionAuthException.UnknownUser()
    if (!isAdmin) {
        throw SessionAuthException.NoAdmin()
    }
    return session
}
