package de.mfcrossendorf.logbook.data

import io.ktor.server.auth.*
import io.ktor.server.sessions.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class UserSession(
    val userId: Int,
    val userName: String,
) : Principal {

    object UserSessionSerializer : SessionSerializer<UserSession> {
        override fun deserialize(text: String) =
            Json.decodeFromString<UserSession>(text)

        override fun serialize(session: UserSession) =
            Json.encodeToString<UserSession>(session)
    }
}
