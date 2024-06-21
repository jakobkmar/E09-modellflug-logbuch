package de.mfcrossendorf.logbook.session

import de.mfcrossendorf.logbook.LoginRequest
import de.mfcrossendorf.logbook.SharedSessionData
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

const val sessionCookieName = "modellflug_session"

private val argon2Encoder = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8()

fun AuthenticationConfig.configureSessionAuth() {
    session<UserSession>("auth-session") {
        validate { session -> UserIdPrincipal(session.sharedData.userId.toString()) }
        challenge { session ->
            if (session == null) {
                throw SessionAuthException.NotAuthenticated()
            } else {
                throw SessionAuthException.InvalidSession()
            }
        }
    }
}

fun SessionsConfig.configureSessionCookie(isProduction: Boolean) {
    cookie<UserSession>(
        name = sessionCookieName,
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

suspend fun PipelineContext<*, ApplicationCall>.handleLoginCall() {
    val credentials = call.receive<LoginRequest>()

    val account = database.accountQueries
        .getAccountByUsername(credentials.username)
        .awaitSingleOrNull()

    if (account == null) {
        throw SessionAuthException.InvalidCredentials()
    }

    val rawPasswordHash = account.password_hash

    val passwordHash = when {
        // no password set for this account yet
        rawPasswordHash == null -> {
            argon2Encoder.encode(credentials.password)
        }
        // plain text set by admin
        rawPasswordHash.startsWith("\$plain\$") -> {
            argon2Encoder.encode(rawPasswordHash.removePrefix("\$plain\$"))
        }
        // old hash format
        argon2Encoder.upgradeEncoding(rawPasswordHash) -> {
            // only upgrade the hash if the password matches the old hash
            if (!argon2Encoder.matches(credentials.password, rawPasswordHash)) {
                throw SessionAuthException.InvalidCredentials()
            }
            argon2Encoder.encode(credentials.password)
        }
        // standard argon2 hash
        else -> rawPasswordHash
    }

    if (passwordHash != rawPasswordHash) {
        val setHashResult = database.accountQueries.setPasswordHash(
            password_hash = passwordHash,
            account_id = account.account_id
        ).awaitSingleOrNull()?.password_hash
        if (setHashResult != passwordHash) {
            call.application.log.error("Failed to get or set password hash for account '${account.account_id}'")
            call.respond(HttpStatusCode.InternalServerError, "Login failed")
            return
        }
    }

    if (!argon2Encoder.matches(credentials.password, passwordHash)) {
        throw SessionAuthException.InvalidCredentials()
    }

    val session = UserSession(SharedSessionData(
        userId = account.account_id,
        username = account.username,
        isAdminUnsafe = account.is_admin,
        canSeeAllLogsUnsafe = account.can_see_all_logs,
    ))
    call.sessions.set(session)

    call.respond(HttpStatusCode.OK, session.sharedData)
}

suspend fun PipelineContext<*, ApplicationCall>.handleLogoutCall() {
    val oldSession = call.sessions.get<UserSession>()
    call.sessions.clear<UserSession>()
    if (oldSession != null) {
        call.respond(HttpStatusCode.OK, "Logged out successfully")
    } else {
        call.respond(HttpStatusCode.OK, "No session to log out from")
    }
}
