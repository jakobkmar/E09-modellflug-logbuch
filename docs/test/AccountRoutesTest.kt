package de.mfcrossendorf.logbook.test

import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.Test
import de.mfcrossendorf.logbook.routes.accountRoutes
import io.ktor.client.request.*
import kotlinx.serialization.json.Json
import de.mfcrossendorf.logbook.CreateAccountRequest
import de.mfcrossendorf.logbook.SharedSessionData
import de.mfcrossendorf.logbook.data.UserSession
import de.mfcrossendorf.logbook.validation.validateAndTrim
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import junit.framework.TestCase.*
import kotlinx.serialization.encodeToString
import kotlin.test.BeforeTest
import io.ktor.serialization.*
import io.ktor.server.testing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlin.test.assertEquals

data class Account(
    val accountId: Int,
    val username: String,
    val firstName: String,
    val lastName: String?,
    val registrationNumber: String?,
    val isAdmin: Boolean,
    val canSeeAllLogs: Boolean,
    val passwordHash: String?
)

val inMemoryAccounts = mutableListOf<Account>()
var nextAccountId = 1

fun Application.testableModule() {
    install(Sessions) {
        cookie<UserSession>("session") {
            serializer = object : SessionSerializer<UserSession> {
                private val json = Json { ignoreUnknownKeys = true }
                override fun serialize(session: UserSession): String = json.encodeToString(session)
                override fun deserialize(text: String): UserSession = json.decodeFromString(text)
            }
        }
    }

    install(ContentNegotiation) {
        json()
    }

    routing {
        post("/account/create") {
            val createRequest = call.receive<CreateAccountRequest>()

            val existingAccount = inMemoryAccounts.find { it.username == createRequest.username }
            if (existingAccount != null) {
                call.respond(HttpStatusCode.Conflict, "Username '${createRequest.username}' is already taken")
                return@post
            }

            val accountId = nextAccountId++
            val newAccount = Account(
                accountId = accountId,
                username = createRequest.username,
                firstName = createRequest.firstName,
                lastName = createRequest.lastName?.ifBlank { null },
                registrationNumber = createRequest.registrationNumber?.ifBlank { null },
                passwordHash = createRequest.password, // Hashing omitted for simplicity
                isAdmin = createRequest.isAdmin,
                canSeeAllLogs = createRequest.canSeeAllLogs
            )
            inMemoryAccounts.add(newAccount)

            call.respond(HttpStatusCode.Created, accountId)
        }
    }
}

// Helper method to create a session
fun createTestSession(userId: Int, username: String): UserSession {
    return UserSession(SharedSessionData(userId, username, true, true))
}

class AccountRoutesTest {

    @Test
    fun `test create account endpoint`() = withTestApplication(Application::testableModule) {
        val json = Json { encodeDefaults = true }

        val requestBody = CreateAccountRequest(
            username = "newUser",
            password = "newPass123",
            firstName = "New",
            lastName = "User",
            registrationNumber = "123456",
            isAdmin = false,
            canSeeAllLogs = false
        )

        val userSession = createTestSession(1, "adminUser")
        val sessionCookie = json.encodeToString(UserSession.serializer(), userSession)

        val response = handleRequest(HttpMethod.Post, "/account/create") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            addHeader(HttpHeaders.Cookie, "session=$sessionCookie")
            setBody(json.encodeToString(CreateAccountRequest.serializer(), requestBody))
        }.response

        assertEquals(HttpStatusCode.Created, response.status())
    }
}
