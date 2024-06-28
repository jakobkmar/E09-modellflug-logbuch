package de.mfcrossendorf.logbook

import de.mfcrossendorf.logbook.config.ConfigManager
import de.mfcrossendorf.logbook.database.database
import de.mfcrossendorf.logbook.database.driver
import de.mfcrossendorf.logbook.db.Database
import de.mfcrossendorf.logbook.routes.*
import de.mfcrossendorf.logbook.session.configureSessionAuth
import de.mfcrossendorf.logbook.session.configureSessionCookie
import de.mfcrossendorf.logbook.session.sessionAuthExceptionHandler
import de.mfcrossendorf.logbook.validation.validationExceptionHandler
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.websocket.*
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import org.postgresql.util.PSQLException
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

fun main() {
    // load configuration from file
    ConfigManager.loadConfig()

    // connect to the database
    database

    // create the database schema if it doesn't exist TODO use migrations
    try {
        Database.Schema.create(database.driver)
    } catch (exc: PSQLException) {
        println("Database schema already exists: '${exc::class.simpleName}', '${exc.message}'")
    }

    @Suppress("UNUSED_VARIABLE") // only for testing purposes
    val delayPlugin = createApplicationPlugin(name = "DelayPlugin") {
        onCall { _ ->
            delay(2.seconds)
        }
    }

    // configure and start the server
    embeddedServer(
        factory = Netty, port = ConfigManager.config.server.port,

    ) {
        log.info("Running application in ${if (ConfigManager.isDevelopmentMode) "development" else "production"} mode")

        install(ContentNegotiation) {
            json()
        }
        install(StatusPages) {
            sessionAuthExceptionHandler()
            validationExceptionHandler()
        }
        if (ConfigManager.isDevelopmentMode) {
            install(CORS) {
                allowHost("localhost:${ConfigManager.config.server.port}")
                allowHost("127.0.0.1:${ConfigManager.config.server.port}")
                allowHost("localhost:5173") // vue / vite dev server
                allowHeader(HttpHeaders.ContentType)
                allowCredentials = true
                HttpMethod.DefaultMethods.forEach(::allowMethod)
            }
        }
        install(Authentication) {
            configureSessionAuth()
        }
        install(Sessions) {
            configureSessionCookie()
        }
        install(WebSockets) {
            contentConverter = KotlinxWebsocketSerializationConverter(Json)
            pingPeriodMillis = 10.seconds.inWholeMilliseconds
            timeoutMillis = 1.minutes.inWholeMilliseconds
        }

        routing {
            route("/api/v1/") {
                accountRoutes()
                flightDirectorRoutes()
                flightLogRoutes()
                appStateRoutes()
                protocolRoutes()
            }
            singlePageApplication {
                useResources = true
                filesPath = "frontend"
                defaultPage = "index.html"
            }
        }
    }.start(wait = true)
}
