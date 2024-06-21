package de.mfcrossendorf.logbook.data

import de.mfcrossendorf.logbook.SharedSessionData
import de.mfcrossendorf.logbook.session.SessionAuthException
import io.ktor.server.auth.*
import io.ktor.server.sessions.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class UserSession(val sharedData: SharedSessionData) : Principal {

    object UserSessionSerializer : SessionSerializer<UserSession> {
        private val sessionJson = Json {
            ignoreUnknownKeys = true
        }

        override fun deserialize(text: String ) = try {
            sessionJson.decodeFromString<UserSession>(text)
        } catch (exc: SerializationException) {
            throw SessionAuthException.InvalidSession("Invalid or corrupted session data")
        }

        override fun serialize(session: UserSession) =
            sessionJson.encodeToString<UserSession>(session)
    }
}
