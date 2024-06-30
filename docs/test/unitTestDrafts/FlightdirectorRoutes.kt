package de.mfcrossendorf.logbook.test

import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.Test
import de.mfcrossendorf.logbook.routes.accountRoutes
import io.ktor.client.request.*
import kotlinx.serialization.json.Json
import de.mfcrossendorf.logbook.CreateAccountRequest
import de.mfcrossendorf.logbook.CurrentFlightDirectorResponse
import de.mfcrossendorf.logbook.LoginRequest
import de.mfcrossendorf.logbook.SharedSessionData
import de.mfcrossendorf.logbook.data.UserSession
import de.mfcrossendorf.logbook.db.Database
import de.mfcrossendorf.logbook.routes.flightDirectorRoutes
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import io.ktor.util.*
import junit.framework.TestCase.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import java.time.Clock
import java.time.LocalDate
import java.time.LocalTime
import kotlin.test.assertEquals


data class LoginRequest(val username: String, val password: String)

@Serializable
data class ResponseData(val success: Boolean, val message: String)

fun Application.testModule() {
    install(ContentNegotiation) {
        json(Json { encodeDefaults = true })
    }
    routing {
        post("/flightdirector/login") {
            val loginRequest = call.receive<LoginRequest>()
            if (loginRequest.username == "director" && loginRequest.password == "pass123") {
                call.respond(HttpStatusCode.OK, ResponseData(true, "Director logged in successfully"))
            } else {
                call.respond(HttpStatusCode.Unauthorized, ResponseData(false, "Invalid credentials"))
            }
        }
    }
}
class FlightDirectorRoutesTest {

    @Test
    fun `test flight director login`() = testApplication {
        // Set the application module for testing
        application { testModule() }

        val json = Json { encodeDefaults = true }

        // Create a request body
        val requestBody = LoginRequest(username = "director", password = "pass123")
        val serializedBody = json.encodeToString(LoginRequest.serializer(), requestBody)

        // Execute the POST request
        val response = client.post("/flightdirector/login") {
            contentType(ContentType.Application.Json)
            setBody(serializedBody)
        }

        // Assert the response
        assertEquals(HttpStatusCode.OK, response.status, "Status code should be OK.")
        val responseContent = response.bodyAsText()
        val expectedContent = json.encodeToString(ResponseData.serializer(), ResponseData(true, "Director logged in successfully"))
        assertEquals(expectedContent, responseContent, "Response content should match expected JSON.")
    }
}
