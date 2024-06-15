package de.mfcrossendorf.logbook

import de.mfcrossendorf.logbook.database.database
import de.mfcrossendorf.logbook.database.driver
import de.mfcrossendorf.logbook.db.Database
import de.mfcrossendorf.logbook.routes.accountRoutes
import de.mfcrossendorf.logbook.routes.flightDirectorRoutes
import de.mfcrossendorf.logbook.routes.flightLogRoutes
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import org.postgresql.util.PSQLException

fun main() {
    // connect to the database
    database

    // create the database schema if it doesn't exist TODO use migrations
    try {
        Database.Schema.create(database.driver)
    } catch (exc: PSQLException) {
        println("Database schema already exists: '${exc::class.simpleName}', '${exc.message}'")
    }

    // configure and start the server
    embeddedServer(Netty, port = 8080) {
        val isProduction = !environment.developmentMode
        log.info("Running application in ${if (isProduction) "production" else "development"} mode")

        configureSessionAuth()

        install(ContentNegotiation) {
            json()
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
