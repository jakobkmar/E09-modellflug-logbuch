package de.mfcrossendorf.logbook.session

import de.mfcrossendorf.logbook.LoginRequest
import de.mfcrossendorf.logbook.LoginResponse
import de.mfcrossendorf.logbook.data.UserSession
import de.mfcrossendorf.logbook.database.awaitSingleOrNull
import de.mfcrossendorf.logbook.database.database
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import io.ktor.util.pipeline.*
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
import kotlin.time.Duration.Companion.seconds

private val argon2Encoder = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8()

fun Application.configureSessionAuth() {
    val isProduction = !developmentMode

    install(Authentication) {
        session<UserSession>("auth-session") {
            validate { session -> session } // TODO check if session is still valid
            challenge { session ->
                if (session == null) {
                    call.respond(HttpStatusCode.Unauthorized, "Not authenticated")
                } else {
                    call.respond(HttpStatusCode.Unauthorized, "Invalid session")
                }
            }
        }
    }

    install(Sessions) {
        cookie<UserSession>(
            name = "modellflug_session",
            storage = CacheStorage(DatabaseSessionStorage(database), 60.seconds.inWholeMilliseconds)
        ) {
            with(cookie) {
                if (isProduction) {
                    httpOnly = true
                    secure = true
                }
            }
            serializer = UserSession.UserSessionSerializer
            // keep in mind that this might be too long for some id storage implementations
            // (e.g. path length limit for directory storage)
            identity { generateSessionId() + generateSessionId() }
        }
    }
}

suspend fun PipelineContext<*, ApplicationCall>.handleLoginCall() {
    val credentials = call.receive<LoginRequest>()

    val account = database.accountQueries
        .getAccountByUsername(credentials.username)
        .awaitSingleOrNull()

    if (account == null) {
        call.respond(HttpStatusCode.Unauthorized, "Wrong username or password")
        return
    }

    val passwordHash = account.password_hash
        ?: database.accountQueries.setPasswordHash(
            passwordHash = argon2Encoder.encode(credentials.password),
            accountId = account.account_id
        ).awaitSingleOrNull()?.password_hash

    if (passwordHash == null) {
        call.application.log.error("Failed to set password hash for account '${account.account_id}'")
        call.response.status(HttpStatusCode.InternalServerError)
        return
    }
    if (!argon2Encoder.matches(credentials.password, passwordHash)) {
        call.respond(HttpStatusCode.Unauthorized, "Wrong username or password")
        return
    }

    val session = UserSession(
        userId = account.account_id,
        userName = account.username,
        isAdminUnsafe = account.is_admin
    )
    call.sessions.set(session)

    val response = LoginResponse(session.userName, session.isAdminUnsafe)
    call.respond(HttpStatusCode.OK, response)
}

suspend fun PipelineContext<*, ApplicationCall>.handleLogoutCall() {
    val oldSession = call.sessions.get<UserSession>()
    call.sessions.clear<UserSession>()
    if (oldSession != null) {
        call.respond(HttpStatusCode.OK, "Logged out successfully")
    } else {
        call.respond(HttpStatusCode.NotModified, "No session to log out from")
    }
}
