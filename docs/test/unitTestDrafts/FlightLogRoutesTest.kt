package de.mfcrossendorf.logbook.test
import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.Test
import kotlinx.serialization.json.Json
import de.mfcrossendorf.logbook.data.UserSession
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import kotlinx.serialization.encodeToString
import kotlin.test.assertEquals
import kotlinx.datetime.*
import de.mfcrossendorf.logbook.CreateFlightLogRequest
import kotlinx.serialization.Serializable
import java.lang.reflect.Array.set


@Serializable
data class CreateFlightLogRequest(
    val date: LocalDate,
    val flightStart: LocalTime,
    val flightEnd: LocalTime?,
    val signature: String,
    val checkedFirstAid: Boolean,
    val modelType: String
)

@Serializable
data class Flight(
    val flightId: Int,
    val accountId: Int,
    val date: LocalDate,
    val flightStart: LocalTime,
    val flightEnd: LocalTime?,
    val checkedFirstAid: Boolean,
    val modelType: String,
    val signature: ByteArray,
    val remarks: String?
)

val inMemoryFlights = mutableListOf<Flight>()
var nextFlightId = 1

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }

    routing {
        post("/flightlog/create") {
            val createRequest = call.receive<CreateFlightLogRequest>()
            val flight = Flight(
                flightId = nextFlightId++,
                accountId = 1, // Static user ID for simplicity
                date = createRequest.date,
                flightStart = createRequest.flightStart,
                flightEnd = createRequest.flightEnd,
                checkedFirstAid = createRequest.checkedFirstAid,
                modelType = createRequest.modelType,
                signature = createRequest.signature.toByteArray(),
                remarks = null
            )
            inMemoryFlights.add(flight)
            call.respond(HttpStatusCode.Created, flight.flightId)
        }
    }
}
class FlightLogRoutesTest {

    private val json = Json { encodeDefaults = true }

    @Test
    fun `test create flight log endpoint`() = withTestApplication(Application::module) {
        val requestBody = CreateFlightLogRequest(
            date = LocalDate(2023, 6, 30),
            flightStart = LocalTime(14, 0),
            flightEnd = LocalTime(15, 0),
            signature = "testSignature",
            checkedFirstAid = true,
            modelType = "testModel"
        )

        with(handleRequest(HttpMethod.Post, "/flightlog/create") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(json.encodeToString(CreateFlightLogRequest.serializer(), requestBody))
        }) {
            assertEquals(HttpStatusCode.Created, response.status())
            // Check the response content if needed
        }
    }
}
