package de.mfcrossendorf.logbook

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
import kotlin.io.path.Path

private val argon2Encoder = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8()

fun Application.configureSessionAuth() {
    val isProduction = !environment.developmentMode

    install(Authentication) {
        session<UserSession>("auth-session") {
            validate { session -> session } // TODO check if session is still valid
            challenge {
                call.respondRedirect("/login")
            }
        }
    }

    install(Sessions) {
        cookie<UserSession>(
            name = "modellflug_session",
            storage = directorySessionStorage(Path("data/sessions").toFile(), cached = true) // TODO use better provider
        ) {
            if (isProduction) {
                cookie.secure = true
            }
            serializer = UserSession.UserSessionSerializer
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
