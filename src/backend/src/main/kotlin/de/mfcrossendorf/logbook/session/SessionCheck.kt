package de.mfcrossendorf.logbook.session

import de.mfcrossendorf.logbook.data.UserSession
import de.mfcrossendorf.logbook.database.awaitSingleOrNull
import de.mfcrossendorf.logbook.database.database
import io.ktor.server.application.*
import io.ktor.server.sessions.*

fun ApplicationCall.sessionOrThrow(): UserSession {
    return sessions.get<UserSession>()
        ?: throw SessionAuthException.NotAuthenticated()
}

suspend fun ApplicationCall.adminSessionOrThrow(): UserSession {
    val session = sessionOrThrow()
    if (!session.sharedData.isAdminUnsafe) {
        throw SessionAuthException.NoAdmin()
    }
    val isAdmin = database.accountQueries.checkAdminById(session.sharedData.userId)
        .awaitSingleOrNull() ?: throw SessionAuthException.UnknownUser()
    if (!isAdmin) {
        throw SessionAuthException.NoAdmin()
    }
    return session
}

suspend fun ApplicationCall.canSeeAllLogsSessionOrThrow(): UserSession {
    val session = sessionOrThrow()
    if (!session.sharedData.canSeeAllLogsUnsafe && !session.sharedData.isAdminUnsafe) {
        throw SessionAuthException.InsufficientPermissions()
    }
    val canSeeAllLogs = database.accountQueries.checkCanSeeAllLogsById(session.sharedData.userId)
        .awaitSingleOrNull() ?: throw SessionAuthException.UnknownUser()
    val isAdmin = database.accountQueries.checkAdminById(session.sharedData.userId)
        .awaitSingleOrNull() ?: throw SessionAuthException.UnknownUser()
    if (!canSeeAllLogs && !isAdmin) {
        throw SessionAuthException.InsufficientPermissions()
    }
    return session
}
