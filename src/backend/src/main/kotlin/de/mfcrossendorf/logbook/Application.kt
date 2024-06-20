package de.mfcrossendorf.logbook

import de.mfcrossendorf.logbook.database.database
import de.mfcrossendorf.logbook.database.driver
import de.mfcrossendorf.logbook.db.Database
import de.mfcrossendorf.logbook.routes.accountRoutes
import de.mfcrossendorf.logbook.routes.flightDirectorRoutes
import de.mfcrossendorf.logbook.routes.flightLogRoutes
import de.mfcrossendorf.logbook.session.configureSessionAuth
import de.mfcrossendorf.logbook.session.configureSessionCookie
import de.mfcrossendorf.logbook.session.sessionAuthExceptionHandler
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.coroutines.delay
import org.postgresql.util.PSQLException
import kotlin.time.Duration.Companion.seconds

fun main() {
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
    embeddedServer(Netty, port = 8080) {
        val isProduction = !developmentMode
        log.info("Running application in ${if (isProduction) "production" else "development"} mode")

        install(ContentNegotiation) {
            json()
        }
        install(StatusPages) {
            sessionAuthExceptionHandler()
        }
        if (developmentMode) {
            install(CORS) {
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
            configureSessionCookie(isProduction)
        }

        routing {
            route("/api/v1/") {
                accountRoutes()
                flightDirectorRoutes()
                flightLogRoutes()
            }
        }
    }.start(wait = true)
}
