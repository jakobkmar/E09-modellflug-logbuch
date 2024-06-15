package de.mfcrossendorf.logbook

import de.mfcrossendorf.logbook.data.UserSession
import de.mfcrossendorf.logbook.database.awaitSingleOrNull
import de.mfcrossendorf.logbook.database.database
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder

private val argon2Encoder = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8()

fun Application.configureSessionAuth() {
    val isProduction = !environment.developmentMode

    install(Authentication) {
        form("auth-form") {
            userParamName = "user"
            passwordParamName = "password"
            validate { credentials ->
                val account = database.accountQueries
                    .getAccountByUsername(credentials.name)
                    .awaitSingleOrNull()

                if (account == null)
                    return@validate null
                if (!argon2Encoder.matches(credentials.password, account.password_hash))
                    return@validate null

                UserSession(
                    userId = account.account_id,
                    userName = account.username)
            }
        }
        session<UserSession>("auth-session") {
            validate { session -> session } // TODO check if session is still valid
            challenge {
                call.respondRedirect("/login")
            }
        }
    }

    install(Sessions) {
        cookie<UserSession>(
            name = "user_session",
            storage = SessionStorageMemory()
        ) {
            if (isProduction) {
                cookie.secure = true
            }
            serializer = UserSession.UserSessionSerializer
        }
    }
}
